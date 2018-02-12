package taxi.eskar.eskartaxi.ui.order.progress.passenger

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_order_progress_passenger.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.actioncable.orderprogresspassenger.OrderProgressPassengerCable
import taxi.eskar.eskartaxi.data.bus.RxBus
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.repository.connection.ConnectionRepository
import taxi.eskar.eskartaxi.data.repository.location.LocationRepository
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.transformations.CircleTransformation
import toothpick.Toothpick

class OrderProgressPassengerFragment : BaseFragment(), OrderProgressPassengerView {

    companion object {
        private const val ARG_ORDER = "args.openOrder"

        fun newInstance(order: Order): OrderProgressPassengerFragment {
            val args = Bundle()
            args.putSerializable(ARG_ORDER, order)
            val fragment = OrderProgressPassengerFragment()
            fragment.arguments = args
            return fragment
        }
    }


    @InjectPresenter lateinit var presenter: OrderProgressPassengerPresenter


    private lateinit var cancelOrderDialog: AlertDialog


    // =============================================================================================
    //   Android
    // =============================================================================================
    override val layoutResId: Int = R.layout.fragment_order_progress_passenger

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rating_bar.setOnRatingBarChangeListener { _, fl, _ -> presenter.onRatingChanged(fl) }

        cancelOrderDialog = AlertDialog.Builder(this.context!!, R.style.AppTheme_AlertDialog)
                .setMessage(R.string.dialog_order_progress_passenger_cancel_msg)
                .setPositiveButton(R.string.dialog_order_progress_passenger_cancel_pos_btn, { _, _ -> presenter.onCancelOrderOk() })
                .setNegativeButton(R.string.dialog_order_progress_passenger_cancel_neg_btn, { _, _ -> })
                .setCancelable(true).create()

        btn_search_driver_cancel.setOnClickListener { presenter.onCancelOrderClicked() }
        btn_ended_cancel.setOnClickListener { presenter.onContinueClicked() }
        btn_ended_cancel.setOnClickListener { presenter.onEndClicked() }
        btn_in_progress_rating_post.setOnClickListener { presenter.onRatingSaveClicked() }
    }

    override fun onPause() {
        cancelOrderDialog.dismiss()
        super.onPause()
    }


    // =============================================================================================
    //   View
    // =============================================================================================

    override fun bind() {
        et_review.textChanges().skipInitialValue().map(CharSequence::toString)
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onReviewChanged(it) }
    }

    override fun showDriverSearch() {
        card_search.visibility = VISIBLE
    }

    override fun showDriverFound() {
        card_search.visibility = INVISIBLE
        card_driver_found.visibility = VISIBLE
    }

    override fun showDriverInfo(driver: Driver) {
        tv_name_first.text = driver.name
        tv_name_last.text = driver.surname
        tv_rating.text = this.getString(R.string.mask_rating, driver.rating)
        if (driver.photo.exists()) {
            Picasso.with(this.context)
                    .load(driver.photo.url).fit().centerCrop()
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

        card_driver_found_info.visibility = VISIBLE
    }

    override fun showDriverArived() {
        card_driver_found.visibility = INVISIBLE
        card_arrived.visibility = VISIBLE
    }

    override fun showOrderInProgress(showRating: Boolean) {
        card_arrived.visibility = INVISIBLE
        card_in_progress.visibility = VISIBLE
        card_in_progress_rating.visibility = if (showRating) VISIBLE else INVISIBLE
    }

    override fun showOrderEnded(showRating: Boolean) {
        card_in_progress.visibility = INVISIBLE
        card_in_progress_rating.visibility = if (showRating) VISIBLE else INVISIBLE

        card_ended.visibility = VISIBLE
    }

    override fun showCancelOrderAlert() {
        cancelOrderDialog.show()
    }


    // =============================================================================================
    //   Moxy
    // =============================================================================================

    @ProvidePresenter fun providePresenter(): OrderProgressPassengerPresenter {
        val connectionRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(ConnectionRepository::class.java)
        val locationRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(LocationRepository::class.java)
        val orderProgressPassengerCable = Toothpick.openScope(Scopes.APP)
                .getInstance(OrderProgressPassengerCable::class.java)
        val orderRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(OrderRepository::class.java)
        val profileRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(ProfileRepository::class.java)
        val order = arguments?.getSerializable(ARG_ORDER) as Order
        val router = Toothpick.openScope(Scopes.APP)
                .getInstance(Router::class.java)
        val rxBus = Toothpick.openScope(Scopes.APP)
                .getInstance(RxBus::class.java)
        return OrderProgressPassengerPresenter(connectionRepository, locationRepository, orderProgressPassengerCable,
                orderRepository, profileRepository, order, router, rxBus)
    }
}