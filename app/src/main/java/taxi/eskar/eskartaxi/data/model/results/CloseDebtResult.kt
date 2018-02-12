package taxi.eskar.eskartaxi.data.model.results

sealed class CloseDebtResult {

    companion object {
        fun success(): CloseDebtResult = Success
        fun code(code: Int): CloseDebtResult = Code(code)
        fun failure(throwable: Throwable): CloseDebtResult = Failure(throwable)
    }

    object Success : CloseDebtResult()
    data class Code(val code: Int) : CloseDebtResult()
    data class Failure(val throwable: Throwable) : CloseDebtResult()
}