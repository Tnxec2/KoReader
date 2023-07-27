package com.kontranik.koreader.database

import androidx.lifecycle.*
import com.kontranik.koreader.database.repository.BookmarksRepository
import com.kontranik.koreader.database.model.Bookmark
import com.kontranik.koreader.database.repository.BookStatusRepository

class BookmarksViewModel(private val mRepository: BookmarksRepository) : ViewModel() {

    fun insert(bookmark: Bookmark) {
        mRepository.insert(bookmark)
    }

    private val path = MutableLiveData<String>()
    var mAllBookmarks: LiveData<List<Bookmark>> = path.switchMap { getLiveDataBookmarksByPath(it)}


    private fun getLiveDataBookmarksByPath(path: String) = mRepository.getByPath(path)

    fun loadBookmarks(path: String) = apply { this.path.value = path }

    fun delete(bookmark: Bookmark) {
        if (bookmark.id == null) return
        mRepository.delete(bookmark.id!!)
    }

}

class BookmarksViewModelFactory(private val repository: BookmarksRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookmarksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookmarksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}