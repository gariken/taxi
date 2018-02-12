package taxi.eskar.eskartaxi.data.model.responses

data class SendPasswordResponse(val data: Data) {
    data class Data(val state: String)
}