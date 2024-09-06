package com.kontranik.koreader.compose.ui.library

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.DocumentsContract.Document
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.ui.library.bytitle.LibraryByTitleDestination
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.LibraryItem
import com.kontranik.koreader.database.model.LibraryItemAuthorsCrossRef
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import com.kontranik.koreader.database.repository.AuthorsRepository
import com.kontranik.koreader.database.repository.LibraryItemRepository
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.Closeable


class LibraryViewModel(
    savedStateHandle: SavedStateHandle,
    private val libraryItemRepository: LibraryItemRepository,
    private val authorsRepository: AuthorsRepository,
    private val applicationScope: CoroutineScope,
    ) : ViewModel() {

    private val authorId: String? = savedStateHandle[LibraryByTitleDestination.AUTHOR_ID]

    val authorState = mutableStateOf<Author?>(null)

    init {
        authorId?.let {
            BooksRoomDatabase.databaseWriteExecutor.execute {
                authorsRepository.getById(it).firstOrNull().let { author ->
                    loadTitlePageInit(author)
                }
            }
        }
    }

    private var libraryTitleSearchFilter = MutableLiveData<String?>(null)

    val libraryTitlePageByFilter = libraryTitleSearchFilter.switchMap {
        getTitlePageByFilter(it).liveData.cachedIn(viewModelScope)
    }.asFlow()

    private fun getTitlePageByFilter(searchFilter: String?) = Pager(config = PagingConfig(15)) {
        libraryItemRepository.pageLibraryItem(authorState.value, searchFilter)
    }

    private fun loadTitlePageInit(author: Author?) = apply {
        viewModelScope.launch {
            authorState.value = author
            if ( libraryTitlePageByFilter.asLiveData().value == null) {
                libraryTitleSearchFilter.postValue(null)
            }
        }
    }

    fun changeTitleSearchText(text: String?) {
        val filter = if (text?.isEmpty() == true) null else text
        val altVal = this.libraryTitleSearchFilter.value
        if ( altVal == null || altVal != filter) {
            viewModelScope.launch {
                libraryTitleSearchFilter.postValue(filter)
            }
        }
    }
    private val libraryAuthorSearchFilter = MutableLiveData<String?>()

    val libraryAuthorPageByFilter = libraryAuthorSearchFilter.switchMap {
        getAuthorPageByFilter(it).liveData.cachedIn(viewModelScope)
    }.asFlow()

    private fun getAuthorPageByFilter(searchFilter: String?) = Pager(PagingConfig(pageSize = 15)) {
        authorsRepository.pageAuthor(searchFilter)
    }

    fun loadAuthorPageInit() = apply {
        if ( libraryAuthorPageByFilter.asLiveData().value == null) {
            this.libraryAuthorSearchFilter.postValue(null)
        }
    }

    fun changeAuthorSearchText(text: String?) {
        val altVal = this.libraryAuthorSearchFilter.value
        if ( altVal == null || altVal != text) {
            libraryAuthorSearchFilter.postValue(text)
        }
    }

    fun insert(libraryItem: LibraryItem) = viewModelScope.launch {
        libraryItemRepository.insert(libraryItem)
    }

    fun delete(libraryItemWithAuthors: LibraryItemWithAuthors) = viewModelScope.launch {
        KoReaderApplication.getApplicationScope().launch {
            libraryItemWithAuthors.libraryItem.id?.let {
                libraryItemRepository.delete(it)
            }
            libraryItemWithAuthors.authors.forEach { author ->
                val authorId = author.id
                authorId?.let { id ->
                    val count = libraryItemRepository.getCountByAuthorId(id)
                    if (count == 0L) author.id?.let { authorsRepository.delete(id) }
                }
            }
            libraryItemRepository.deleteCrossRefLibraryItem(libraryItemWithAuthors.libraryItem)
        }
    }

    fun deleteAll() = viewModelScope.launch {
        libraryItemRepository.deleteAll()
        authorsRepository.deleteAll()
        libraryItemRepository.deleteAllCrossRef()
    }


    val CHANNEL_ID = "LIBRARY"
    val notificationId = 0

    val refreshInProgress = MutableLiveData<Boolean>(false)

    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = KoReaderApplication.getContext().getString(R.string.channel_name_library)
        val descriptionText = KoReaderApplication.getContext().getString(R.string.channel_description_library)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)

    }

    fun readRecursive(context: Context, scanPoints: Set<String>) {
        applicationScope.launch {
            refreshInProgress.postValue(true)
            notify(context, "Library refresh", "Refreshing of library started")
            scanPoints.forEach { scanPoint ->
                readRecursiveScanpoint(context, scanPoint)
            }
            refreshInProgress.postValue(false)
            notify(context, "Library refresh", "Refreshing of library ended")
        }
    }

    private fun readRecursiveScanpoint(context: Context, scanPoint: String) {
        val resolver: ContentResolver = context.contentResolver

        try {
            val directoryUri = Uri.parse(scanPoint)
                ?: throw IllegalArgumentException("Must pass URI of directory to open")
            val documentsTree = DocumentFile.fromTreeUri(context, directoryUri)
            if (documentsTree == null || !documentsTree.isDirectory || !documentsTree.canRead()) {
                //
            } else {
                traverseDirectoryEntries(resolver, directoryUri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun notify(context: Context, title: String, message: String) {
        val builder =
            NotificationCompat.Builder(KoReaderApplication.getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_local_library_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        try {

        NotificationManagerCompat.from(KoReaderApplication.getContext()).apply {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            } else {
                notify(notificationId, builder.build())
            }
        }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun traverseDirectoryEntries(contentResolver: ContentResolver, rootUri: Uri?) {
        var childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            rootUri,
            DocumentsContract.getTreeDocumentId(rootUri)
        )

        // Keep track of our directory hierarchy
        val dirNodes: MutableList<Uri> = mutableListOf()
        dirNodes.add(childrenUri)
        while (dirNodes.isNotEmpty()) {
            childrenUri = dirNodes.removeAt(0) // get the item from top
            Log.d("Library refresh", "node uri: $childrenUri")
            val c = contentResolver.query(
                childrenUri,
                arrayOf(
                    Document.COLUMN_DOCUMENT_ID,
                    Document.COLUMN_DISPLAY_NAME,
                    Document.COLUMN_MIME_TYPE
                ),
                null,
                null,
                null
            )
            try {
                while (c!!.moveToNext()) {
                    val docId = c.getString(0)
                    val name = c.getString(1)
                    val mime = c.getString(2)
                    Log.d("Library refresh", "docId: $docId, name: $name, mime: $mime")
                    if (isDirectory(mime)) {
                        val newNode = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, docId)
                        dirNodes.add(newNode)
                        // traverseDirectoryEntries(contentResolver, newNode)
                    } else if (
                        EbookHelper.isEpub(name)
                        ||
                        EbookHelper.isFb2(name)
                    ) {
                        val documentUri = DocumentsContract.buildDocumentUriUsingTree(rootUri, docId)
                        readBookInfo(documentUri)
                    }
                }
            } finally {
                closeQuietly(c)
            }
        }
    }

    // Util method to check if the mime type is a directory
    private fun isDirectory(mimeType: String): Boolean {
        return Document.MIME_TYPE_DIR == mimeType
    }

    // Util method to close a closeable
    private fun closeQuietly(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (re: RuntimeException) {
                throw re
            } catch (ignore: java.lang.Exception) {
                // ignore exception
            }
        }
    }

    private fun readBookInfo(uri: Uri) {
        val item = libraryItemRepository.getByPath(uri.toString())
        if (item.isEmpty()) {
            val bookInfo = readBookInfo(
                uri.toString()
            )
            Log.d("saveBookInLibrary", "bookInfo " + bookInfo.toString())
            saveBookInLibrary(bookInfo)
        }
    }

    private fun saveBookInLibrary(bookInfo: BookInfo?) {
        bookInfo?.let {
            val item = libraryItemRepository.getByPath(bookInfo.path)
            if (item.isEmpty()) {
                val libraryItemId = libraryItemRepository.insert(LibraryItem(it))
                if (libraryItemId != null) {
                    bookInfo.authors?.forEach { bookInfoAuthor ->
                        saveAuthor(bookInfoAuthor, libraryItemId)
                    }
                }
                Log.d("LibraryViewModel", "save " + it.path)
            }
        }
    }

    private fun saveAuthor(author: Author, libraryItemId: Long) {
        val dbAuthorList = authorsRepository.getByName(
            firstname = author.firstname,
            middlename = author.middlename,
            lastname = author.lastname
        )
        Log.d("LibraryVieWModel", "dbAuthorList size: ${dbAuthorList.size}")
        if (dbAuthorList.isEmpty()) {
            val authorId = authorsRepository.insert(author)
            if (authorId != null) {
                libraryItemRepository.inserCrossRef(
                    LibraryItemAuthorsCrossRef(authorId, libraryItemId)
                )
            }
        } else {
            val authorId = dbAuthorList[0].id
            if (authorId != null) {
                libraryItemRepository.inserCrossRef(
                    LibraryItemAuthorsCrossRef(authorId, libraryItemId)
                )
            }
        }
    }

    fun updateLibraryItem(libraryItemWithAuthors: LibraryItemWithAuthors) {
        applicationScope.launch {
            val bookInfo = readBookInfo(
                libraryItemWithAuthors.libraryItem.path
            )
            Log.d("LibraryViewModel", "bookInfo " + bookInfo.toString())
            if (bookInfo == null) delete(libraryItemWithAuthors)
            bookInfo?.let {
                with(libraryItemWithAuthors.libraryItem) {
                    title = it.title
                    cover = ImageUtils.getBytes(it.cover)
                }
                libraryItemRepository.update(libraryItemWithAuthors.libraryItem)

                if (bookInfo.authors == null) {
                    libraryItemRepository.deleteCrossRefLibraryItem(libraryItemWithAuthors.libraryItem)
                } else {
                    // delete authors where are not in bookinfo
                    libraryItemWithAuthors.authors.forEach { libraryItemAuthor ->
                        if (bookInfo.authors?.firstOrNull { bookInfoAuthor ->
                                bookInfoAuthor.compare(
                                    libraryItemAuthor
                                )
                            } == null) {
                            authorsRepository.deleteCrossRefAuthor(libraryItemAuthor)
                        }
                    }
                    // insert new authors from bookinfo
                    bookInfo.authors?.forEach { bookInfoAuthor ->
                        val libraryItemAuthor =
                            libraryItemWithAuthors.authors.firstOrNull { libraryItemAuthor ->
                                bookInfoAuthor.compare(libraryItemAuthor)
                            }
                        if (libraryItemAuthor == null) {
                            val dbAuthorList = authorsRepository.getByName(
                                bookInfoAuthor.firstname?.trim(),
                                bookInfoAuthor.middlename?.trim(),
                                bookInfoAuthor.lastname?.trim()
                            )
                            if (dbAuthorList.isEmpty()) {
                                val authorId = authorsRepository.insert(bookInfoAuthor)
                                if (authorId != null) {
                                    libraryItemRepository.inserCrossRef(
                                        LibraryItemAuthorsCrossRef(
                                            authorid = authorId,
                                            libraryitemid = libraryItemWithAuthors.libraryItem.id!!
                                        )
                                    )
                                }
                            } else {
                                val authorId = dbAuthorList[0].id
                                if (authorId != null) {
                                    libraryItemRepository.inserCrossRef(
                                        LibraryItemAuthorsCrossRef(
                                            authorid = authorId,
                                            libraryitemid = libraryItemWithAuthors.libraryItem.id!!
                                        )
                                    )
                                }
                            }
                        }

                    }
                }

                Log.d("LibraryViewModel", "updated " + it.path)
            }
        }
    }
    private fun readBookInfo(contentUriPath: String): BookInfo? {
        return EbookHelper.getBookInfoTemporary(contentUriPath)
    }

    fun getAuthorByName(firstname: String?, middlename: String?, lastname: String?): Author? {
        return authorsRepository.getByName(firstname, middlename, lastname).firstOrNull()
    }

    fun getLibraryItemByPath(path: String): LibraryItemWithAuthors? {
        val item = libraryItemRepository.getByPathWithAuthors(path)
        return item.firstOrNull()
    }
}