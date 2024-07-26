package com.kontranik.koreader.compose.ui.bookinfo

interface IBookInfoViewModell {
    val bookInfoUiState: BookInfoUiState
    val canDeleteState: Boolean
    var exit: Boolean
    val bookPath: String?
}