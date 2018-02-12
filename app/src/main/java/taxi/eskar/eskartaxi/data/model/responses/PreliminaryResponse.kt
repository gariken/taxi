package taxi.eskar.eskartaxi.data.model.responses

data class PreliminaryResponse(val data: Data) {
    data class Data(val preliminaryOrder: PreliminaryOrder) {
        data class PreliminaryOrder(val distance: Double, val tariffs: List<Tariff>) {
            data class Tariff(val id: Int, val name: String, val amount: Int)
        }
    }
}