package taxi.eskar.eskartaxi.util

import android.os.Build
import android.webkit.CookieManager

object CookieManagerHelper {


    /**
     * Clears cookies asynchronously
     */
    fun clear() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(null)
        } else {
            CookieManager.getInstance().removeAllCookie()
        }
    }

}