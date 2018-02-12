package taxi.eskar.eskartaxi.util.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseAdapter
import taxi.eskar.eskartaxi.data.model.Sex

class SexAdapter(
        listener: (Int, Sex) -> Unit
) : BaseAdapter<Sex, SexAdapter.SexViewHolder>() {

    private var selectedPosOld = -1
    private var selectedPos = -1
    private val listener = { position: Int, sex: Sex ->
        listener.invoke(position, sex)
    }

    init {

    }

    // Adapter
    override fun onBindViewHolder(holder: SexViewHolder, position: Int) {
        val item = this.getItemAt(position)

        holder.bind(item)
        holder.button.isChecked = position == selectedPos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SexViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sex, parent, false)

        return SexViewHolder(view, listener)
    }


    // Control selection
    fun setSelected(position: Int) {
        selectedPosOld = selectedPos
        selectedPos = position

        this.notifyItemChanged(selectedPosOld)
        this.notifyItemChanged(selectedPos)
    }


    class SexViewHolder(
            view: View, private val listener: (Int, Sex) -> Unit
    ) : BaseAdapter.BaseViewHolder<Sex>(view) {

        val button: RadioButton = view.findViewById(R.id.rb_sex)
        private val clicksView = view.findViewById<View>(R.id.view_clicks)
        private val textView = view.findViewById<TextView>(R.id.tv_sex_title)

        init {
            clicksView.setOnClickListener(this)
        }

        override fun bind(item: Sex) {
            this.item = item
            textView.text = item.title
        }

        override fun onClick(view: View) {
            item?.let { listener.invoke(this.adapterPosition, it) }
        }
    }
}