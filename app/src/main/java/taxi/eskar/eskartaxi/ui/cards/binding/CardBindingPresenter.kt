package taxi.eskar.eskartaxi.ui.cards.binding

import com.arellomobile.mvp.InjectViewState
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.business.cardbinding.CardBindingInteractor
import taxi.eskar.eskartaxi.data.model.CardInfo
import taxi.eskar.eskartaxi.data.model.results.BindResult
import taxi.eskar.eskartaxi.data.resources.MessageResource
import taxi.eskar.eskartaxi.ui.Screens
import javax.inject.Inject

@InjectViewState
class CardBindingPresenter @Inject constructor(
        private val interactor: CardBindingInteractor,
        private val messageResource: MessageResource,
        router: Router
) : BasePresenter<CardBindingView>(router) {

    private val bindRequests = PublishRelay.create<Unit>()

    // region data changes

    private val cardNumChanges = PublishRelay.create<Pair<Boolean, String>>()
    private val cardExpChanges = PublishRelay.create<Pair<Boolean, String>>()
    private val cardCvvChanges = PublishRelay.create<Pair<Boolean, String>>()
    private val cardOwnChanges = PublishRelay.create<String>()

    // endregion

    private val cardChanges = Observables.combineLatest(
            cardNumChanges.map { it.second },
            cardExpChanges.map { it.second },
            cardCvvChanges.map { it.second },
            cardOwnChanges, this::combineCard)

    init {
        this.subscribeToFilledChanges()
        this.subscribeToBindRequests()
    }

    // region view

    fun onCardNumChanged(filled: Boolean, number: String) {
        cardNumChanges.accept(Pair(filled, number))
    }

    fun onCardExpChanged(filled: Boolean, expiration: String) {
        cardExpChanges.accept(Pair(filled, expiration))
    }

    fun onCardCvvChanged(filled: Boolean, cvv: String) {
        cardCvvChanges.accept(Pair(filled, cvv))
    }

    fun onCardOwnChanged(owner: String) {
        cardOwnChanges.accept(owner)
    }

    fun onBindCardClicked() {
        bindRequests.accept(Unit)
    }

    // endregion


    // region private

    private fun subscribeToFilledChanges() {
        val numFilledChanges = cardNumChanges.map { it.first }.startWith(false)
        val expFilledChanges = cardExpChanges.map { it.first }.startWith(false)
        val cvvFilledChanges = cardCvvChanges.map { it.first }.startWith(false)
        val filledChanges = Observables.combineLatest(
                numFilledChanges, expFilledChanges, cvvFilledChanges, this::combineFilled)

        filledChanges
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(viewState::showBindingButton, this::processError)
    }

    private fun combineFilled(b1: Boolean, b2: Boolean, b3: Boolean): Boolean = b1 && b2 && b3


    private fun subscribeToBindRequests() {
        bindRequests
                .withLatestFrom(cardChanges, BiFunction<Unit, CardInfo, CardInfo> { _, card -> card })
                .doOnNext {
                    viewState.showLoading(true)
                    viewState.hideKeyboard()
                }
                .observeOn(Schedulers.io())
                .flatMapSingle(interactor::bindCard)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { viewState.showLoading(false) }
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .subscribe(this::processBindCardResult, this::processError)
    }

    private fun combineCard(num: String, exp: String, cvv: String, own: String): CardInfo =
            CardInfo(num, exp, cvv, own)

    private fun processBindCardResult(result: BindResult) {
        when (result) {
            is BindResult.Success3dsD -> {
                viewState.showSystemMessage(messageResource.cardBindingSuccess())
                router.exit()
            }
            is BindResult.Success3dsE -> router.navigateTo(Screens.THREE_D_SECURE, result)
            is BindResult.Code -> viewState.showSystemMessage(messageResource.error(result.code))
            is BindResult.DataError ->
                viewState.showSystemMessage(messageResource.cardBindingDataError())
            is BindResult.Failure -> {
                viewState.showSystemMessage(result.throwable.localizedMessage)
            }
        }
    }

    // endregion
}