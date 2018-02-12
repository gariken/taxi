package taxi.eskar.eskartaxi.ui.order.close.driver

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_order_close_driver.*
import kotlinx.android.synthetic.main.layout_appbar.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.data.resources.MessageResource
import taxi.eskar.eskartaxi.data.resources.StringResource
import taxi.eskar.eskartaxi.data.system.Vibrator
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.AlertDialogHelper
import taxi.eskar.eskartaxi.util.orZero
import taxi.eskar.eskartaxi.util.show
import toothpick.Toothpick

class OrderCloseDriverFragment : BaseFragment(), OrderCloseDriverView {

    companion object {
        private const val ARG_ORDER = "args.order"

        fun newInstance(order: Order): OrderCloseDriverFragment {
            return OrderCloseDriverFragment().apply {
                arguments = Bundle().apply {
                    this.putSerializable(ARG_ORDER, order)
                }
            }
        }
    }


    // region moxy

    @InjectPresenter lateinit var presenter: OrderCloseDriverPresenter

    @ProvidePresenter fun providePresenter(): OrderCloseDriverPresenter {
        val messageResource = Toothpick.openScope(Scopes.APP)
                .getInstance(MessageResource::class.java)
        val orderRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(OrderRepository::class.java)
        val stringResource = Toothpick.openScope(Scopes.APP)
                .getInstance(StringResource::class.java)
        val vibrator = Toothpick.openScope(Scopes.APP).getInstance(Vibrator::class.java)
        val order = arguments?.get(ARG_ORDER) as Order
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return OrderCloseDriverPresenter(messageResource, orderRepository, vibrator, order, stringResource, router)
    }

    // endregion


    // region android

    override val layoutResId: Int = R.layout.fragment_order_close_driver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_order_close)
        btn_payment_pos.setOnLongClickListener {
            presenter.onPaymentPositiveClicked()
        }
        btn_payment_neg.setOnLongClickListener {
            presenter.onPaymentNegativeClicked()
        }
        btn_continue.setOnLongClickListener {
            presenter.onContinueClicked()
        }
    }

    // endregion


    // region view

    override fun showOrder(order: Order) {
        tv_order_price.text = this.getString(R.string.mask_price, order.amount.orZero())
    }

    override fun showPaymentMethod(paymentMethod: String) {
        tv_payment_method.text = paymentMethod
    }

    override fun showCashButtons() {
        btn_payment_pos.show(true)
        btn_payment_neg.show(true)
    }

    override fun showCashlessButtons() {
        btn_continue.show(true)
    }

    override fun showSystemMessage(message: String) {
        AlertDialogHelper.showSystemMessage(this.context, message)
    }

    // endregion

}