package taxi.eskar.eskartaxi.data.model.responses

data class TariffsResponse(val data: Data) {
    data class Data(val tariffs: List<Tariff>) {
        data class Tariff(val id: Int,
                          val name: String,
                          val waitingPrice: Double,
                          val pricePerKilometer: Double,
                          val percentageOfDriver: Double,
                          val freeWaitingMinutes: Int,
                          val driverRateIncreaseByOrders: Double,
                          val maxDriverRate: Double,
                          val driverRateIncreaseByRating: Double,
                          val minOrderAmount: Double,
                          val driverRateIncreaseByPhoto: Double,
                          val position: Int)
    }
}