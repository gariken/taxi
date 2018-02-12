package taxi.eskar.eskartaxi.business.ordersetup

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.PaymentType
import taxi.eskar.eskartaxi.data.model.Tariff
import taxi.eskar.eskartaxi.data.model.results.*
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.data.repository.payment.PaymentRepository
import taxi.eskar.eskartaxi.data.resources.StringResource
import taxi.eskar.eskartaxi.data.store.order.OrderStore
import taxi.eskar.eskartaxi.data.store.payment.PaymentStore
import javax.inject.Inject

class OrderSetupInteractor @Inject constructor(
        private val orderRepository: OrderRepository,
        private val orderStore: OrderStore,
        private val paymentRepository: PaymentRepository,
        private val paymentStore: PaymentStore,
        private val stringResource: StringResource
) {

    init {
        orderStore.update {
            it.cardId = paymentStore.getPreferredPaymentType().id
            it.orderOptions.clear()
            it.comment = null
            it
        }
    }

    fun order() = orderStore.get()

    fun paymentType() = paymentStore.getPreferredPaymentType()

    fun fetchTariffs(): Single<TariffsResult> {
        return orderRepository.getTariffs()
                .subscribeOn(Schedulers.io())
    }

    fun fetchPaymentTypes(): Single<PaymentTypesResult> {
        return paymentRepository.getCards()
                .map(this::mapCardsResultToPaymentTypes)
                .onErrorReturn(PaymentTypesResult.Companion::fail)
                .subscribeOn(Schedulers.io())
    }

    private fun mapCardsResultToPaymentTypes(result: CardsResult): PaymentTypesResult {
        return when (result) {
            is CardsResult.Success -> {
                val types = mutableListOf(paymentRepository.getCashPaymentType())
                types.addAll(result.cards.map {
                    PaymentType(it.id, stringResource.paymentTypeCard(it.lastFourNumbers))
                })
                PaymentTypesResult.success(types)
            }
            is CardsResult.Failure -> PaymentTypesResult.fail(result.throwable)
            is CardsResult.Unconfirmed -> PaymentTypesResult.unconfirmed()
            is CardsResult.UnknownStatusCode ->
                PaymentTypesResult.unknownStatusCode(result.statusCode)
        }
    }

    fun updatePrice(): Single<PreliminaryResult> {
        return orderRepository
                .getPreliminaryOrder(orderStore.get())
                .subscribeOn(Schedulers.io())
    }

    fun updateSourceAddress(address: Address): Order {
        return orderStore.updateAndGet {
            it.addressFrom = address.title
            it.latFrom = address.lat
            it.lonFrom = address.lon
            it
        }
    }

    fun updateTargetAddress(address: Address): Order {
        return orderStore.updateAndGet {
            it.addressTo = address.title
            it.latTo = address.lat
            it.lonTo = address.lon
            it
        }
    }

    fun updateComments(order: Order): Order {
        return orderStore.updateAndGet {
            it.comment = order.comment
            it.orderOptions.apply {
                clear()
                addAll(order.orderOptions)
            }
            it
        }
    }

    fun createOrder(tariff: Tariff): Single<OrderResult> {
        return orderRepository
                .createOrder(orderStore.get().apply {
                    paymentMethod = "cash"
                    tariffId = tariff.id
                    amount = tariff.price
                })
                .subscribeOn(Schedulers.io())
    }

    fun changePaymentType(id: Int, title: String): PaymentType {
        val type = PaymentType(id, title)
        orderStore.update { it.apply { cardId = id } }
        paymentStore.putPreferredPaymentType(type)
        return type
    }

}