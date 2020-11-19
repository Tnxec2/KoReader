package com.kontranik.koreader.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.kontranik.koreader.R


object ImageUtils {
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        var bitmap: Bitmap? = null

        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }

        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
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
    fun getBitmap(c: Context, imageEnum: ImageEnum): Bitmap? {

        val d = when (imageEnum) {
            ImageEnum.Parent -> c.resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
            ImageEnum.SD -> c.resources.getDrawable(R.drawable.ic_baseline_sd_card_24)
            ImageEnum.Dir -> c.resources.getDrawable(R.drawable.ic_folder_black_24dp)
            else -> c.resources.getDrawable(R.drawable.ic_book_black_24dp)
        }

        val bitmap = drawableToBitmap(d)
        return bitmap
    }

    fun byteArrayToScaledBitmap(byteArray: ByteArray, width: Int, height: Int): Bitmap {
        var bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return scaleBitmap(bitmap, width, height)
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
            height = height / ratio
        } else if (height > width) {
            // portrait
            val ratio = height / maxHeight
            height = maxHeight.toFloat()
            width = width / ratio
        } else {
            // square
            height = maxHeight.toFloat()
            width = maxWidth.toFloat()
        }

        if ( width > maxWidth) {
            val ratio = width / maxWidth
            width = maxWidth.toFloat()
            height = height / ratio
        }
        if ( height > maxHeight) {
            val ratio = height / maxHeight
            height = maxHeight.toFloat()
            width = width / ratio
        }

        return Rect(0, 0, width.toInt(), height.toInt())
    }
}