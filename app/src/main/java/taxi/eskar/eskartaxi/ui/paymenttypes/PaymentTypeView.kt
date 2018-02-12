package taxi.eskar.eskartaxi.ui.paymenttypes

import taxi.eskar.eskartaxi.base.BaseView
import taxi.eskar.eskartaxi.data.model.PaymentType

interface PaymentTypeView : BaseView {
    fun showPaymentTypes(types: List<PaymentType>)
    fun setSelectedType(position: Int, type: PaymentType)
    fun showPaymentTypeChangeInProcess()
}