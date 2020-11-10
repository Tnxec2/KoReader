package com.kontranik.koreader.model

class AbstractSection(var elements: MutableList<AbstractElement>, var sections: MutableList<AbstractSection>) {
}