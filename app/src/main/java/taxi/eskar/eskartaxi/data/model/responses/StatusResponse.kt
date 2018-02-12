package taxi.eskar.eskartaxi.data.model.responses

data class StatusResponse(val data: Data) {
    data class Data(val status: String)
}