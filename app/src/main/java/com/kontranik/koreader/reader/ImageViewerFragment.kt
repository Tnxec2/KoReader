package com.kontranik.koreader.reader

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import it.sephiroth.android.library.imagezoom.ImageViewTouch
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase


class ImageViewerFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_imageviewer, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val close = view.findViewById<ImageView>(R.id.imageView_imageviewer_close)
        close.setOnClickListener {
            dismiss()
        }

        val touchImageView: ImageViewTouch = view.findViewById(R.id.imageviewtouch_imageviewer)
        touchImageView.displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN

        val barray = requireArguments().getByteArray(ImageByteArray)
        if (barray == null) {
            dismiss()
        } else {
            touchImageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(barray, 0, barray.size)
            )
        }
    }

    companion object {
        const val ImageByteArray = "imagebytearray"

        fun newInstance(imagebytearray: ByteArray): ImageViewerFragment {
            val frag = ImageViewerFragment()
            val args = Bundle()
            args.putByteArray(ImageByteArray, imagebytearray)
            frag.arguments = args

            return frag
        }
    }
}