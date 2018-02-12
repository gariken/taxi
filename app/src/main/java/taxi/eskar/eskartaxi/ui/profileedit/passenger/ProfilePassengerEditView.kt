package taxi.eskar.eskartaxi.ui.profileedit.passenger

import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.model.Sex

interface ProfilePassengerEditView : BaseView {
    fun showAllSex(sexList: List<Sex>)
    fun showProfile(passenger: Passenger)
    fun setSelectedSex(pos: Int, sex: Sex)
}