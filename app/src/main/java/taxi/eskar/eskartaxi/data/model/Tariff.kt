package taxi.eskar.eskartaxi.data.model

import taxi.eskar.eskartaxi.R

data class Tariff(val id: Int, val title: String, var price: Int?) {

    companion object {
        fun getDrawableResIdForId(id: Int, active: Boolean = false) =
                when (id) {
                    1 -> {
                        if (active)
                            R.drawable.ic_tariff_economy_active
                        else
                            R.drawable.ic_tariff_economy_inactive
                    }
                    2 -> {
                        if (active)
                            R.drawable.ic_tariff_comfort_active
                        else
                            R.drawable.ic_tariff_comfort_inactive
                    }
                    3 -> {
                        if (active)
                            R.drawable.ic_tariff_ladies_active
                        else
                            R.drawable.ic_tariff_ladies_inactive
                    }
                    4 -> {
                        if (active)
                            R.drawable.ic_tariff_courier_active
                        else
                            R.drawable.ic_tariff_courier_inactive
                    }
                    else -> R.drawable.ic_favorite
                }

        fun hasDrawable(id: Int) = id in 1..4
    }

}