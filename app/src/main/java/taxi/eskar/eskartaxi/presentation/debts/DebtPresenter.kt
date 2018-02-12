package taxi.eskar.eskartaxi.presentation.debts

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.business.debts.DebtInteractor
import taxi.eskar.eskartaxi.data.model.Card
import taxi.eskar.eskartaxi.data.model.results.CardsResult
import taxi.eskar.eskartaxi.data.model.results.CloseDebtResult
import taxi.eskar.eskartaxi.data.model.results.DebtResult
import taxi.eskar.eskartaxi.data.resources.MessageResource
import javax.inject.Inject

@InjectViewState class DebtPresenter @Inject constructor(
        private val interactor: DebtInteractor,
        private val messageResource: MessageResource,
        router: Router
) : BasePresenter<DebtView>(router) {

    init {
        interactor.fetchDebt()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processDebtResult, this::processError)
    }

    fun onCardSelected(card: Card) {
        interactor.closeDebt(card)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnSubscribe { viewState.showLoading(true) }
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processCloseDebtResult, this::processError)
    }

    private fun processDebtResult(result: DebtResult) {
        when (result) {
            is DebtResult.Success -> {
                viewState.showDebt(result.debt)
                if (result.debt == 0) {
                    viewState.showNoDebt()
                } else {
                    interactor.fetchCards()
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(this::unsubscribeOnDestroy)
                            .doOnSubscribe { viewState.showLoading(true) }
                            .doOnEvent { _, _ -> viewState.showLoading(false) }
                            .subscribe(this::processCardsResult, this::processError)
                }
            }
            is DebtResult.Failure -> viewState.showSystemMessage(messageResource.debtLoadingError())
        }
    }

    private fun processCardsResult(result: CardsResult) {
        when (result) {
            is CardsResult.Success -> viewState.showCards(result.cards)
            else -> viewState.showSystemMessage(messageResource.cardsLoadingError())
        }
    }

    private fun processCloseDebtResult(result: CloseDebtResult) {
        when (result) {
            CloseDebtResult.Success -> {
                viewState.showSystemMessage(messageResource.debtClosingSuccess())
                router.exit()
            }
            is CloseDebtResult.Code ->
                viewState.showSystemMessage(messageResource.error(result.code))
            is CloseDebtResult.Failure ->
                viewState.showSystemMessage(messageResource.debtClosingError())
        }
    }
}