package taxi.eskar.eskartaxi.data.model

data class AddressResponse(val data: Data) {
    data class Data(val address: String)
}