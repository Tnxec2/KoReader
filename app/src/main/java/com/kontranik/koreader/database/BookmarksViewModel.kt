package com.kontranik.koreader.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.kontranik.koreader.database.repository.BookmarksRepository
import com.kontranik.koreader.model.BookStatus
import com.kontranik.koreader.model.Bookmark
import java.io.File

class BookmarksViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: BookmarksRepository = BookmarksRepository(application)

    fun insert(bookmark: Bookmark) {
        mRepository.insert(bookmark)
    }

    private val path = MutableLiveData<String>()
    var mAllBookmarks: LiveData<List<Bookmark>> = Transformations.switchMap(
        path,
        ::getLiveDataBookmarksByPath
    )

    private fun getLiveDataBookmarksByPath(path: String) = mRepository.getByPath(path)

    fun loadBookmarks(path: String) = apply { this.path.value = path }

    fun delete(bookmark: Bookmark) {
        if (bookmark.id == null) return
        mRepository.delete(bookmark.id!!)
    }

}