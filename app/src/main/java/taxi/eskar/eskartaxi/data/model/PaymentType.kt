package taxi.eskar.eskartaxi.data.model

data class PaymentType(
        val id: Int,
        val title: String,
        var active: Boolean = true
)