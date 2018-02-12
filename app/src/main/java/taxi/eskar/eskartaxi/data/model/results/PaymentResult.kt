package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.PaymentType

sealed class PaymentResult {

    companion object {
        fun success(type: PaymentType): PaymentResult = Success(type)
        fun fail(throwable: Throwable): PaymentResult = Fail(throwable)
    }

    class Success(val type: PaymentType) : PaymentResult()
    class Fail(val throwable: Throwable) : PaymentResult()
}