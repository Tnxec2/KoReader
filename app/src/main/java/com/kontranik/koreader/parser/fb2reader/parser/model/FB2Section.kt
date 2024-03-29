package com.kontranik.koreader.parser.fb2reader.parser.model

import com.fasterxml.jackson.annotation.JsonProperty

class FB2Section(
    @JsonProperty("orderid") val orderid: Int,
    @JsonProperty("id")  var id: String?,
    @JsonProperty("typ") var typ: FB2Elements?,
    @JsonProperty("deep") var deep: Int?,
    @JsonProperty("parentId") var parentId: Int?,
    @JsonProperty("isNote")  var isNote: Boolean = false,
    @JsonProperty("isComment")  var isComment: Boolean = false) {

    @JsonProperty("title") var title: String? = null
    @JsonProperty("text")  var text: StringBuffer = StringBuffer()
    @JsonProperty("textsize")  var textsize: Int? = null



    override fun toString(): String {
        return "Section. Orderid: $orderid, id: $id, ParentId: $parentId, Deep: $deep, Typ: $typ, isNote: $isNote, isComment: $isComment, Title: $title"
    }
}