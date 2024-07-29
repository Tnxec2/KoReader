package com.kontranik.koreader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kontranik.koreader.compose.ui.quickmenu.QuickMenuDialog


class QuickMenuFragment : BottomSheetDialogFragment() {

    private var mListener: QuickMenuDialogListener? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface QuickMenuDialogListener {
        fun onFinishQuickMenuDialog(textSize: Float, lineSpacingMultiplier: Float, letterSpacing: Float, colorTheme: String)
        fun onChangeTextSizeQuickMenuDialog(textSize: Float)
        fun onChangeLineSpacingQuickMenuDialog(lineSpacingMultiplier: Float)
        fun onChangeLetterSpacingQuickMenuDialog(letterSpacing: Float)
        fun onCancelQuickMenuDialog()
        fun onAddBookmarkQuickMenuDialog()
        fun onShowBookmarklistQuickMenuDialog()
        fun onChangeColorThemeQuickMenuDialog(colorTheme: String, colorThemeIndex: Int)

        fun onOpenBookInfoQuickMenuDialog(bookUri: String?)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        return ComposeView(requireContext()).apply {
            setContent {
                var show by remember {
                    mutableStateOf(true)
                }

                if (show)
                QuickMenuDialog(
                    onFinishQuickMenuDialog = { textSize, lineSpacingMultiplier, letterSpacing, colorTheme ->
                        mListener?.onFinishQuickMenuDialog(textSize, lineSpacingMultiplier, letterSpacing, colorTheme)
                        show = false
                        dismiss()
                    } ,
                    onClose = {
                        mListener?.onCancelQuickMenuDialog()
                        show = false
                        dismiss()
                    },
                    onAddBookmark = {
                        mListener?.onAddBookmarkQuickMenuDialog()
                        show = false
                        dismiss() },
                    onOpenBookmarks = {
                        mListener?.onShowBookmarklistQuickMenuDialog()
                        show = false
                        dismiss()
                    },
                    onOpenBookInfo = { bookPath ->
                        mListener?.onOpenBookInfoQuickMenuDialog(bookPath)
                        show = false
                        dismiss()
                    },
                    onChangeColorThemeQuickMenuDialog = { item, pos ->
                        mListener?.onChangeColorThemeQuickMenuDialog(item, pos)
                    },
                    onChangeLineSpacingQuickMenuDialog = { value ->
                        mListener?.onChangeLineSpacingQuickMenuDialog(value)
                    },
                    onChangeLetterSpacingQuickMenuDialog = { value ->
                        mListener?.onChangeLetterSpacingQuickMenuDialog(value)
                    },
                    onChangeTextSizeQuickMenuDialog = { value ->
                        mListener?.onChangeTextSizeQuickMenuDialog(value)
                    }
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is QuickMenuDialogListener) {
            mListener = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement QuickMenuDialogListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()

        mListener?.onCancelQuickMenuDialog()
        mListener = null
    }


    companion object {
        const val THEME = "theme"
        const val TEXTSIZE = "textSize"
        const val FONTPATH = "fontpath"
        const val FONTNAME = "fontname"
    }

}