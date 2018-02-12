package taxi.eskar.eskartaxi.ui.paymenttypes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseAdapter
import taxi.eskar.eskartaxi.data.model.PaymentType

class PaymentAdapter(
        listener: (Int, PaymentType) -> Unit
) : BaseAdapter<PaymentType, PaymentAdapter.PaymentTypeViewHolder>() {

    private var selectedPos = -1
    private val listener = { position: Int, type: PaymentType ->
        listener.invoke(position, type)
    }

    init {

    }

    // Adapter
    override fun onBindViewHolder(holder: PaymentTypeViewHolder, position: Int) {
        val item = this.getItemAt(position)
        if (item.active) selectedPos = holder.adapterPosition

        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentTypeViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_payment_type, parent, false)

        return PaymentTypeViewHolder(view, listener)
    }


    // Control selection
    fun setSelected(position: Int) {
        this.getItemAt(selectedPos).active = false
        this.notifyItemChanged(selectedPos)

        this.getItemAt(position).active = true
        this.notifyItemChanged(position)
        selectedPos = position
    }


    class PaymentTypeViewHolder(
            view: View, private val listener: (Int, PaymentType) -> Unit
    ) : BaseAdapter.BaseViewHolder<PaymentType>(view) {

        private val button = view.findViewById<RadioButton>(R.id.rb_payment_type)
        private val clicksView = view.findViewById<View>(R.id.view_clicks)
        private val textView = view.findViewById<TextView>(R.id.tv_payment_type_title)

        init {
            clicksView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            item?.let {
                listener.invoke(this.adapterPosition, it)
            }
        }

        override fun bind(item: PaymentType) {
            this.item = item
            button.isChecked = item.active
            textView.text = item.title
        }
    }
}