package com.kontranik.koreader.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.kontranik.koreader.R
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    @JvmStatic
    fun getBitmap(c: Context, imageEnum: ImageEnum): Bitmap? {
        val b = getBitmapData(c, imageEnum)
        if (b != null) {
            val bmp = BitmapFactory.decodeByteArray(b, 0, b.size)
            val width = 100
            val height = 100
            val background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val originalWidth = bmp.width.toFloat()
            val originalHeight = bmp.height.toFloat()
            val canvas = Canvas(background)
            val scale = width / originalWidth
            val xTranslation = 0.0f
            val yTranslation = (height - originalHeight * scale) / 2.0f
            val transformation = Matrix()
            transformation.postTranslate(xTranslation, yTranslation)
            transformation.preScale(scale, scale)
            val paint = Paint()
            paint.isFilterBitmap = true
            canvas.drawBitmap(bmp, transformation, paint)
            return background
        }
        return null
    }

    private fun getBitmapData(c: Context, imageEnum: ImageEnum): ByteArray? {
        var d: Drawable? = null
        var cover: ByteArray? = null
        when (imageEnum) {
            ImageEnum.Parent -> d = c.resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
            ImageEnum.SD -> d = c.resources.getDrawable(R.drawable.ic_baseline_sd_card_24)
            ImageEnum.Dir -> d = c.resources.getDrawable(R.drawable.ic_folder_black_24dp)
            else -> d = c.resources.getDrawable(R.drawable.ic_book_black_24dp)
        }
        if (d == null && cover == null) {
            d = c.resources.getDrawable(R.drawable.ic_book_black_24dp)
        }
        if (d != null) {
            val icon = drawableToBitmap(d)
            val stream = ByteArrayOutputStream()
            if (icon != null) {
                icon.compress(Bitmap.CompressFormat.PNG, 100, stream)
                cover = stream.toByteArray()
            }
        }
        return cover
    }

    fun byteArrayToScaledBitmap(byteArray: ByteArray, width: Int, height: Int): Bitmap {
        var bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return scaleBitmap(bitmap, width, height)
    }

    fun scaleBitmap(bm: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var bm = bm
        var width = bm.width.toFloat()
        var height = bm.height.toFloat()
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
        bm = Bitmap.createScaledBitmap(bm, width.toInt(), height.toInt(), true)
        return bm
    }
}