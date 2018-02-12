package taxi.eskar.eskartaxi.data.model

import java.io.Serializable

data class Address(val title: String?, val lat: Double = .0, val lon: Double = .0,
                   val subtitle: String? = null, val text: String? = null) : Serializable