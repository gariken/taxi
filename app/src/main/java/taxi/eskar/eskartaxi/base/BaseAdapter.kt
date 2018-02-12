package taxi.eskar.eskartaxi.base

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(

) : RecyclerView.Adapter<VH>() {

    private val items = mutableListOf<T>()


    fun getItemAt(pos: Int): T =
            items[pos]

    // RecyclerView#Adapter
    override fun getItemCount(): Int =
            items.size


    // Control content
    fun addMore(newItems: List<T>) {
        items.addAll(newItems)
        this.notifyItemRangeInserted((items.size - newItems.size), newItems.size)
    }

    fun clearItems() {
        items.clear()
        this.notifyDataSetChanged()
    }

    fun replaceItems(newItems: List<T>) {
        items.clear()
        items.addAll(newItems)
        this.notifyDataSetChanged()
    }

    fun insert(item: T, position: Int) {
        items.add(position, item)
        this.notifyItemInserted(position)
    }


    abstract class BaseViewHolder<T>(
            view: View
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        protected var item: T? = null

        abstract fun bind(item: T)
    }

    fun delete(item: T) {
        val id = items.indexOf(item)
        if (id >= 0 && id < items.size) {
            items.removeAt(id)
            this.notifyItemRemoved(id)
        }
    }
}