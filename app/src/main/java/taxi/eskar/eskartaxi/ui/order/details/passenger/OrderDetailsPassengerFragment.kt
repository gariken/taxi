package taxi.eskar.eskartaxi.ui.order.details.passenger

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_order_details_passenger.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.transformations.CircleTransformation
import toothpick.Toothpick

class OrderDetailsPassengerFragment : BaseFragment(), OrderDetailsPassengerView {

    companion object {
        const val ARG_ORDER = "args.order"
        fun newInstance(order: Order): OrderDetailsPassengerFragment {
            val args = Bundle()
            args.putSerializable(ARG_ORDER, order)
            val fragment = OrderDetailsPassengerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter lateinit var presenter: OrderDetailsPassengerPresenter

    override val layoutResId: Int = R.layout.fragment_order_details_passenger

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.apply {
            setTitle(R.string.title_order_details)
            setNavigationIcon(R.drawable.ic_arrow_back_orange)
            setNavigationOnClickListener { presenter.onBackClicked() }
        }
    }


    override fun showDriver(driver: Driver) {
        tv_order_details_passenger_driver_name_first.apply {
            text = driver.name
            visibility = View.VISIBLE
        }
        tv_order_details_passenger_driver_name_last.apply {
            text = driver.surname
            visibility = View.VISIBLE
        }

        if (driver.photo.exists()) {
            Picasso.with(this.context)
                    .load(driver.photo.thumb.url).fit().centerCrop()
                    .transform(CircleTransformation())
                    .placeholder(R.drawable.ic_account_orange)
                    .error(R.drawable.ic_account_orange)
                    .into(iv_avatar)
        } else {
            Picasso.with(this.context)
                    .load(R.drawable.ic_account_orange)
                    .placeholder(R.drawable.ic_account_orange)
                    .error(R.drawable.ic_account_orange)
                    .into(iv_avatar)
        }
        iv_avatar.visibility = View.VISIBLE
    }

    override fun showDriverEmpty() {
        tv_order_details_passenger_driver_empty.visibility = View.VISIBLE
    }

    override fun showLoading(show: Boolean) {
        progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showOrder(order: Order) {
        tv_order_details_passenger_from.text = order.addressFrom
        tv_order_details_passenger_to.text = order.addressTo
        tv_order_details_passenger_price.text = this.getString(R.string.mask_price, order.amount)
        tv_order_details_passenger_distance.text = this.getString(R.string.mask_distance, order.distance)
    }


    @ProvidePresenter fun providePresenter(): OrderDetailsPassengerPresenter {
        val order = arguments?.getSerializable(ARG_ORDER) as Order
        val profileRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(ProfileRepository::class.java)
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return OrderDetailsPassengerPresenter(order, profileRepository, router)
    }
}