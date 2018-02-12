package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.PaymentType

sealed class PaymentTypesResult {

    companion object {
        fun success(types: List<PaymentType>): PaymentTypesResult = Success(types)
        fun fail(throwable: Throwable): PaymentTypesResult = Failure(throwable)
        fun unknownStatusCode(statusCode: Int): PaymentTypesResult = UnknownStatusCode(statusCode)
        fun unconfirmed(): PaymentTypesResult = Unconfirmed()
    }

    data class Success(val types: List<PaymentType>) : PaymentTypesResult()
    data class Failure(val throwable: Throwable) : PaymentTypesResult()
    data class UnknownStatusCode(val statusCode: Int): PaymentTypesResult()
    class Unconfirmed: PaymentTypesResult()

}