package taxi.eskar.eskartaxi.ui.order.progress.driver

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_order_progress_driver.*
import kotlinx.android.synthetic.main.layout_order_details_route.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.actioncable.base.ACRepository
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.repository.location.LocationRepository
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.data.system.Vibrator
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.transformations.CircleTransformation
import toothpick.Toothpick


class OrderProgressDriverFragment : BaseFragment(), OrderProgressDriverView {

    companion object {
        private const val ARG_ORDER = "arg.openOrder"
        fun newInstance(order: Order): OrderProgressDriverFragment {
            val args = Bundle()
            args.putSerializable(ARG_ORDER, order)

            val fragment = OrderProgressDriverFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: OrderProgressDriverPresenter


    override val layoutResId = R.layout.fragment_order_progress_driver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_order_details_driver_from.setOnClickListener { presenter.onAddressFromClicked() }
        tv_order_details_driver_to.setOnClickListener { presenter.onAddressToClicked() }
        btn_order_details_driver_build_route.setOnClickListener { presenter.onBuildRouteClicked() }

        btn_action_start_waiting.setOnClickListener { presenter.onShortClick() }
        btn_action_start_waiting.setOnLongClickListener {
            presenter.onStartWaitingClicked()
            true
        }
        btn_action_start_driving.setOnClickListener { presenter.onShortClick() }
        btn_action_start_driving.setOnLongClickListener {
            presenter.onStartDrivingClicked()
            true
        }
        btn_action_finish_driving.setOnClickListener { presenter.onShortClick() }
        btn_action_finish_driving.setOnLongClickListener {
            presenter.onCloseOrderClicked()
            true
        }
        btn_passenger_call.setOnClickListener { presenter.onCallPassengerClicked() }
    }


    // =============================================================================================
    // View
    // =============================================================================================

    override fun showOrder(order: Order) {
        tv_order_details_driver_from.text = order.addressFrom
        tv_order_details_driver_to.text = order.addressTo
    }

    override fun showOrderTaked() {
        btn_action_start_driving.visibility = GONE
        btn_action_finish_driving.visibility = GONE
        btn_action_start_waiting.visibility = VISIBLE
    }

    override fun showOrderWaiting() {
        btn_action_start_waiting.visibility = GONE
        btn_action_finish_driving.visibility = GONE
        btn_action_start_driving.visibility = VISIBLE
    }

    override fun showOrderStarted() {
        btn_action_start_waiting.visibility = GONE
        btn_action_start_driving.visibility = GONE
        btn_action_finish_driving.visibility = VISIBLE
    }

    override fun showOrderClosed() {
        AlertDialog.Builder(this.context!!, R.style.AppTheme_AlertDialog)
                .setTitle(R.string.title_dialog_order_closed)
                .setMessage(R.string.message_dialog_order_closed)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    presenter.onRedirectConfirmed()
                })
                .create().show()
    }

    override fun showPassenger(passenger: Passenger) {
        tv_passenger_empty.visibility = GONE

        iv_avatar.visibility = VISIBLE
        if (passenger.photo.exists()) {
            Picasso.with(this.context)
                    .load(passenger.photo.url).fit().centerCrop()
                    .transform(CircleTransformation())
                    .error(R.drawable.ic_account_orange)
                    .into(iv_avatar)

        } else {
            Picasso.with(this.context)
                    .load(R.drawable.ic_account_orange)
                    .placeholder(R.drawable.ic_account_orange)
                    .error(R.drawable.ic_account_orange)
                    .into(iv_avatar)
        }

        tv_passenger_name_first.apply {
            text = passenger.name
            visibility = VISIBLE
        }
        tv_passenger_name_last.apply {
            text = passenger.surname
            visibility = VISIBLE
        }
        btn_passenger_call.visibility = VISIBLE
    }

    override fun showPassengerEmpty() {
        iv_avatar.visibility = View.INVISIBLE
        tv_passenger_name_first.visibility = View.INVISIBLE
        tv_passenger_name_last.visibility = View.INVISIBLE
        btn_passenger_call.visibility = View.INVISIBLE
        tv_passenger_empty.visibility = View.VISIBLE
    }

    override fun showPassengerLoading(show: Boolean) {
        progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showSystemMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    @ProvidePresenter fun providePresenter(): OrderProgressDriverPresenter {
        val acRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(ACRepository::class.java)
        val locationRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(LocationRepository::class.java)
        val orderRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(OrderRepository::class.java)
        val profileRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(ProfileRepository::class.java)
        val vibrator = Toothpick.openScope(Scopes.APP).getInstance(Vibrator::class.java)
        val order = arguments?.getSerializable(ARG_ORDER) as Order
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return OrderProgressDriverPresenter(acRepository, locationRepository, orderRepository,
                profileRepository, vibrator, order, router)
    }
}
