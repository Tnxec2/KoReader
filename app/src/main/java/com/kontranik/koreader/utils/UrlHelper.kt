package com.kontranik.koreader.utils

import java.net.URL

object UrlHelper {
    fun getUrl(url: String, startUrl: String?): String {
        if (startUrl == null || url == startUrl) return url
        if (url.startsWith("http")) return url
        val mUrl = URL(startUrl)

        return if (url.startsWith("//"))
            "${mUrl.protocol}:${url}"
        else
            "${mUrl.protocol}://${mUrl.authority}/${if (url.startsWith("/")) url.substring(1) else url}"
    }
}