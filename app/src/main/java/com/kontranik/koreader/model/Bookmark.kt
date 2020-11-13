package com.kontranik.koreader.model

import java.util.*

class Bookmark(
        var id: Long? = null,
        var path: String,
        var text: String? = null,
        var sort: String? = null,
        var position_section: Int = 0,
        var position_offset: Int = 0,
        var createDate: Long? = null
) {

    init {
        if ( createDate == null) createDate = Date().time
        if ( sort == null ) sort = "" + position_section + SORT_TRIM + position_offset + SORT_TRIM + createDate
    }

    companion object {
        const val SORT_TRIM = ":"
    }
}