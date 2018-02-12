package taxi.eskar.eskartaxi.data.store.location

import android.content.SharedPreferences
import taxi.eskar.eskartaxi.data.model.LatLon

class RealLocationStore(private val prefs: SharedPreferences) : LocationStore {

    companion object {
        private const val PREFS_LAT = "lat"
        private const val PREFS_LON = "lon"
    }

    override fun getLocation(): LatLon {
        return LatLon(prefs.getFloat(PREFS_LAT, 0f).toDouble(),
                prefs.getFloat(PREFS_LON, 0f).toDouble())
    }

    override fun hasLocation(): Boolean {
        return prefs.contains(PREFS_LAT) && prefs.contains(PREFS_LON)
    }

    override fun putLocation(latLon: LatLon) {
        prefs.edit()
                .putFloat(PREFS_LAT, latLon.lat.toFloat())
                .putFloat(PREFS_LON, latLon.lon.toFloat())
                .apply()
    }

    override fun removeLocation() {
        prefs.edit().remove(PREFS_LAT).remove(PREFS_LON).apply()
    }

}