package taxi.eskar.eskartaxi.data.model.results

import java.io.Serializable

sealed class BindResult {

    companion object {
        fun success3dsD(message: String, code: Int): BindResult = Success3dsD(message, code)
        fun success3dsE(message: String, transactionId: Int, paReq: String, acsUrl: String, termUrl: String): BindResult = Success3dsE(message, transactionId, paReq, acsUrl, termUrl)
        fun code(code: Int): BindResult = Code(code)
        fun dataError(message: String): BindResult = DataError(message)
        fun failure(throwable: Throwable): BindResult = Failure(throwable)
        fun failure(): BindResult = Failure(Exception())
    }

    data class Success3dsD(val message: String, val code: Int): BindResult()
    data class Success3dsE(val message: String, val transactionId: Int, val paReq: String, val acsUrl: String, val termUrl: String): BindResult(), Serializable
    data class Code(val code: Int): BindResult()
    data class Failure(val throwable: Throwable): BindResult()
    data class DataError(val message: String): BindResult()
}
