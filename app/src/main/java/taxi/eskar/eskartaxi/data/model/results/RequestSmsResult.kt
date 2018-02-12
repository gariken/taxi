package taxi.eskar.eskartaxi.data.model.results

sealed class RequestSmsResult {

    companion object {
        fun success(phone: String): RequestSmsResult = Success(phone)
        fun fail(throwable: Throwable): RequestSmsResult = Fail(throwable)
    }

    class Success(val phone: String) : RequestSmsResult()
    class Fail(val throwable: Throwable) : RequestSmsResult()
}