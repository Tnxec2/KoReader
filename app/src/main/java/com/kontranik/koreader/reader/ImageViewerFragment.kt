package com.kontranik.koreader.reader

import android.graphics.BitmapFactory
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

        val barray = requireArguments().getByteArray(ImageByteArray)
        if (barray == null) {
            dismiss()
        } else {
            binding.imageviewtouchImageviewer.setImageBitmap(
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