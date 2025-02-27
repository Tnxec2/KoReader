package com.kontranik.koreader.database

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.*
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.BookStatus
import com.kontranik.koreader.database.repository.AuthorsRepository
import com.kontranik.koreader.database.repository.BookStatusRepository
import com.kontranik.koreader.database.repository.LibraryItemRepository
import com.kontranik.koreader.utils.FileHelper
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.util.*


const val LAST_OPENED_COUNT = 10

class BookStatusViewModel(
    private val mRepository: BookStatusRepository,
    private val libraryItemRepository: LibraryItemRepository,
    private val authorsRepository: AuthorsRepository,
) : ViewModel() {

    private val path = MutableLiveData<String>()
    val savedBookStatus: LiveData<BookStatus?> = path.switchMap { getLiveDataBookStatusByPath(it) }

    private fun getLiveDataBookStatusByPath(path: String) = mRepository.getLiveDataBookStatusByPath(path)

    fun loadBookStatus(path: String) = apply { this.path.value = path }

    val lastOpenedBooks = mRepository.getLastOpened(LAST_OPENED_COUNT)

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
                // delete status
                mRepository.getBookStatusByPath(bookUri)?.id?.let {
                    delete(it)
                }

                // delete in library: book and authors when empty
                val libraryItemWithAuthors = libraryItemRepository.getByPathWithAuthors(bookUri)

                libraryItemWithAuthors.forEach { item ->
                    item.libraryItem.id?.let {
                        libraryItemRepository.delete(it)
                    }
                    item.authors.forEach { author: Author ->
                        author.id?.let { id ->
                            val count = libraryItemRepository.getCountByAuthorId(id)
                            if (count == 0L) author.id?.let { authorsRepository.delete(id) }
                        }
                    }
                    libraryItemRepository.deleteCrossRefLibraryItem(item.libraryItem)
                }

                // delete file
                try {
                    DocumentFile.fromSingleUri(KoReaderApplication.getContext(), Uri.parse(bookUri))?.delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
