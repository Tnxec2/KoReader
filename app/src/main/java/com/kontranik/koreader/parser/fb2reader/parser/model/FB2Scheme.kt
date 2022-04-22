package com.kontranik.koreader.parser.fb2reader.parser.model

class FB2Scheme {

    var description = FB2Description()
    var sections: MutableList<FB2Section> = mutableListOf()
    var cover: BinaryData? = null
    var path: String? = null

    fun getSection(orderId: Int?): FB2Section {
        return sections[orderId!!]
    }

    fun getSection(attrId: String): FB2Section? {
        for (fb2Section in sections) {
            if (fb2Section.id != null && fb2Section.id == attrId) {
                return fb2Section
            }
        }
        return null
    }
}