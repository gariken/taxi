package taxi.eskar.eskartaxi.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import java.io.File

fun Context.getDpi(): Float {
    return this.resources.displayMetrics.density;
}

fun Context.toDpi(px: Int): Float {
    return px / this.getDpi();
}

fun Context.toPx(dp: Int): Int {
    return (dp * this.getDpi() + 0.5f).toInt()
}

fun Context.getFile(uri: Uri?): File? {
    if (uri != null) {
        return File(getPath(uri))
    }
    return null
}

fun Context.getPath(uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = this.contentResolver.query(uri, projection,
            null, null, null).also { it.close() } ?: return null
    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val s = cursor.getString(column_index)
    cursor.close()
    return s
}

fun Int?.orZero(): Int = this ?: 0

fun View.show(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}