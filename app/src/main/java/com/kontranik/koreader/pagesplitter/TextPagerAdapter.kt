package com.kontranik.koreader.pagesplitter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kontranik.koreader.model.Page
import com.kontranik.koreader.pagesplitter.PageFragment.Companion.newInstance

class TextPagerAdapter(
        fm: FragmentManager?,
        private val pages: List<Page>) : FragmentStatePagerAdapter(fm!!) {

    override fun getItem(i: Int): Fragment {
        return newInstance(pages[i].content)
    }

    override fun getCount(): Int {
        return pages.size
    }
}