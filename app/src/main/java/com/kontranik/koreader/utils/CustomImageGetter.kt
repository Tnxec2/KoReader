package com.kontranik.koreader.utils

import android.graphics.drawable.Drawable
import android.text.Html
import com.kontranik.koreader.model.Book

class CustomImageGetter(
        private val book: Book,
        private val pageWidth: Int,
        private val pageHeight: Int,
        private val colorTint: Int,
): Html.ImageGetter {

    override fun getDrawable( source: String?): Drawable? {
        if ( source != null) {

            val mImage = book.getImageBitmapDrawable(source, colorTint)

            if ( mImage != null) {
                val mSize = ImageUtils.getScaledSize(
                        mImage.intrinsicWidth, mImage.intrinsicHeight,
                        pageWidth, pageHeight)
                mImage.setBounds(0, 0, mSize.width(), mSize.height())
            }
            return mImage
        }
        return null
    }

}