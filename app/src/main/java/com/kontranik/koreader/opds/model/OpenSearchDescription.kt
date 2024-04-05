package com.kontranik.koreader.opds.model

data class OpenSearchDescription(
    val longName: String? = null,
    val shortName: String? = null,
    val description: String? = null,
    val tags: String? = null,
    val urls: MutableList<Url>,


)

data class Url(
    val type: String,
    val template: String,
)

object SearchUrlTypes {
    val text = "text/html"
    val atom = "application/atom+xml"
    val suggestions = "application/x-suggestions+json"
}