package taxi.eskar.eskartaxi.ui.splash

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_splash.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.show
import toothpick.Toothpick

class SplashFragment : BaseFragment(), SplashView {


    companion object {
        const val RC_LOCATION = 11
    }

    @InjectPresenter lateinit var presenter: SplashPresenter


    // region android

    override val layoutResId: Int = R.layout.fragment_splash

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_location_settings.setOnClickListener { presenter.onLocationSettingsClicked() }
        btn_retry_io.setOnClickListener { presenter.onRetryClicked() }
        btn_retry_location.setOnClickListener { presenter.onRetryClicked() }
        btn_sign_out.setOnClickListener { presenter.onSignOutClicked() }

        this.requestPermissions(arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION,
                READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), RC_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == RC_LOCATION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                presenter.onPermissionsGranted()
            } else {
                presenter.onPermissionsDenied()
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // endregion


    // region view

    override fun showAuthError(show: Boolean) {
        tv_error_auth.setText(R.string.error_auth)
        layout_auth_error.show(show)
    }

    override fun showBannedError(show: Boolean) {
        tv_error_auth.setText(R.string.error_banned)
        layout_auth_error.show(show)
    }

    override fun showIOError(show: Boolean) {
        layout_io_error.show(show)
    }

    override fun showLocationError(show: Boolean) {
        layout_location_error.show(show)
    }

    override fun showLoading(show: Boolean) {
        progress_bar.show(show)
    }

    // endregion


    // region moxy

    @ProvidePresenter fun providePresenter(): SplashPresenter =
            Toothpick.openScope(Scopes.APP).getInstance(SplashPresenter::class.java)

    // endregion

}