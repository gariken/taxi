package taxi.eskar.eskartaxi.business.debts

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import taxi.eskar.eskartaxi.data.model.Card
import taxi.eskar.eskartaxi.data.model.results.CardsResult
import taxi.eskar.eskartaxi.data.model.results.CloseDebtResult
import taxi.eskar.eskartaxi.data.model.results.DebtResult
import taxi.eskar.eskartaxi.data.repository.payment.PaymentRepository
import javax.inject.Inject

class DebtInteractor @Inject constructor(
        private val paymentRepository: PaymentRepository
) {

    fun fetchDebt(): Single<DebtResult> {
        return paymentRepository.getDebt()
                .subscribeOn(Schedulers.io())
    }

    fun fetchCards(): Single<CardsResult> {
        return paymentRepository.getCards()
                .subscribeOn(Schedulers.io())
    }

    fun closeDebt(card: Card): Single<CloseDebtResult> {
        return paymentRepository.closeDebt(card)
                .subscribeOn(Schedulers.io())
    }

}