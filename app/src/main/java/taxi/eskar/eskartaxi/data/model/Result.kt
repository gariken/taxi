package taxi.eskar.eskartaxi.data.model

sealed class Result<T>(val item: T) {

    companion object {
        fun <T> success(item: T): Result<T> = Success(item)
        fun fail(throwable: Throwable): Result<Throwable> = Fail(throwable)
    }

    class Success<T>(item: T) : Result<T>(item)
    class Fail(item: Throwable) : Result<Throwable>(item)
}