package taxi.eskar.eskartaxi.ui.order.history.driver

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_order_history_passenger.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick

class OrderHistoryDriverFragment : BaseFragment(), OrderHistoryDriverView {

    companion object {
        fun newInstance(): OrderHistoryDriverFragment {
            val args = Bundle()
            val frnt = OrderHistoryDriverFragment()
            frnt.arguments = args
            return frnt
        }
    }


    @InjectPresenter lateinit var presenter: OrderHistoryDriverPresenter

    private val adapter = OrderAdapter({ pos, order ->

    })


    // Android
    override val layoutResId: Int = R.layout.fragment_order_history_passenger

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_order_history)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_orange)
        toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        rv_orders.addItemDecoration(HorizontalDividerItemDecoration.Builder(this.context)
                .margin(2)
                .build())
        rv_orders.layoutManager = LinearLayoutManager(this.context)
        rv_orders.adapter = adapter
    }


    // View
    override fun bind() {
        btn_order_history_passenger_error_reload.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onReloadClicked() }
    }

    override fun showOrders(orders: List<Order>) {
        tv_order_history_passenger_empty_orders.visibility = View.INVISIBLE
        layout_order_history_passenger_error.visibility = View.INVISIBLE
        adapter.replaceItems(orders)
    }

    override fun showOrdersEmpty() {
        adapter.clearItems()
        layout_order_history_passenger_error.visibility = View.INVISIBLE
        tv_order_history_passenger_empty_orders.visibility = View.VISIBLE
    }

    override fun showOrdersError() {
        adapter.clearItems()
        tv_order_history_passenger_empty_orders.visibility = View.INVISIBLE
        layout_order_history_passenger_error.visibility = View.VISIBLE
    }


    @ProvidePresenter fun providePresenter(): OrderHistoryDriverPresenter =
            Toothpick.openScope(Scopes.APP).getInstance(OrderHistoryDriverPresenter::class.java)
}