package taxi.eskar.eskartaxi.ui.root

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseActivity
import taxi.eskar.eskartaxi.data.model.*
import taxi.eskar.eskartaxi.data.model.results.BindResult
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.ui.Screens
import taxi.eskar.eskartaxi.ui.address.AddressActivity
import taxi.eskar.eskartaxi.ui.auth.code.AuthCodeFragment
import taxi.eskar.eskartaxi.ui.auth.phone.AuthPhoneFragment
import taxi.eskar.eskartaxi.ui.cards.binding.CardBindingFragment
import taxi.eskar.eskartaxi.ui.cards.list.CardsFragment
import taxi.eskar.eskartaxi.ui.debts.DebtFragment
import taxi.eskar.eskartaxi.ui.favoritedaddresses.FavoritedAddressesFragment
import taxi.eskar.eskartaxi.ui.order.close.driver.OrderCloseDriverFragment
import taxi.eskar.eskartaxi.ui.order.details.passenger.OrderDetailsPassengerFragment
import taxi.eskar.eskartaxi.ui.order.history.driver.OrderHistoryDriverFragment
import taxi.eskar.eskartaxi.ui.order.history.passenger.OrderHistoryPassengerFragment
import taxi.eskar.eskartaxi.ui.order.progress.driver.OrderProgressDriverFragment
import taxi.eskar.eskartaxi.ui.order.progress.passenger.OrderProgressPassengerFragment
import taxi.eskar.eskartaxi.ui.order.setup.driver.OrderSetupDriverFragment
import taxi.eskar.eskartaxi.ui.order.setup.passenger.comments.OrderCommentsActivity
import taxi.eskar.eskartaxi.ui.paymenttypes.PaymentTypeFragment
import taxi.eskar.eskartaxi.ui.profile.driver.ProfileDriverFragment
import taxi.eskar.eskartaxi.ui.profile.passenger.ProfilePassengerFragment
import taxi.eskar.eskartaxi.ui.profileedit.driver.ProfileDriverEditFragment
import taxi.eskar.eskartaxi.ui.profileedit.passenger.ProfilePassengerEditFragment
import taxi.eskar.eskartaxi.ui.registration.driver.RegDriverFragment
import taxi.eskar.eskartaxi.ui.registration.passenger.RegPassengerFragment
import taxi.eskar.eskartaxi.ui.splash.SplashFragment
import taxi.eskar.eskartaxi.ui.start.driver.StartDriverFragment
import taxi.eskar.eskartaxi.ui.start.passenger.StartPassengerFragment
import taxi.eskar.eskartaxi.ui.threedsecure.ThreeDSecureFragment
import toothpick.Toothpick
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject


class RootActivity : BaseActivity(), RootView {

    companion object {
        private const val MAPS_URI_POINT = "http://maps.google.com/maps?daddr=%s,%s"
        private const val MAPS_URI_ROUTE = "http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s"
    }

    @Inject lateinit var holder: NavigatorHolder
    @InjectPresenter lateinit var presenter: RootPresenter

    private val cameraPositionBuilder = CameraPosition.Builder().zoom(16.0)
    private val passengerMarkerOptions = MarkerOptions()
    private var passengerMarkerReference: AtomicReference<Marker?> = AtomicReference(null)
    private val driverMarkerOptions = MarkerOptions()
    private var driverMarkerReference: AtomicReference<Marker?> = AtomicReference(null)

    private lateinit var mapView: MapView

    private lateinit var map: MapboxMap


    // =============================================================================================
    //   Android
    // =============================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_root)

        Toothpick.openScopes(Scopes.APP, Scopes.ACTIVITY_MAIN).apply {
            Toothpick.inject(this@RootActivity, this)
        }

        val iconFactory = IconFactory.getInstance(this)
        passengerMarkerOptions.icon = iconFactory.fromResource(R.drawable.ic_location_user_red_24dp)
        driverMarkerOptions.icon = iconFactory.fromResource(R.drawable.ic_location_taxi_24dp)

