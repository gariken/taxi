package taxi.eskar.eskartaxi.presentation.cards

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.business.cards.CardsInteractor
import taxi.eskar.eskartaxi.data.model.Card
import taxi.eskar.eskartaxi.data.model.results.CardsResult
import taxi.eskar.eskartaxi.data.model.results.UnbindResult
import taxi.eskar.eskartaxi.data.resources.MessageResource
import taxi.eskar.eskartaxi.ui.Screens
import javax.inject.Inject

@InjectViewState class CardsPresenter @Inject constructor(
        private val interactor: CardsInteractor,
        private val messages: MessageResource, router: Router
) : BasePresenter<CardsView>(router) {

    private val cards = mutableListOf<Card>()


    override fun attachView(view: CardsView) {
        super.attachView(view)
        this.fetchCards()
    }


    // region view

    fun onCardLongClicked(position: Int, card: Card) {
        viewState.showDeleteCardDialog(card)
    }

    fun onUnbindCard(card: Card) {
        interactor.unbindCard(card)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processUnbinding, this::processError)
    }

    fun onAddButtonClicked() {
        router.navigateTo(Screens.CARD_BINDING)
    }

    // endregion

    // region private

    private fun fetchCards() {
        interactor.fetchCards()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processCards, this::processError)
    }

    private fun processCards(result: CardsResult) {
        when (result) {
            is CardsResult.Success -> {
                cards.clear()
                cards.addAll(result.cards)
                this.cardsChanged()
            }
            is CardsResult.Failure -> {
                result.throwable.message?.let { viewState.showSystemMessage(it) }
                this.processError(result.throwable)
            }
        }
    }

    private fun processUnbinding(result: UnbindResult) {
        when (result) {
            is UnbindResult.Success -> {

                cards.removeAll { result.removedCard.id == it.id }
                this.cardsChanged()
            }
            is UnbindResult.Failure -> {
                viewState.showSystemMessage(messages.cardUnbindingError())
            }
        }
    }

    private fun cardsChanged() {
        viewState.showNoCards(cards.isEmpty())
        viewState.showCards(cards)
    }

    // endregion

}