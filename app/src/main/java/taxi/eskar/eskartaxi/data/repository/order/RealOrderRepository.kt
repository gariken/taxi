package taxi.eskar.eskartaxi.data.repository.order

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import taxi.eskar.eskartaxi.data.Codes
import taxi.eskar.eskartaxi.data.managers.PushManager
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.OrderOption
import taxi.eskar.eskartaxi.data.model.Tariff
import taxi.eskar.eskartaxi.data.model.responses.OrderResponse
import taxi.eskar.eskartaxi.data.model.responses.OrdersResponse
import taxi.eskar.eskartaxi.data.model.responses.PreliminaryResponse
import taxi.eskar.eskartaxi.data.model.results.*
import taxi.eskar.eskartaxi.data.retrofit.EskarApi
import taxi.eskar.eskartaxi.data.store.auth.AuthStore
import taxi.eskar.eskartaxi.util.orZero

class RealOrderRepository(
        private val eskarApi: EskarApi,
        private val store: AuthStore,
        private val pushManager: PushManager
) : OrderRepository {

    override fun getOrderOptions(): Single<OrderOptionsResult> =
            eskarApi.getOrderOptions()
                    .flatMapObservable { Observable.fromIterable(it.data.orderOptions) }
                    .map {
                        OrderOption(it.id, it.description, it.createdAt, it.updatedAt, it.price)
                    }.toList()
                    .map { OrderOptionsResult.success(it) }
                    .onErrorReturn { OrderOptionsResult.fail(it) }

    override fun getTariffs(): Single<TariffsResult> =
            eskarApi.getTariffs()
                    .flatMapObservable { Observable.fromIterable(it.data.tariffs) }
                    .filter { Tariff.hasDrawable(it.id) }
                    .map { Tariff(it.id, it.name, null) }.toList()
                    .map { TariffsResult.success(it) }
                    .onErrorReturn { TariffsResult.fail(it) }


    override fun createOrder(order: Order): Single<OrderResult> {
        return eskarApi
                .createOrder(order.latFrom, order.lonFrom, order.latTo, order.lonTo,
                        order.comment, order.tariffId, order.paymentMethod,
                        order.orderOptions.map { it.id }, store.getIdPassenger(), order.cardId)
                .doOnSuccess { pushManager.subscribeToOrderProgress(it.body()?.data?.order?.id.orZero()) }
                .map(this::mapOrderResponseToOrderResult)
                .onErrorReturn { OrderResult.fail(it) }
    }

    private fun mapOrderResponseToOrderResult(response: Response<OrderResponse>): OrderResult {
        val code = response.body()?.data?.code ?: -1
        response.errorBody()
        return when (code) {
            Codes.SUCCESS -> OrderResult.success(response.body()?.data?.order ?: Order.empty())
            else -> OrderResult.unknownStatusCode(code)
        }
    }


    override fun getOrdersHistoryPassenger(): Single<OrdersResult> {
        return eskarApi.getOrdersPassenger(store.getIdPassenger())
                .map { it.data.orders }
                .map(this::sortOrdersHistory)
                .map { OrdersResult.success(it) }
                .onErrorReturn { OrdersResult.fail(it) }
    }


    override fun getOrdersHistoryDriver(): Single<OrdersResult> {
        return eskarApi.getOrdersDriver(store.getIdDriver())
                .map { it.data.orders }
                .map(this::sortOrdersHistory)
                .map { OrdersResult.success(it) }
                .onErrorReturn { OrdersResult.fail(it) }
    }

    private fun sortOrdersHistory(orders: List<Order>): List<Order> {
        return orders.sortedByDescending { it.createdAt }
    }


    override fun getAllNewOrdersDriver(): Single<OrdersResult> {
        return eskarApi.getOrdersNewDriver()
                .map(this::responseToOrderResult)
                .onErrorReturn { OrdersResult.fail(it) }
    }

    private fun responseToOrderResult(response: Response<OrdersResponse>): OrdersResult =
            when (response.code()) {
                200 -> {
                    val orders = response.body()?.data?.orders
                            ?.sortedByDescending { it.createdAt } ?: emptyList()
                    OrdersResult.success(orders)
                }
                403 -> OrdersResult.unconfirmed()
                else -> OrdersResult.unknownStatusCode(response.code())
            }


    override fun getPreliminaryOrder(order: Order): Single<PreliminaryResult> {
        return eskarApi
                .getPreliminaryOrder(order.latFrom, order.lonFrom, order.latTo, order.lonTo,
                        order.orderOptions.map { it.id })
                .map(this::preliminaryResponseToResult)
                .onErrorReturn { PreliminaryResult.fail(it) }
    }

    private fun preliminaryResponseToResult(response: PreliminaryResponse): PreliminaryResult {
        val order = response.data.preliminaryOrder
        return PreliminaryResult.success(order.distance, order.tariffs.map {
            Tariff(it.id, it.name, it.amount)
        })
    }


    override fun deleteOrder(order: Order): Single<OrderResult> {
        return eskarApi.deleteOrder(order.id)
                .doOnSuccess { pushManager.subscribeToOrderProgress(order.id) }
                .map { OrderResult.success(Order.empty()) }
                .onErrorReturn { OrderResult.fail(it) }
    }


    override fun takeOrder(order: Order, driver: Driver): Single<OrderResult> {
        return eskarApi.takeOrder(order.id, driver.id)
                .map(this::responseToOrderResult)
                .onErrorReturn { OrderResult.fail(it) }
    }

    private fun responseToOrderResult(response: Response<OrderResponse>) =
            when (response.code()) {
                200 -> OrderResult.success(response.body()?.data?.order ?: Order.empty())
                422 -> OrderResult.lowBalance()
                else -> OrderResult.unknownStatusCode(response.code())
            }


    override fun waitOrder(order: Order): Single<OrderResult> {
        return eskarApi.waitOrder(order.id)
                .map { OrderResult.success(it.data.order) }
                .onErrorReturn { OrderResult.fail(it) }
    }


    override fun startOrder(order: Order): Single<OrderResult> {
        return eskarApi.startOrder(order.id)
                .map { OrderResult.success(it.data.order) }
                .onErrorReturn { OrderResult.fail(it) }
    }


    override fun closeOrder(order: Order, payed: Boolean?): Single<OrderResult> {
        return eskarApi.closeOrder(order.id, payed)
                .map { OrderResult.success(it.data.order) }
                .onErrorReturn { OrderResult.fail(it) }
    }


    override fun rateOrder(order: Order, rating: Double?, review: String?): Single<OrderResult> {
        return eskarApi.rateOrder(order.id, rating, review)
                .map { OrderResult.success(it.data.order) }
                .onErrorReturn { OrderResult.fail(it) }
    }
}