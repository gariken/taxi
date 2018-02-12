package taxi.eskar.eskartaxi.ui.order.setup.passenger.comments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.fragment_order_setup_passenger_comments.*
import kotlinx.android.synthetic.main.layout_appbar.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.OrderOption
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick

class OrderCommentsFragment : BaseFragment(), OrderCommentsView {
    companion object {
        const val ARG_ORDER = "args.openOrder"

        fun newInstance(order: Order): OrderCommentsFragment {
            val args = Bundle()
            args.putSerializable(ARG_ORDER, order)
            val fragment = OrderCommentsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter lateinit var presenter: OrderCommentsPresenter

    private val adapter = OrderOptionsAdapter({ selected ->
        presenter.onSelectedChanged(selected)
    })


    override val layoutResId = R.layout.fragment_order_setup_passenger_comments

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_order_comments)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_orange)
        toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        rv_options.layoutManager = LinearLayoutManager(this.context)
        rv_options.adapter = adapter
    }

    override fun bind() {
        et_order_comments_comment.textChanges()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onCommentChanged(it) }

        btn_order_comments_save.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onSaveClicked() }
    }

    override fun showOrder(order: Order) {
        et_order_comments_comment.setText(order.comment)
    }

    override fun showOptions(options: List<OrderOption>, selectedOptions: List<Int>) {
        tv_order_options_empty.visibility = INVISIBLE
        adapter.replaceAll(options, selectedOptions)
        rv_options.visibility = VISIBLE
    }

    override fun showOptionsEmpty() {
        adapter.clear()
        rv_options.visibility = INVISIBLE
        tv_order_options_empty.visibility = VISIBLE
    }

    @ProvidePresenter fun providePresenter(): OrderCommentsPresenter {
        val ordersRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(OrderRepository::class.java)
        val order = arguments?.getSerializable(ARG_ORDER) as Order
        val router = Toothpick.openScope(Scopes.APP)
                .getInstance(Router::class.java)
        return OrderCommentsPresenter(ordersRepository, order, router)
    }
}
