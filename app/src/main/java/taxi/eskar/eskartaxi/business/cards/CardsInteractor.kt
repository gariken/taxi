package taxi.eskar.eskartaxi.business.cards

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import taxi.eskar.eskartaxi.data.model.Card
import taxi.eskar.eskartaxi.data.model.results.CardsResult
import taxi.eskar.eskartaxi.data.model.results.UnbindResult
import taxi.eskar.eskartaxi.data.repository.payment.PaymentRepository
import taxi.eskar.eskartaxi.data.store.payment.PaymentStore
import timber.log.Timber
import javax.inject.Inject

class CardsInteractor @Inject constructor(
        private val repository: PaymentRepository,
        private val store: PaymentStore
) {

    fun fetchCards(): Single<CardsResult> {
        return repository.getCards()
                .subscribeOn(Schedulers.io())
    }

    fun unbindCard(card: Card): Single<UnbindResult> {
        return repository.unbindCard(card)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(::onSuccessfulUnbind)
    }

    private fun onSuccessfulUnbind(result: UnbindResult) {
        if (result is UnbindResult.Success) {
            val removedCard = result.removedCard
            val preferedType = store.getPreferredPaymentType()
            if (removedCard.id == preferedType.id)
                store.clear()
        }
    }

}