package com.kontranik.koreader.model

import java.util.*

class Bookmark(
        var id: Long? = null,
        var path: String,
        var text: String? = null,
        var sort: String? = null,
        var position_page: Int = 0,
        var position_element: Int = 0,
        var position_paragraph: Int = 0,
        var position_symbol: Int = 0,
        var createDate: Long? = null
) {

    init {
        if ( createDate == null) createDate = Date().time
        if ( sort == null ) sort = "" + position_page + SORT_TRIM + position_element + SORT_TRIM + position_paragraph + SORT_TRIM + position_symbol + SORT_TRIM + createDate
    }

    companion object {
        const val SORT_TRIM = ":"
    }
}