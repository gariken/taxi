package taxi.eskar.eskartaxi.ui.address.selecting

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import kotlinx.android.synthetic.main.fragment_address_selecting.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.repository.address.AddressRepository
import taxi.eskar.eskartaxi.data.repository.location.LocationRepository
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.ui.Results
import taxi.eskar.eskartaxi.ui.start.passenger.StartPassengerFragment
import toothpick.Toothpick


class AddressSelectingFragment : BaseFragment(), AddressSelectingView {

    companion object {
        const val ARG_RESULT_CODE = "args.result_code"

        fun newInstance(resultCode: Int): AddressSelectingFragment {
            val args = Bundle()
            args.putInt(ARG_RESULT_CODE, resultCode)
            val fragment = AddressSelectingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter lateinit var presenter: AddressSelectingPresenter

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap

    private val cameraPositionBuilder = CameraPosition.Builder().zoom(16.0)
    private val latLng = LatLon(.0, .0)
    private val markerOptions = MarkerOptions()
    private var marker: Marker? = null
    private var resultCode = 0


    // Android
    override val layoutResId: Int = R.layout.fragment_address_selecting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultCode = arguments?.getInt(ARG_RESULT_CODE) ?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_address_selecting)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_orange)
        toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        val iconFactory = IconFactory.getInstance(view.context)
        markerOptions.icon = iconFactory.fromResource(R.drawable.ic_location_user_red_24dp)

        val largeMarkerDrawable = this.resolveDrawable(R.drawable.ic_map_marker_solar_from_red_48dp,
                R.drawable.ic_map_marker_solar_to_blue_48dp, R.drawable.ic_map_marker_orange_48dp)
        iv_address_select_marker.setImageDrawable(largeMarkerDrawable)

        val smallMarkerDrawable = this.resolveDrawable(R.drawable.ic_map_marker_from_red,
                R.drawable.ic_map_marker_to_blue, R.drawable.ic_map_marker_orange)
        iv_address_select_address.setImageDrawable(smallMarkerDrawable)

        mapView = view.findViewById(R.id.mv_mapbox)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this::onMapReady)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }


    // View
    override fun bind() {
        card_location.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onLocationClicked() }
        card_select_address.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onSelectClicked() }
    }

    override fun showAddress(address: Address) {
        tv_address_select_address.text = address.title
    }

    override fun showUserLocation(latLon: LatLon) {
        val ll = com.mapbox.mapboxsdk.geometry.LatLng(latLon.lat, latLon.lon)
        this.animateCameraTo(map, ll)
        this.updateUserMarker(map, ll)
    }

    override fun showLoading(show: Boolean) {
        if (show) {
            progress_bar.visibility = VISIBLE
            iv_address_select_address.visibility = INVISIBLE
        } else {
            progress_bar.visibility = INVISIBLE
            iv_address_select_address.visibility = VISIBLE
        }
    }


    // =============================================================================================
    //   Moxy
    // =============================================================================================
    @ProvidePresenter fun providePresenter(): AddressSelectingPresenter {
        val addressRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(AddressRepository::class.java)
        val locationRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(LocationRepository::class.java)
        val resultCode = arguments?.getInt(ARG_RESULT_CODE) ?: 0
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return AddressSelectingPresenter(addressRepository, locationRepository, resultCode, router)
    }


    // =============================================================================================
    //   Private
    // =============================================================================================
    private fun resolveDrawable(res1: Int, res2: Int, defaultRes: Int): Drawable {
        val drawableRes = when (resultCode) {
            Results.ADDRESS_TYPING_SELECT_ON_MAP_FROM -> res1
            Results.ADDRESS_TYPING_SELECT_ON_MAP_TO -> res2
            else -> defaultRes
        }

        return ContextCompat.getDrawable(this.context!!, drawableRes)!!
    }

    private fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        map.setOnScrollListener(this::onMapScrolled)
        presenter.onMapReady()
    }

    private fun onMapScrolled() {
        val ll = map.cameraPosition.target
        presenter.onMapScrolled(this.latLng.apply {
            lat = ll.latitude
            lon = ll.longitude
        })
    }

    private fun animateCameraTo(map: MapboxMap, latLng: com.mapbox.mapboxsdk.geometry.LatLng) {
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(
                cameraPositionBuilder.target(latLng).build())
        map.animateCamera(cameraUpdate, StartPassengerFragment.DURATION_CAMERA_ANIM)
    }

    private fun updateUserMarker(map: MapboxMap, latLng: com.mapbox.mapboxsdk.geometry.LatLng) {
        if (marker != null) {
            marker?.remove()
            markerOptions.position(latLng)
        }

        marker = map.addMarker(markerOptions.position(latLng))
    }
}