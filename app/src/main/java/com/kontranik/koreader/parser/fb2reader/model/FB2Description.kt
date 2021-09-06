package com.kontranik.koreader.parser.fb2reader.model

class FB2Description {
    var titleInfo = FB2TitleInfo()
    var documentInfo = FB2DocumentInfo()
    var publishInfo = FB2PublishInfo()
    var customInfo = StringBuffer()
}