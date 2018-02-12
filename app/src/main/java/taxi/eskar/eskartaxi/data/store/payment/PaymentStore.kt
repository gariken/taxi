package taxi.eskar.eskartaxi.data.store.payment

import taxi.eskar.eskartaxi.data.model.PaymentType

interface PaymentStore {
    fun getPreferredPaymentType(): PaymentType
    fun putPreferredPaymentType(type: PaymentType)
    fun clear()
}