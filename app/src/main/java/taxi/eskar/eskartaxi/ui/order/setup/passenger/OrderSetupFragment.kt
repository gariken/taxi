package taxi.eskar.eskartaxi.ui.order.setup.passenger

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_order_setup_passenger.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseBottomSheetDialogFragment
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.PaymentType
import taxi.eskar.eskartaxi.data.model.Tariff
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.AlertDialogHelper
import toothpick.Toothpick

class OrderSetupFragment : BaseBottomSheetDialogFragment(), OrderSetupView {

    companion object {
        fun newInstance(): OrderSetupFragment {
            val args = Bundle()
            val fragment = OrderSetupFragment()
            fragment.arguments = args
            return fragment
        }
    }


    // region moxy

    @InjectPresenter lateinit var presenter: OrderSetupPresenter

    @ProvidePresenter fun providePresenter(): OrderSetupPresenter =
            Toothpick.openScope(Scopes.APP).getInstance(OrderSetupPresenter::class.java)

    // endregion

    private val adapter = TariffsAdapter({ pos, _ ->
        presenter.onPlanSelected(pos)
    })

    private var paymentTypesMenu: PopupMenu? = null

    private lateinit var progressDialog: ProgressDialog


    // =============================================================================================
    //   Android
    // =============================================================================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        progressDialog = ProgressDialog(inflater.context, R.style.AppTheme_AlertDialog).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setMessage(this@OrderSetupFragment.getString(R.string.dialog_progress_msg))
        }

        return inflater.inflate(R.layout.fragment_order_setup_passenger,
                container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv_tariffs.layoutManager = LinearLayoutManager(this.context,
                LinearLayoutManager.HORIZONTAL, false)
        rv_tariffs.adapter = this.adapter

        tv_order_setup_from.setOnClickListener { presenter.onFromClicked() }
        tv_order_setup_to.setOnClickListener { presenter.onToClicked() }
        btnAddComments.setOnClickListener { presenter.onAddCommentsClicked() }
        btnChangePaymentType.setOnClickListener { presenter.onPaymentTypeClicked() }
        btnConfirm.setOnClickListener { presenter.onConfirmClicked() }

        val ctx = context
        if (ctx != null) {
            paymentTypesMenu = PopupMenu(ctx, btnChangePaymentType, Gravity.FILL_HORIZONTAL)
            paymentTypesMenu?.setOnMenuItemClickListener {
                presenter.onPaymentTypeSelected(it.itemId, it.title.toString())
                true
            }
        }
    }


    // =============================================================================================
    //   View
    // =============================================================================================

    override fun showOrder(order: Order) {
        tv_order_setup_from.text = order.addressFrom ?: "---"
        tv_order_setup_to.text = order.addressTo ?: "---"
    }

    override fun showTariffs(tariffs: List<Tariff>, tariffN: Int) {
        adapter.replace(tariffs, tariffN)
    }

    override fun showPaymentType(type: PaymentType) {
        tv_payment_type.text = this.getString(R.string.text_payment_type, type.title)
    }

    override fun showPaymentTypes(types: List<PaymentType>) {
        paymentTypesMenu?.apply {
            menu.clear()
            types.forEach { menu.add(0, it.id, 0, it.title) }
            this.show()
        }
    }

    override fun showLoading(show: Boolean) {
        if (show) progressDialog.show()
        else progressDialog.dismiss()
    }

    override fun showSystemMessage(message: String) {
        AlertDialogHelper.showSystemMessage(context, message)
    }
}