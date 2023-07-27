package com.kontranik.koreader.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.ComponentReadInfoAreaBinding
import com.kontranik.koreader.utils.OnSwipeTouchListener

class ReadInfoArea@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
):
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding = ComponentReadInfoAreaBinding.inflate(LayoutInflater.from(context), this)

    private var listener: ReadInfoAreaListener? = null

    private var tempParam = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ReadInfoArea,
            0, 0).apply {

            try {
                tempParam = getBoolean(R.styleable.ReadInfoArea_tempParam, false)
            } finally {
                recycle()
            }
        }

        initComponents()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initComponents() {
        binding.llInfobereich.setOnTouchListener(object :
            OnSwipeTouchListener(context) {
            override fun onClick(point: Point) {
                super.onClick(point)
                listener?.onClickReadInfoArea()
            }

            override fun onLongClick(point: Point) {
                super.onLongClick(point)
                listener?.onLongClickReadInfoArea()
            }
        })
    }

    fun update(left: String, center: String, right: String) {
        binding.tvInfotextLeft.text = left
        binding.tvInfotextSystemstatus.text = center
        binding.tvInfotextRight.text = right
    }

    fun changeStyle(textColor: Int) {
        binding.tvInfotextLeft.setTextColor(textColor)
        binding.tvInfotextSystemstatus.setTextColor(textColor)
        binding.tvInfotextRight.setTextColor(textColor)
    }

    interface ReadInfoAreaListener {
        fun onClickReadInfoArea()
        fun onLongClickReadInfoArea()
    }

    fun setListener(listener: ReadInfoAreaListener) {
        this.listener  = listener
    }

    fun setTextLeft(text: CharSequence?) {
        binding.tvInfotextLeft.text = text
    }

    fun setTextRight(text: CharSequence?) {
        binding.tvInfotextRight.text = text
    }

    fun setTextMiddle(text: CharSequence?) {
        binding.tvInfotextSystemstatus.text = text
    }
}