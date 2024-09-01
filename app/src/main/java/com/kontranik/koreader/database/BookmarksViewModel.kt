package com.kontranik.koreader.database

import android.net.Uri
import androidx.lifecycle.*
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoDestination
import com.kontranik.koreader.compose.ui.bookmarks.BoomkmarksScreenDestination
import com.kontranik.koreader.database.repository.BookmarksRepository
import com.kontranik.koreader.database.model.Bookmark
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class BookmarksViewModel(
    savedStateHandle: SavedStateHandle,
    private val mRepository: BookmarksRepository) : ViewModel() {

    fun insert(bookmark: Bookmark) {
        println("insert bookmark ${bookmark.path}")
        mRepository.insert(bookmark)
        loadBookmarks(bookmark.path)
    }

    private val source: String? = savedStateHandle[BoomkmarksScreenDestination.PATH_ARG]

    private val mPath = MutableLiveData<String?>()
    var mAllBookmarks: Flow<List<Bookmark>> = mPath.asFlow().transform { path ->
        if (path != null) {
            val result = mRepository.getByPath(path).first()
            emit(result)
        }
    }

    fun loadBookmarks(path: String) = apply {
        this.mPath.value = path
    }

    fun delete(bookmark: Bookmark) {
        bookmark.id?.let {
            mRepository.delete(bookmark.id!!)
            loadBookmarks(bookmark.path)
        }
    }

    init {
        viewModelScope.launch {
            source?.let {
                loadBookmarks(Uri.decode(it).replace('|', '%'))
            }
        }
    }



}