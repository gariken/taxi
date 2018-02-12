package taxi.eskar.eskartaxi.data.resources

interface StringResource {
    fun paymentTypeCard(lastFourNumbers: String): String
    fun paymentMethod(paymentMethod: String): String
}