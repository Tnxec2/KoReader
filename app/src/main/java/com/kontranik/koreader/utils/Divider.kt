package com.kontranik.koreader.utils

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R

object Divider {
    fun appendDivider(context: Context, recyclerView: RecyclerView) {
        val divider = DividerItemDecoration(
            recyclerView.context, DividerItemDecoration.VERTICAL
        )
        ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.recycler_view_divider,
            null
        )?.let {
            divider.setDrawable(
                it
            )
        }
        recyclerView.addItemDecoration(divider)
    }
}