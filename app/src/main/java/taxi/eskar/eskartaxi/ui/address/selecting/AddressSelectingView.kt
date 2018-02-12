package taxi.eskar.eskartaxi.ui.address.selecting

import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.LatLon

interface AddressSelectingView : BaseView {
    fun showAddress(address: Address)
    fun showUserLocation(latLon: LatLon)
}