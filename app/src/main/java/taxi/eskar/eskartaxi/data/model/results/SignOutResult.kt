package taxi.eskar.eskartaxi.data.model.results

sealed class SignOutResult {

    companion object {
        fun success(): SignOutResult = Success()
        fun fail(throwable: Throwable): SignOutResult = Fail(throwable)
    }

    class Success() : SignOutResult()
    class Fail(val throwable: Throwable) : SignOutResult()
}