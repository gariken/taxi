package taxi.eskar.eskartaxi.data.actioncable

object Channels {
    const val COORDINATES_DRIVERS = "DriversCoordinatesChannel"
    const val COORDINATES_PASSENGER = "UsersCoordinatesChannel"
    const val ORDERS = "OrdersChannel"
    const val ORDERS_CANCEL = "CancelOrdersChannel"
    const val ORDERS_CANCEL_FOR_DRIVERS = "CancelOrdersForDriversChannel"
    const val ORDERS_CLOSE = "CloseOrdersChannel"
    const val ORDERS_TAKE = "TakeOrdersChannel"
    const val ORDERS_TAKE_FOR_DRIVERS = "TakeOrdersForDriversChannel"
    const val START_DRIVING = "DrivingStartChannel"
    const val START_WAITING = "WaitingStartChannel"
}