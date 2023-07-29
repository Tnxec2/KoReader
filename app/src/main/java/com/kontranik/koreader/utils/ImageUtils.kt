package com.kontranik.koreader.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.kontranik.koreader.R
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


object ImageUtils {
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    @JvmStatic
    fun getBitmap(c: Context, imageEnum: ImageEnum): Bitmap {

        val d = when (imageEnum) {
            ImageEnum.Parent -> ResourcesCompat.getDrawable(
                c.resources,
                R.drawable.ic_baseline_arrow_back_24,
                null
            )
            ImageEnum.SD -> ResourcesCompat.getDrawable(
                c.resources,
                R.drawable.ic_baseline_sd_card_24,
                null
            )
            ImageEnum.Dir -> ResourcesCompat.getDrawable(
                c.resources,
                R.drawable.ic_folder_black_24dp,
                null
            )
            else -> ResourcesCompat.getDrawable(c.resources, R.drawable.ic_book_black_24dp, null)
        }

        return drawableToBitmap(d!!)
    }

    @JvmStatic
    fun invertAndTint(src: Bitmap, tintColor: Int?): Bitmap? {
        val height = src.height
        val width = src.width
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val matrixGrayscale = ColorMatrix()
        matrixGrayscale.setSaturation(0f)
        val matrixInvert = ColorMatrix()
        matrixInvert.set(
            floatArrayOf(
                -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
        matrixInvert.preConcat(matrixGrayscale)
        val filter = ColorMatrixColorFilter(matrixInvert)
        paint.colorFilter = filter
        canvas.drawBitmap(src, 0f, 0f, paint)
        tintColor?.let { canvas.drawColor(it, PorterDuff.Mode.MULTIPLY) }
        return bitmap
    }

    fun byteArrayToScaledBitmap(byteArray: ByteArray?, width: Int, height: Int): Bitmap? {
        if ( byteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            if (bitmap != null)
                return scaleBitmap(bitmap, width, height)
        }
        return null
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val mSize = getScaledSize(bitmap.width, bitmap.height, maxWidth, maxHeight)
        return Bitmap.createScaledBitmap(bitmap, mSize.width(), mSize.height(), true)
    }

    fun getScaledSize(originalWidth: Int, originalHeight: Int, maxWidth: Int, maxHeight: Int): Rect {
        var width: Float = originalWidth.toFloat()
        var height: Float = originalHeight.toFloat()
        if (width > height) {
            // landscape
            val ratio = width / maxWidth
            width = maxWidth.toFloat()
            height /= ratio
        } else if (height > width) {
            // portrait
            val ratio = height / maxHeight
            height = maxHeight.toFloat()
            width /= ratio
        } else {
            // square
            height = maxHeight.toFloat()
            width = maxWidth.toFloat()
        }

        if ( width > maxWidth) {
            val ratio = width / maxWidth
            width = maxWidth.toFloat()
            height /= ratio
        }
        if ( height > maxHeight) {
            val ratio = height / maxHeight
            height = maxHeight.toFloat()
            width /= ratio
        }

        return Rect(0, 0, width.toInt(), height.toInt())
    }

    // convert from bitmap to byte array
    fun getBytes(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) return null

        val scaled = if (bitmap.width > 50 || bitmap.height > 100) scaleBitmap(bitmap, 50, 100) else bitmap
        val stream = ByteArrayOutputStream()
        scaled.compress(CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    // convert from byte array to bitmap
    fun getImage(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}