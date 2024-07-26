package com.kontranik.koreader.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.kontranik.koreader.database.repository.BookStatusRepository
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.database.model.BookStatus
import com.kontranik.koreader.utils.FileHelper
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.util.*

const val LAST_OPENED_COUNT = 10

class BookStatusViewModel(private val mRepository: BookStatusRepository) : ViewModel() {

    fun updateLastOpenTime(book: Book) {
        viewModelScope.launch {
            BooksRoomDatabase.databaseWriteExecutor.execute {
                val bookStatus = mRepository.getBookStatusByPath(book.fileLocation)
                if (bookStatus != null) {
                    mRepository.updateLastOpenTime(bookStatus.id, Date().time)
                } else {
                    mRepository.insert(BookStatus(book))
                }
            }
        }
    }

    private val path = MutableLiveData<String>()
    val savedBookStatus: LiveData<BookStatus?> = path.switchMap { getLiveDataBookStatusByPath(it) }


    private fun getLiveDataBookStatusByPath(path: String) = mRepository.getLiveDataBookStatusByPath(path)

    fun loadBookStatus(path: String) = apply { this.path.value = path }

    val lastOpenedBooks: LiveData<List<BookStatus>> = mRepository.getLastOpened(LAST_OPENED_COUNT)

    fun delete(id: Long) {
        mRepository.delete(id)
    }

    fun cleanup(context: Context) {
        Log.d("BookStatusCleanup", "Start: " + Date().toString())
        BooksRoomDatabase.databaseWriteExecutor.execute {
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
            mRepository.deleteOlderCount(LAST_OPENED_COUNT)
            Log.d("BookStatusCleanup", "cleanup: finish. deleted: $countDeleted")
        }
    }

    fun deleteByPath(bookUri: String) {
        viewModelScope.launch {
            BooksRoomDatabase.databaseWriteExecutor.execute {
                val bookStatus = mRepository.getBookStatusByPath(bookUri)

                if (bookStatus?.id == null) {
                    delete(bookStatus!!.id!!)
                }
            }
        }
    }
}

class BookStatusViewModelFactory(private val repository: BookStatusRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookStatusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookStatusViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
