package taxi.eskar.eskartaxi.data.model.responses

import taxi.eskar.eskartaxi.data.model.Card

data class CardsResponse(val data: Data) {
    data class Data(val cards: List<Card>)
}