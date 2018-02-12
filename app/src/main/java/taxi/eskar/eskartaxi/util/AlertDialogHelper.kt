package taxi.eskar.eskartaxi.util

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import taxi.eskar.eskartaxi.R

object AlertDialogHelper {

    fun showSystemMessage(context: Context?, message: String,
                          listener: DialogInterface.OnClickListener? = null) {
        AlertDialog.Builder(context ?: return, R.style.AppTheme_AlertDialog)
                .setMessage(message)
                .setPositiveButton(R.string.pos_btn_default, listener)
                .show()
    }

}