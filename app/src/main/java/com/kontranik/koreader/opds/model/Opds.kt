package com.kontranik.koreader.opds.model

data class Opds(
    val title: String?,
    val subtitle: String? = null,
    val author: Author? = null,
    val icon: String? = null,
    val links: List<Link>,
    var entries: MutableList<Entry>,
    val search: Link? = null,
)