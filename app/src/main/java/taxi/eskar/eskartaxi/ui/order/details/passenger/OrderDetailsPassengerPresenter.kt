package taxi.eskar.eskartaxi.ui.order.details.passenger

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.results.DriverResult
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository

@InjectViewState
class OrderDetailsPassengerPresenter(
        private val order: Order,
        private val profileRepository: ProfileRepository,
        router: Router
) : BasePresenter<OrderDetailsPassengerView>(router) {

    init {
        viewState.showOrder(order)
        this.fetchDriverAndShow()
    }


    private fun fetchDriverAndShow() {
        profileRepository.getDriver(order.driverId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processDriverAndShow, this::processError)
    }

    private fun processDriverAndShow(driverResult: DriverResult) {
        when (driverResult) {
            is DriverResult.Success -> {
                viewState.showDriver(driverResult.driver)
            }
            is DriverResult.Failure -> {
                viewState.showDriverEmpty()
                this.processError(driverResult.throwable)
            }
        }
    }

}