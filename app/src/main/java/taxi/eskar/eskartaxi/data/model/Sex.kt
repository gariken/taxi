package taxi.eskar.eskartaxi.data.model

import java.io.Serializable

data class Sex(val id: Int, val title: String, val value: String?) : Serializable {
    companion object {
        val ALL = listOf(
                Sex(0, "Мужчина", "male"),
                Sex(1, "Женщина", "female")
        )

        fun getFor(string: String?): Sex = when (string) {
            "male" -> ALL[0]
            "female" -> ALL[1]
            else -> Sex(-1, "Unnamed", null)
        }
    }
}