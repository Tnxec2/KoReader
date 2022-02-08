package com.kontranik.koreader.database

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.kontranik.koreader.database.repository.BookStatusRepository
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.BookStatus
import com.kontranik.koreader.utils.FileHelper
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.util.*


class BookStatusViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: BookStatusRepository = BookStatusRepository(application)

    fun updateLastOpenTime(book: Book) {
        viewModelScope.launch {
            val bookStatus = mRepository.getBookStatusByPath(book.fileLocation)
            if (bookStatus != null) {
                mRepository.updateLastOpenTime(bookStatus.id, Date().time)
            } else {
                mRepository.insert(BookStatus(book))
            }
        }
    }

    private val path = MutableLiveData<String>()
    val savedBookStatus: LiveData<BookStatus?> = Transformations.switchMap(
        path,
        ::getLiveDataBookStatusByPath
    )

    private fun getLiveDataBookStatusByPath(path: String) = mRepository.getLiveDataBookStatusByPath(path)

    fun loadBookStatus(path: String) = apply { this.path.value = path }

    fun savePosition(book: Book) {
        viewModelScope.launch {
            val bookStatus = mRepository.getBookStatusByPath(book.fileLocation)

            if (bookStatus == null) {
                Log.d("savePosition", "bookstatus is null")
                mRepository.insert(BookStatus(book))
            } else {
                Log.d(
                    "savePosition",
                    bookStatus.position_section.toString() + " " + bookStatus.position_offset
                )
                val bookPosition =
                    if (book.curPage == null) BookPosition() else BookPosition(book.curPage!!.startBookPosition)
                bookStatus.updatePosition(bookPosition)
                mRepository.update(bookStatus)
            }
        }
    }

    private val countLastOpened = MutableLiveData<Int>()
    val lastOpenedBooks: LiveData<List<BookStatus>> = Transformations.switchMap(
        countLastOpened,
        ::getLastOpened
    )

    private fun getLastOpened(count: Int) = mRepository.getLastOpened(count)

    fun loadLastOpened(lastOpenedCount: Int) = apply { this.countLastOpened.value = lastOpenedCount }

    fun delete(id: Long) {
        mRepository.delete(id)
    }

    fun cleanup(context: Context) {
        Log.d("BookStatusCleanup", "Start: " + Date().toString())
        viewModelScope.launch {
            val list = mRepository.allBookStatus()
            Log.d("BookStatusCleanup", "list size: " + list.size)
            var countDeleted = 0
            for (i in list.indices) {
                val item = list[i]
                Log.d("BookStatusCleanup", "cleanup: " + item.path)
                if (item.path == null) {
                    mRepository.delete(item.id!!)
                    countDeleted++
                } else {
                    try {
                        if (!FileHelper.contentFileExist(context, item.path)) {
                            mRepository.delete(item.id!!)
                            countDeleted++
                        }
                    } catch (e: FileNotFoundException) {
                        mRepository.delete(item.id!!)
                        countDeleted++
                    }
                }
            }

            Log.d("BookStatusCleanup", "cleanup: finish. deleted: $countDeleted")
        }
    }
}