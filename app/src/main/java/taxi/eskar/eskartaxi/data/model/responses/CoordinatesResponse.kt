package taxi.eskar.eskartaxi.data.model.responses

data class CoordinatesResponse(val data: Data) {
    data class Data(private val coordinates: List<Double>) {
        fun lon() = coordinates.getOrElse(1, { 0.0 })
        fun lat() = coordinates.getOrElse(0, { 0.0 })
    }
}