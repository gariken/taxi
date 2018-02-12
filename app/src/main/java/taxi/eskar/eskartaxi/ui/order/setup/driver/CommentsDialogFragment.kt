package taxi.eskar.eskartaxi.ui.order.setup.driver

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.model.Order

class CommentsDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_ORDER = "args.order"

        fun newInstance(order: Order): CommentsDialogFragment = CommentsDialogFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_ORDER, order) }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val order = arguments?.getSerializable(ARG_ORDER) as Order
        return AlertDialog.Builder(this.context!!, R.style.AppTheme_AlertDialog)
                .setTitle(R.string.dialog_comments_title)
                .setMessage(order.getCommentsAndOptions())
                .setPositiveButton(R.string.dialog_comments_pos, { _, _ -> })
                .create()
    }

}