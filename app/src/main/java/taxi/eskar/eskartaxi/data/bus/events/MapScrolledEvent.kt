package taxi.eskar.eskartaxi.data.bus.events

import taxi.eskar.eskartaxi.data.model.LatLon

data class MapScrolledEvent(val latLon: LatLon) : Event()