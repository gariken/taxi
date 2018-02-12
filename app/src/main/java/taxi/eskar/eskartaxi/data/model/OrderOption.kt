package taxi.eskar.eskartaxi.data.model

import java.io.Serializable
import java.util.*

data class OrderOption(val id: Int, val description: String,
                       val createdAt: Date, val updatedAt: Date,
                       val price: Double? = null) : Serializable