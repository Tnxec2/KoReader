package com.kontranik.koreader.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.FragmentImageviewerBinding
import com.kontranik.koreader.utils.ImageUtils
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase


class ImageViewerFragment : DialogFragment() {

    private lateinit var binding: FragmentImageviewerBinding

    private var bitmap: Bitmap? = null
    private var invertImage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentImageviewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewImageviewerClose.setOnClickListener {
            dismiss()
        }

        binding.imageViewImageviewerToggleInvert.setOnClickListener {
            invertImage = !invertImage
            updateImage()
        }

        binding.imageviewtouchImageviewer.displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN

        val byteArray = requireArguments().getByteArray(ImageByteArray)
        if (byteArray == null) {
            dismiss()
        } else {
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            invertImage = requireArguments().getBoolean(InvertImageBoolean, false)
            updateImage()
        }
    }

    private fun updateImage() {
        if (bitmap != null) {
            if (invertImage)
                binding.imageviewtouchImageviewer.setImageBitmap(ImageUtils.invertAndTint(bitmap!!, tintColor = null))
            else
                binding.imageviewtouchImageviewer.setImageBitmap(bitmap)
        }
    }

    companion object {
        const val ImageByteArray = "imagebytearray"
        const val InvertImageBoolean = "invertImageBoolean"

        fun newInstance(imagebytearray: ByteArray, invertImage: Boolean?): ImageViewerFragment {
            val frag = ImageViewerFragment()
            val args = Bundle()
            args.putByteArray(ImageByteArray, imagebytearray)
            args.putBoolean(InvertImageBoolean, invertImage == true)

            frag.arguments = args

            return frag
        }
    }
}