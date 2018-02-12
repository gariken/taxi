package taxi.eskar.eskartaxi.data.repository.payment

import io.reactivex.Single
import taxi.eskar.eskartaxi.data.model.Card
import taxi.eskar.eskartaxi.data.model.CardInfo
import taxi.eskar.eskartaxi.data.model.PaymentType
import taxi.eskar.eskartaxi.data.model.responses.BindCardResponse
import taxi.eskar.eskartaxi.data.model.results.*

interface PaymentRepository {
    fun getCashPaymentType(): PaymentType

    fun getPaymentTypes(): Single<PaymentsResult>
    fun setPaymentType(type: PaymentType): Single<PaymentResult>

    fun getCards(): Single<CardsResult>
    fun bindCard(cardInfo: CardInfo): Single<BindResult>
    fun unbindCard(card: Card): Single<UnbindResult>

    fun mapResponseToResult(response: BindCardResponse): BindResult

    fun getDebt(): Single<DebtResult>
    fun closeDebt(card: Card): Single<CloseDebtResult>
}