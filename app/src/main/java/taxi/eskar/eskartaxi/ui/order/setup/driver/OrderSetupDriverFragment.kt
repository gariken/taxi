package taxi.eskar.eskartaxi.ui.order.setup.driver

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_order_setup_driver.*
import kotlinx.android.synthetic.main.layout_appbar.*
import kotlinx.android.synthetic.main.layout_order_details_route.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.actioncable.base.ACRepository
import taxi.eskar.eskartaxi.data.actioncable.startdriver.StartDriverCable
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.transformations.CircleTransformation
import toothpick.Toothpick

class OrderSetupDriverFragment : BaseFragment(), OrderSetupDriverView {

    companion object {
        private const val ARG_ORDER = "args.openOrder"

        fun newInstance(order: Order): OrderSetupDriverFragment {
            val args = Bundle()
            args.putSerializable(ARG_ORDER, order)
            val fragment = OrderSetupDriverFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter lateinit var presenter: OrderSetupDriverPresenter


    override val layoutResId: Int = R.layout.fragment_order_setup_driver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_order_details_driver)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_orange)
        toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        tv_order_details_driver_from.setOnClickListener { presenter.onAddressFromClicked() }
        tv_order_details_driver_to.setOnClickListener { presenter.onAddressToClicked() }
        btn_order_details_driver_build_route.setOnClickListener { presenter.onBuildRouteClicked() }
        tv_order_details_driver_comments.setOnClickListener { presenter.onCommentsClicked() }
        btn_order_details_driver_confirm.setOnClickListener { presenter.onTakeClicked() }
    }


    override fun showOrder(order: Order) {
        order.addressFrom?.let { tv_order_details_driver_from.text = it }
        order.addressTo?.let { tv_order_details_driver_to.text = it }
        order.amount?.let {
            tv_order_details_driver_price.text =
                    this.getString(R.string.tv_order_details_driver_price_mask, it)
        }
        order.distance?.let {
            tv_order_details_driver_distance.text =
                    this.getString(R.string.tv_order_details_driver_distance_mask, it)
        }

        if (order.comment.isNullOrBlank() && order.orderOptions.isEmpty()) {
            tv_order_details_driver_comments.setText(R.string.text_no_data)
            tv_order_details_driver_comments.isEnabled = false
        } else {
            tv_order_details_driver_comments.text = order.getCommentsAndOptions()
        }
    }

    override fun showPassenger(passenger: Passenger) {
        tv_order_details_driver_passenger_empty.visibility = View.GONE

        tv_order_details_driver_passenger_name_first.apply {
            text = passenger.name
            visibility = View.VISIBLE
        }
        tv_order_details_driver_passenger_name_last.apply {
            text = passenger.surname
            visibility = View.VISIBLE
        }

        iv_avatar.visibility = View.VISIBLE
        if (passenger.photo.exists()) {
            Picasso.with(this.context)
                    .load(passenger.photo.thumb.url).fit().centerCrop()
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
    }

    override fun showPassengerLoading(show: Boolean) {
        progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showPassengerEmpty() {
        iv_avatar.visibility = View.INVISIBLE
        tv_order_details_driver_passenger_name_first.visibility = View.INVISIBLE
        tv_order_details_driver_passenger_name_last.visibility = View.INVISIBLE
        tv_order_details_driver_passenger_empty.visibility = View.VISIBLE
    }

    override fun showComment(order: Order) {
        CommentsDialogFragment.newInstance(order)
                .show(fragmentManager, CommentsDialogFragment.toString())
    }


    @ProvidePresenter fun providePresenter(): OrderSetupDriverPresenter {
        val startDriverCable = Toothpick.openScope(Scopes.APP)
                .getInstance(StartDriverCable::class.java)
        val orderRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(OrderRepository::class.java)
        val profileRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(ProfileRepository::class.java)
        val order = arguments?.getSerializable(ARG_ORDER) as Order
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return OrderSetupDriverPresenter(startDriverCable, orderRepository, profileRepository, order, router)
    }
}