package taxi.eskar.eskartaxi.data.services

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import timber.log.Timber

class TokenRefreshService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        Timber.i("Token is ${FirebaseInstanceId.getInstance().token}")
    }
}