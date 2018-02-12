package taxi.eskar.eskartaxi.data.model.bodies

data class AddressSuggestionBody(var query: String, var count: Int? = 25) : Body()
