package taxi.eskar.eskartaxi.util

import android.content.Context
import android.support.v4.content.ContextCompat
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import taxi.eskar.eskartaxi.R

object ImagePickerHelper {

    fun setup(context: Context, aspectX: Int, aspectY: Int): CropImage.ActivityBuilder =
            this.create(context)
                    .setAspectRatio(aspectX, aspectY)
                    .setFixAspectRatio(true)

    fun setup(context: Context): CropImage.ActivityBuilder = this.create(context)

    private fun create(context: Context) = CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setActivityMenuIconColor(ContextCompat.getColor(context, R.color.orange))
}