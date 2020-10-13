package com.kontranik.koreader.pagesplitter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kontranik.koreader.pagesplitter.PageFragment.Companion.newInstance

class TextPagerAdapter(
        fm: FragmentManager?,
        private val pageTexts: List<CharSequence>) : FragmentStatePagerAdapter(fm!!) {

    override fun getItem(i: Int): Fragment {
        return newInstance(pageTexts[i])
    }

    override fun getCount(): Int {
        return pageTexts.size
    }
}