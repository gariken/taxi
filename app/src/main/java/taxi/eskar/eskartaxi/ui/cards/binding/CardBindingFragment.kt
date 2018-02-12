package taxi.eskar.eskartaxi.ui.cards.binding

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.EditText
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.widget.textChanges
import com.redmadrobot.inputmask.MaskedTextChangedListener
import kotlinx.android.synthetic.main.fragment_card_binding.*
import kotlinx.android.synthetic.main.layout_appbar.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick

class CardBindingFragment : BaseFragment(), CardBindingView {

    companion object {
        private const val AUTOCOMPLETE = true

        private const val MASK_CARD_NUM = "[0000] [0000] [0000] [0000999]"
        private const val MASK_CARD_EXP = "[00]/[00]"
        private const val MASK_CARD_CVV = "[000]"


        fun newInstance() = CardBindingFragment()
    }


    @InjectPresenter lateinit var presenter: CardBindingPresenter


    // region android

    override val layoutResId: Int = R.layout.fragment_card_binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setTitle(R.string.title_card_binding)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_orange)
        toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        this.setupListener(et_card_num, MASK_CARD_NUM, presenter::onCardNumChanged)
        this.setupListener(et_card_expiration, MASK_CARD_EXP, presenter::onCardExpChanged)
        this.setupListener(et_card_cvv, MASK_CARD_CVV, presenter::onCardCvvChanged)

        presenter.onCardOwnChanged("ALEKSEY DOLGIY")

        btn_bind_card.setOnClickListener { presenter.onBindCardClicked() }
    }

    // endregion

    // region view

    override fun bind() {
        et_card_owner.textChanges().doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }.subscribe({ presenter.onCardOwnChanged(it) })
    }

    override fun showBindingButton(show: Boolean) {
        btn_bind_card.isEnabled = show
    }

    override fun showSystemMessage(message: String) {
        view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
    }

    // endregion


    // region moxy

    @ProvidePresenter fun providePresenter(): CardBindingPresenter =
            Toothpick.openScope(Scopes.APP).getInstance(CardBindingPresenter::class.java)

    // endregion


    // region private

    private fun setupListener(editText: EditText, mask: String,
                              method: (filled: Boolean, value: String) -> Unit) {
        val listener = MaskedTextChangedListener(mask, AUTOCOMPLETE, editText, null,
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                        method.invoke(maskFilled, extractedValue)
                    }
                })

        editText.addTextChangedListener(listener)
        editText.onFocusChangeListener = listener
        editText.hint = listener.placeholder()
    }

    // endregion

}