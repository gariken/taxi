package taxi.eskar.eskartaxi.ui.profileedit.driver

import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.Driver

interface ProfileDriverEditView : BaseView {
    fun showProfile(driver: Driver)
}