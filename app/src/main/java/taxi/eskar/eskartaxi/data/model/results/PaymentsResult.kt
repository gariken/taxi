package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.PaymentType

sealed class PaymentsResult {

    companion object {
        fun success(type: List<PaymentType>): PaymentsResult = Success(type)
        fun fail(throwable: Throwable): PaymentsResult = Fail(throwable)
    }

    class Success(val types: List<PaymentType>) : PaymentsResult()
    class Fail(val throwable: Throwable) : PaymentsResult()
}