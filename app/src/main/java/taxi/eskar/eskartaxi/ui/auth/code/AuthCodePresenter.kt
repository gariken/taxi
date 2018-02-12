package taxi.eskar.eskartaxi.ui.auth.code

import com.arellomobile.mvp.InjectViewState
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.results.ConfirmAuthResult
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.ui.Screens

@InjectViewState
class AuthCodePresenter constructor(
        private val mode: Int, private val phone: String,
        private val profileRepository: ProfileRepository,
        router: Router
) : BasePresenter<AuthCodeView>(router) {

    private val codeChanges = PublishRelay.create<String>()
    private val proceedRequestsPassenger = PublishRelay.create<Unit>()
    private val proceedRequestsDriver = PublishRelay.create<Unit>()

    private val unitCodeToCode = BiFunction<Unit, String, String> { _, phone -> phone }

    private var maskFilled = false

    init {
        codeChanges.doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe({
                    viewState.showButtonsEnabled(maskFilled)
                }, this::processError)

        codeChanges.accept("")

        proceedRequestsPassenger
                .observeOn(Schedulers.io())
                .withLatestFrom(codeChanges, unitCodeToCode)
                .flatMapSingle(this::confirmAuthPassenger)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processConfirmAuthResultPassenger, this::processError)

        proceedRequestsDriver
                .observeOn(Schedulers.io())
                .withLatestFrom(codeChanges, unitCodeToCode)
                .flatMapSingle(this::confirmAuthDriver)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processConfirmAuthResultDriver, this::processError)
    }


    fun onCodeChanged(maskFilled: Boolean, code: String) {
        this.maskFilled = maskFilled
        codeChanges.accept(code)
    }

    fun onProceedButtonClicked(unit: Unit) = when (mode) {
        AuthMode.PASSENGER -> proceedRequestsPassenger.accept(unit)
        AuthMode.DRIVER -> proceedRequestsDriver.accept(Unit)
        else -> throw RuntimeException("Unexpected AuthMode value is $mode")
    }

    private fun confirmAuthPassenger(code: String): Single<ConfirmAuthResult> {
        return profileRepository.confirmAuthWithCodePassenger(phone, code)
    }

    private fun confirmAuthDriver(code: String): Single<ConfirmAuthResult> {
        return profileRepository.confirmAuthWithCodeDriver(phone, code)
    }

    private fun processConfirmAuthResultPassenger(result: ConfirmAuthResult) {
        when (result) {
            is ConfirmAuthResult.SuccessPassengerOld ->
                router.newRootScreen(Screens.SPLASH, result.passenger)
            is ConfirmAuthResult.SuccessPassengerNew ->
                router.newRootScreen(Screens.SPLASH, result.passenger)
            is ConfirmAuthResult.Fail -> {
                router.showSystemMessage("Ошибка - неправильный код или плохое соединение")
                this.processError(result.throwable)
            }
        }
    }

    private fun processConfirmAuthResultDriver(result: ConfirmAuthResult) {
        when (result) {
            is ConfirmAuthResult.SuccessDriverOld ->
                router.newRootScreen(Screens.SPLASH, result.driver)
            is ConfirmAuthResult.SuccessDriverNew ->
                router.newRootScreen(Screens.SPLASH, result.driver)
            is ConfirmAuthResult.Fail -> {
                router.showSystemMessage("Ошибка - неправильный код или плохое соединение")
            }
        }
    }
}