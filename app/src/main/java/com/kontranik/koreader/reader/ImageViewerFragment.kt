package com.kontranik.koreader.reader

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.FragmentImageviewerBinding
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase


class ImageViewerFragment : DialogFragment() {

    private lateinit var binding: FragmentImageviewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentImageviewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewImageviewerClose.setOnClickListener {
            dismiss()
        }

        binding.imageviewtouchImageviewer.displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN

        val byteArray = requireArguments().getByteArray(ImageByteArray)
        if (byteArray == null) {
            dismiss()
        } else {
            binding.imageviewtouchImageviewer.setBackgroundColor(Color.WHITE)
            binding.imageviewtouchImageviewer.setImageBitmap(
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
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