package taxi.eskar.eskartaxi.util.transformations

import android.graphics.*

import com.squareup.picasso.Transformation


class CircleTransformation() : Transformation {

    companion object {
        private val PAINT_BITMAP = Paint().apply {
            isAntiAlias = true
        }
        private val PAINT_BORDER = Paint().apply {
            color = Color.parseColor(STROKE_COLOR)
            isAntiAlias = true
            strokeWidth = STROKE_WIDTH
            style = Paint.Style.STROKE
        }

        private val STROKE_COLOR = "#FFAD0D"
        private val STROKE_WIDTH = 8f
    }

    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)

        val x = (source.width - size) / 2
        val y = (source.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }

        val bitmap = Bitmap.createBitmap(size, size, source.config)

        val canvas = Canvas(bitmap)

        PAINT_BITMAP.shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val r = size / 2f
        canvas.drawCircle(r, r, r, PAINT_BITMAP)

        val strokeWidthHalf = STROKE_WIDTH / 2
        canvas.drawCircle(r, r, r - strokeWidthHalf, PAINT_BORDER)

        squaredBitmap.recycle()
        return bitmap
    }

    override fun key(): String {
        return "circle"
    }
}