        mapView = findViewById(R.id.mv_mapbox)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this::onMapReady)

        if (savedInstanceState == null) {
            navigator.applyCommand(Replace(Screens.SPLASH, null))
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        holder.setNavigator(navigator)
    }

    override fun onPause() {
        holder.removeNavigator()
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        if (isFinishing)
            Toothpick.closeScope(Scopes.ACTIVITY_MAIN)
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.let { mapView.onSaveInstanceState(it) }
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.fragments.forEach { fragment ->
            data?.let { fragment.onActivityResult(requestCode, resultCode, it) }
        }
    }

    override fun onBackPressed() {
        presenter.onBackClicked()
    }


    // =============================================================================================
    //   View
    // =============================================================================================

    override fun showUserLocation(latLon: LatLon?, focus: Boolean) {
        mapView.getMapAsync { map ->
            if (latLon != null) {
                val ll = com.mapbox.mapboxsdk.geometry.LatLng(latLon.lat, latLon.lon)
                if (focus) this.animateCameraTo(map, ll)
                this.updateUserMarker(map, ll)
            } else {
                passengerMarkerReference.getAndSet(null)?.let { map.removeMarker(it) }
            }
        }
    }

    override fun showDriverLocation(latLon: LatLon?, focus: Boolean) {
        mapView.getMapAsync { map ->
            if (latLon != null) {
                val ll = com.mapbox.mapboxsdk.geometry.LatLng(latLon.lat, latLon.lon)
                if (focus) this.animateCameraTo(map, ll)
                this.updateDriverMarker(map, ll)
            } else {
                driverMarkerReference.getAndSet(null)?.let { map.removeMarker(it) }
            }
        }
    }


    private fun onMapReady(map: MapboxMap) {
        map.setOnScrollListener(this::onMapScrolled)
        presenter.onMapReady()
        this.map = map
    }

    private fun onMapScrolled() {
        val ll = this.map.cameraPosition.target
        presenter.onMapScrolled(LatLon(ll.latitude, ll.longitude))
    }

    private fun animateCameraTo(map: MapboxMap, latLng: com.mapbox.mapboxsdk.geometry.LatLng) {
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(
                cameraPositionBuilder.target(latLng).build())
        map.animateCamera(cameraUpdate, StartPassengerFragment.DURATION_CAMERA_ANIM)
    }

    private fun updateUserMarker(map: MapboxMap, latLng: LatLng) {
        this.updateMarker(passengerMarkerReference, passengerMarkerOptions, map, latLng)
    }

    private fun updateDriverMarker(map: MapboxMap, latLng: LatLng) {
        this.updateMarker(driverMarkerReference, driverMarkerOptions, map, latLng)
    }

    private fun updateMarker(markerReference: AtomicReference<Marker?>,
                             options: MarkerOptions, map: MapboxMap, latLng: LatLng) {
        options.position(latLng)
        if (markerReference.get() == null) {
            markerReference.set(map.addMarker(options.position(latLng)))
        } else {
            markerReference.get()?.position = latLng
        }
    }

    private val navigator = object : SupportAppNavigator(this, R.id.container_main) {

        override fun applyCommand(command: Command?) {
            if (command is Forward && command.screenKey == Screens.PICKER) {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                }
                val chooserIntent = Intent
                        .createChooser(intent, this@RootActivity.getString(R.string.title_chooser))

                this@RootActivity.startActivityForResult(chooserIntent, command.transitionData as Int)
            } else super.applyCommand(command)
        }

        override fun createActivityIntent(screenKey: String?, data: Any?): Intent? =
                when (screenKey) {
                    Screens.ADDRESS_TYPING ->
                        AddressActivity.newIntent(this@RootActivity, data as Int)
                    Screens.ORDER_SETUP_PASSENGER_COMMENTS ->
                        OrderCommentsActivity.newIntent(this@RootActivity, data as Order)
                    Screens.CALL -> Intent(Intent.ACTION_DIAL).apply {
                        setData(Uri.parse("tel:+${data as String}"))
                    }
                    Screens.MAP_POINT -> {
                        val address = data as Address
                        this.intentForMaps(MAPS_URI_POINT,
                                String.format(Locale.US, "%f", address.lat),
                                String.format(Locale.US, "%f", address.lon))
                    }
                    Screens.MAP_ROUTE -> {
                        val order = data as Order
                        this.intentForMaps(MAPS_URI_ROUTE,
                                String.format(Locale.US, "%f", order.latFrom),
                                String.format(Locale.US, "%f", order.lonFrom),
                                String.format(Locale.US, "%f", order.latTo),
                                String.format(Locale.US, "%f", order.lonTo))
                    }
                    Screens.SETTINGS_LOCATION -> {
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    }
                    Screens.SHARE -> {
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share))
                        intent.type = "text/plain"
                        Intent.createChooser(intent, "Выберите приложение")
                    }
                    else -> null
                }

        private fun intentForMaps(uriBaseString: String, vararg args: String?): Intent {
            val uriString = uriBaseString.format(*args)
            val uri = Uri.parse(uriString)
            return Intent.createChooser(Intent(android.content.Intent.ACTION_VIEW, uri), "Выберите приложение")
        }

        override fun createFragment(screenKey: String?, data: Any?): Fragment =
                when (screenKey) {
                    Screens.SPLASH -> SplashFragment()

                    Screens.AUTH_PHONE ->
                        AuthPhoneFragment.newInstance()
                    Screens.AUTH_CODE_PASSENGER ->
                        AuthCodeFragment.newInstancePassenger(data as String)
                    Screens.AUTH_CODE_DRIVER ->
                        AuthCodeFragment.newInstanceDriver(data as String)

                    Screens.REG_PASSENGER ->
                        RegPassengerFragment.newInstance(data as Passenger)
                    Screens.REG_DRIVER ->
                        RegDriverFragment.newInstance(data as Driver)

                    Screens.PROFILE_PASSENGER ->
                        ProfilePassengerFragment.newInstance()
                    Screens.PROFILE_PASSENGER_EDIT ->
                        ProfilePassengerEditFragment.newInstance()

                    Screens.PROFILE_DRIVER ->
                        ProfileDriverFragment.newInstance()
                    Screens.PROFILE_DRIVER_EDIT ->
                        ProfileDriverEditFragment.newInstance()

                    Screens.FAVORITED_ADDRESSES_PASSENGER ->
                        FavoritedAddressesFragment.newInstance(data as Int)

                    Screens.PAYMENT_TYPE ->
                        PaymentTypeFragment.newInstance()

                    Screens.START_PASSENGER ->
                        StartPassengerFragment.newInstance()
                    Screens.START_DRIVER ->
                        StartDriverFragment.newInstance()

                    Screens.ORDER_SETUP_DRIVER ->
                        OrderSetupDriverFragment.newInstance(data as Order)

                    Screens.ORDER_HISTORY_PASSENGER ->
                        OrderHistoryPassengerFragment.newInstance()
                    Screens.ORDER_HISTORY_DRIVER ->
                        OrderHistoryDriverFragment.newInstance()

                    Screens.ORDER_HISTORY_DETAILS_PASSENGER ->
                        OrderDetailsPassengerFragment.newInstance(data as Order)

                    Screens.ORDER_PROGRESS_PASSENGER ->
                        OrderProgressPassengerFragment.newInstance(data as Order)
                    Screens.ORDER_PROGRESS_DRIVER ->
                        OrderProgressDriverFragment.newInstance(data as Order)

                    Screens.ORDER_CLOSE_DRIVER ->
                        OrderCloseDriverFragment.newInstance(data as Order)

                    Screens.CARDS -> CardsFragment.newInstance()
                    Screens.CARD_BINDING -> CardBindingFragment.newInstance()
                    Screens.THREE_D_SECURE ->
                        ThreeDSecureFragment.newInstance(data as BindResult.Success3dsE)

                    Screens.DEBTS -> DebtFragment.newInstance()

                    else -> throw UnsupportedOperationException("No screen for key $screenKey")
                }
    }


    // =============================================================================================
    //   Moxy
    // =============================================================================================

    @ProvidePresenter fun providePresenter(): RootPresenter =
            Toothpick.openScope(Scopes.APP).getInstance(RootPresenter::class.java)
}
