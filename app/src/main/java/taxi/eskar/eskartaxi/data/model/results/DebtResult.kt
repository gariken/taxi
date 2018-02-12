package taxi.eskar.eskartaxi.data.model.results

sealed class DebtResult {

    companion object {
        fun success(debt: Int): DebtResult = Success(debt)
        fun failure(throwable: Throwable): DebtResult = Failure(throwable)
    }

    data class Success(val debt: Int): DebtResult()
    data class Failure(val throwable: Throwable): DebtResult()
}