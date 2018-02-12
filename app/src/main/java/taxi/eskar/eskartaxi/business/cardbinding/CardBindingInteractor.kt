package taxi.eskar.eskartaxi.business.cardbinding

import io.reactivex.Single
import taxi.eskar.eskartaxi.data.model.CardInfo
import taxi.eskar.eskartaxi.data.model.results.BindResult
import taxi.eskar.eskartaxi.data.repository.payment.PaymentRepository
import javax.inject.Inject

class CardBindingInteractor @Inject constructor(private val paymentRepository: PaymentRepository) {

    fun bindCard(cardInfo: CardInfo): Single<BindResult> {
        return paymentRepository.bindCard(cardInfo)
    }

}