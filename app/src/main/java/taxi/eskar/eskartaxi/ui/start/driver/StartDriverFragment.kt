package taxi.eskar.eskartaxi.ui.start.driver

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_start_driver.*
import kotlinx.android.synthetic.main.layout_appbar.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick

class StartDriverFragment : BaseFragment(), StartDriverView {

    companion object {
        fun newInstance(): StartDriverFragment {
            val args = Bundle()
            val frnt = StartDriverFragment()
            frnt.arguments = args
            return frnt
        }
    }


    @InjectPresenter lateinit var presenter: StartDriverPresenter

    private val adapter = OrderAdapter({ _, order ->
        presenter.onOrderClicked(order)
    })


    // region android

    override val layoutResId: Int = R.layout.fragment_start_driver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_start_driver)
        toolbar.setNavigationIcon(R.drawable.ic_account_orange)
        toolbar.setNavigationOnClickListener { presenter.onProfileClicked() }
        toolbar.inflateMenu(R.menu.fragment_start_driver)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_reload -> {
                    presenter.onReloadClicked()
                    true
                }
                else -> false
            }
        }

        btn_reload.setOnClickListener { presenter.onReloadClicked() }

        layout_swipe_to_refresh.setColorSchemeResources(R.color.orange)
        layout_swipe_to_refresh.setOnRefreshListener { presenter.onReloadClicked() }

        rv_orders.addItemDecoration(HorizontalDividerItemDecoration.Builder(this.context)
                .margin(2)
                .build())
        rv_orders.layoutManager = LinearLayoutManager(this.context)
        rv_orders.adapter = adapter
    }

    // endregion


    // region view

    override fun showLoading(show: Boolean) {
        layout_swipe_to_refresh.isRefreshing = show
    }

    override fun showOrders(orders: List<Order>) {
        layout_error.visibility = View.GONE
        layout_swipe_to_refresh.visibility = View.VISIBLE
        adapter.replaceItems(orders)
    }

    override fun showOrdersEmpty(show: Boolean) {
        layout_swipe_to_refresh.visibility = this.resolveVisibility(!show)
        tv_error.setText(R.string.text_driver_orders_empty)
        layout_error.visibility = this.resolveVisibility(show)
    }

    override fun showUnconfirmedError(show: Boolean) {
        layout_swipe_to_refresh.visibility = this.resolveVisibility(!show)
        tv_error.setText(R.string.error_unconfirmed)
        layout_error.visibility = this.resolveVisibility(show)
    }

    // endregion


    // region private

    private fun resolveVisibility(show: Boolean) = if (show) View.VISIBLE else View.INVISIBLE

    // endregion


    // region moxy

    @ProvidePresenter fun providePresenter(): StartDriverPresenter =
            Toothpick.openScope(Scopes.APP).getInstance(StartDriverPresenter::class.java)

    // endregion
}