package taxi.eskar.eskartaxi.data

object Codes {
    const val SUCCESS           = 0
    const val INVALID_DATA      = 1001 // ошибка в данных
    const val THREE_D_SECURE    = 1002 // карта с 3D Secure
    const val ORDER_ERROR       = 2000 // невалидные данные для заказа
    const val HAS_OPEN_ORDERS   = 2001 // у юзера есть открытый заказ
    const val TARIFF_NOT_FOUND  = 2002 // тариф не найден
    const val HAS_DEBT          = 2003 // есть задолженность
    const val DISTANCE_ERROR    = 2004 // ошибка расчета дистанции
    const val CARD_NOT_FOUND    = 2005 // карта не найдена
    const val PAYMENT_NOT_FOUND = 3000 // не найдена сущность оплаты
    const val NO_ORDERS         = 3001 // у юзера нет заказов
    const val NO_DEBTS          = 3002 // у юзера нет задолжности
    const val NO_MONEY          = 5051 // недостаточно средств
}