package taxi.eskar.eskartaxi.ui.profileedit.driver

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.results.DriverResult
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.ui.Results
import javax.inject.Inject

@InjectViewState
class ProfileDriverEditPresenter @Inject constructor(
        private val profileRepository: ProfileRepository, router: Router
) : BasePresenter<ProfileDriverEditView>(router) {

    private var updateProfileDisposable: Disposable? = null

    private lateinit var driver: Driver

    init {
        profileRepository.getDriverMe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnSubscribe(this::unsubscribeOnDetach)
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe({
                    if (it is DriverResult.Success) {
                        driver = it.driver
                        viewState.showProfile(driver)
                    }
                }, this::processError)
    }

    fun onLastNameChanged(nameLast: String) {
        driver.surname = nameLast
    }

    fun onFirstNameChanged(nameFirst: String) {
        driver.name = nameFirst
    }

    fun onCarModelChanged(carModel: String) {
        driver.carModel = carModel
    }

    fun onCarColorChanged(carColor: String) {
        driver.carColor = carColor
    }

    fun onLicensePlateChanged(licensePlate: String) {
        driver.licencePlate = licensePlate
    }

    fun onSaveClicked() {
        if (updateProfileDisposable != null) {
            router.showSystemMessage("Профиль обновляется, ждите!")
            return
        }

        if (driver.isNotRegistered()) {
            router.showSystemMessage("Все поля пользователя должны быть заполнены!")
            return
        }

        profileRepository.updateDriver(driver)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { updateProfileDisposable = it }
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> updateProfileDisposable = null }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processUpdateDriverResult)
    }

    private fun processUpdateDriverResult(result: DriverResult) {
        when (result) {
            is DriverResult.Success ->
                router.exitWithResult(Results.PROFILE_EDIT_SAVED_DRIVER, driver)
            is DriverResult.Failure -> this.processError(result.throwable)
        }
    }
}