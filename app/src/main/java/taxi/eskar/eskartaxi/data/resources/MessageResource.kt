package taxi.eskar.eskartaxi.data.resources

interface MessageResource {
    fun cardBindingSuccess(): String
    fun cardBindingError(): String
    fun cardBindingDataError(): String
    fun cardUnbindingError(): String
    fun cardsLoadingError(): String

    fun debtLoadingError(): String
    fun debtClosingError(): String
    fun error(code: Int): String
    fun debtClosingSuccess(): String

    fun orderCreatingError(statusCode: Int): String
    fun orderClosingError(): String
}