package taxi.eskar.eskartaxi.ui.auth.phone

import com.arellomobile.mvp.InjectViewState
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.results.RequestSmsResult
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.ui.Screens
import javax.inject.Inject

@InjectViewState
class AuthPhonePresenter @Inject constructor(
        private val profileRepository: ProfileRepository,
        router: Router
) : BasePresenter<AuthPhoneView>(router) {

    private val phoneChages = PublishRelay.create<String>()
    private val nextRequestsPassenger = PublishRelay.create<Unit>()
    private val nextRequestsDriver = PublishRelay.create<Unit>()

    private val unitPhoneToPhone = BiFunction<Unit, String, String> { _, phone -> phone }

    private var maskFilled = false


    init {
        phoneChages.doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({
                    viewState.showButtonsEnabled(maskFilled)
                }, this::processError)

        nextRequestsPassenger
                .doOnNext { viewState.showLoading(true) }.observeOn(Schedulers.io())
                .withLatestFrom(phoneChages, unitPhoneToPhone)
                .flatMapSingle { profileRepository.requestSmsCodePassenger(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { viewState.showLoading(false) }
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({
                    when (it) {
                        is RequestSmsResult.Success -> {
                            router.navigateTo(Screens.AUTH_CODE_PASSENGER, it.phone)
                        }
                        is RequestSmsResult.Fail -> {
                            router.showSystemMessage(it.throwable.message)
                        }
                    }
                }, this::processError)

        nextRequestsDriver
                .doOnNext { viewState.showLoading(true) }.observeOn(Schedulers.io())
                .withLatestFrom(phoneChages, unitPhoneToPhone)
                .flatMapSingle { profileRepository.requestSmsCodeDriver(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { viewState.showLoading(false) }
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({
                    when (it) {
                        is RequestSmsResult.Success -> {
                            router.navigateTo(Screens.AUTH_CODE_DRIVER, it.phone)
                        }
                        is RequestSmsResult.Fail -> {
                            router.showSystemMessage(it.throwable.message)
                        }
                    }
                }, this::processError)

        phoneChages.accept("")
    }


    fun onPhoneChanged(maskFilled: Boolean, phone: String) {
        this.maskFilled = maskFilled
        phoneChages.accept(phone)
    }

    fun onNextButtonClicked(unit: Unit) {
        nextRequestsPassenger.accept(unit)
    }

    fun onNextDriverButtonClicked(unit: Unit) {
        nextRequestsDriver.accept(unit)
    }
}