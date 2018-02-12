package taxi.eskar.eskartaxi.ui.paymenttypes

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_payment_type.*
import kotlinx.android.synthetic.main.layout_appbar.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.PaymentType
import taxi.eskar.eskartaxi.data.repository.payment.PaymentRepository
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick

class PaymentTypeFragment : BaseFragment(), PaymentTypeView {

    companion object {
        fun newInstance(): PaymentTypeFragment {
            val args = Bundle()
            val fragment = PaymentTypeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter lateinit var presenter: PaymentTypePresenter

    private val adapter = PaymentAdapter({ pos, paymentType ->
        presenter.onPaymentClick(pos, paymentType)
    })

    // Android
    override val layoutResId: Int = R.layout.fragment_payment_type

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_payment_type)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_orange)
        toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        rv_payment_types.layoutManager = LinearLayoutManager(this.context)
        rv_payment_types.adapter = adapter

        btn_bind_card.setOnClickListener { presenter.onBindCardClicked() }
    }

    // View
    override fun bind() {

    }

    override fun showPaymentTypes(types: List<PaymentType>) {
        adapter.replaceItems(types)
    }

    override fun setSelectedType(position: Int, type: PaymentType) {
        adapter.setSelected(position)
    }

    override fun showPaymentTypeChangeInProcess() {
        Toast.makeText(this.context, R.string.message_payment_type_change_in_process,
                Toast.LENGTH_SHORT).show()
    }


    // Moxy
    @ProvidePresenter fun providePresenter(): PaymentTypePresenter {
        val paymentRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(PaymentRepository::class.java)
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return PaymentTypePresenter(paymentRepository, router)
    }
}