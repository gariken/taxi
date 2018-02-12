package taxi.eskar.eskartaxi.ui.splash

import com.arellomobile.mvp.InjectViewState
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.business.splash.SplashInteractor
import taxi.eskar.eskartaxi.business.splash.SyncResult
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.repository.location.LocationProvidersUnavailableException
import taxi.eskar.eskartaxi.ui.Screens
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@InjectViewState
class SplashPresenter @Inject constructor(
        private val interactor: SplashInteractor, router: Router
) : BasePresenter<SplashView>(router) {

    private val syncRequests = PublishRelay.create<Unit>()
    private val noSyncInProgress = AtomicBoolean(true)

    init {
        syncRequests.filter { noSyncInProgress.get() }
                .doOnNext { this.doBeforeSync() }
                .flatMapSingle { interactor.sync() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { this.doAfterSync() }
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processSyncResult, this::processError)
    }

    fun onPermissionsGranted() {
        syncRequests.accept(Unit)
    }

    fun onPermissionsDenied() {
        router.exit()
    }

    fun onLocationSettingsClicked() {
        router.navigateTo(Screens.SETTINGS_LOCATION)
    }

    fun onRetryClicked() {
        viewState.showIOError(false)
        viewState.showLoading(true)
        syncRequests.accept(Unit)
    }

    fun onSignOutClicked() {
        interactor.signout()
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processSyncResult, this::processError)
    }

    private fun processSyncResult(result: SyncResult) = when (result) {
        is SyncResult.Driver -> this.resolveScreenDriver(result.driver, result.order)
        is SyncResult.Passenger -> this.resolveScreenPassenger(result.passenger, result.order)
        is SyncResult.None -> router.newRootScreen(Screens.AUTH_PHONE)
        is SyncResult.Banned -> viewState.showBannedError(true)
        is SyncResult.Unauthorized -> viewState.showAuthError(true)
        is SyncResult.UnknownStatusCode -> viewState.showAuthError(true)
        is SyncResult.Error -> this.showSyncError(result.throwable)
    }

    private fun resolveScreenPassenger(passenger: Passenger, order: Order?) {
        if (passenger.isRegistered()) {
            if (order == null) {
                router.newRootScreen(Screens.START_PASSENGER)
            } else router.newRootScreen(Screens.ORDER_PROGRESS_PASSENGER, order)
        } else router.newRootScreen(Screens.REG_PASSENGER, passenger)
    }

    private fun resolveScreenDriver(driver: Driver, order: Order?) {
        if (driver.isRegistered()) {
            if (order == null) {
                router.newRootScreen(Screens.START_DRIVER)
            } else router.newRootScreen(Screens.ORDER_PROGRESS_DRIVER, order)
        } else router.newRootScreen(Screens.REG_DRIVER, driver)
    }

    private fun doBeforeSync() {
        viewState.showAuthError(false)
        viewState.showBannedError(false)
        viewState.showIOError(false)
        viewState.showLocationError(false)

        viewState.showLoading(true)
        noSyncInProgress.set(false)
    }

    private fun doAfterSync() {
        viewState.showLoading(false)
        noSyncInProgress.set(true)
    }

    private fun showSyncError(throwable: Throwable) {
        when (throwable) {
            is IOException -> viewState.showIOError(true)
            is LocationProvidersUnavailableException -> viewState.showLocationError(true)
            else -> this.processError(throwable)
        }
    }
}