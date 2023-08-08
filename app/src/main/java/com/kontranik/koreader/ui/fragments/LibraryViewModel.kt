package com.kontranik.koreader.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.DocumentsContract.Document
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.kontranik.koreader.App
import com.kontranik.koreader.R
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.LibraryItem
import com.kontranik.koreader.database.model.LibraryItemAuthorsCrossRef
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import com.kontranik.koreader.database.repository.AuthorsRepository
import com.kontranik.koreader.database.repository.LibraryItemRepository
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.parser.epubreader.EpubHelper
import com.kontranik.koreader.parser.fb2reader.FB2Helper
import com.kontranik.koreader.ui.adapters.PagingLibraryItemAdapter
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.Closeable


class LibraryViewModel(
    private val libraryItemRepository: LibraryItemRepository,
    private val authorsRepository: AuthorsRepository,
    private val applicationScope: CoroutineScope) : ViewModel() {

    private val libraryTitleSearchFilter = MutableLiveData<String?>()
    private var author: Author? = null

    val libraryTitlePageByFilter = libraryTitleSearchFilter.switchMap(
        :: getTitlePageByFilter
    )

    private fun getTitlePageByFilter(searchFilter: String?) = Pager(PagingConfig(pageSize = 15)) {
        libraryItemRepository.pageLibraryItem(author, searchFilter) }
        .liveData
        .cachedIn(viewModelScope)

    fun loadTitlePageInit(author: Author?) = apply {
        this.author = author
        if ( libraryTitlePageByFilter.value == null) {
            this.libraryTitleSearchFilter.value = null
        }
    }

    fun changeTitleSearchText(text: String?) {
        val altVal = this.libraryTitleSearchFilter.value
        if ( altVal == null || altVal != text) {
            libraryTitleSearchFilter.postValue(text)
        }
    }
    private val libraryAuthorSearchFilter = MutableLiveData<String?>()

    val libraryAuthorPageByFilter = libraryAuthorSearchFilter.switchMap(
        :: getAuthorPageByFilter
    )

    private fun getAuthorPageByFilter(searchFilter: String?) = Pager(PagingConfig(pageSize = 15)) {
        authorsRepository.pageAuthor(searchFilter) }
        .liveData
        .cachedIn(viewModelScope)

    fun loadAuthorPageInit() = apply {
        if ( libraryAuthorPageByFilter.value == null) {
            this.libraryAuthorSearchFilter.value = null
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
        libraryItemWithAuthors.libraryItem.id?.let {
            libraryItemRepository.delete(it)
        }
        libraryItemWithAuthors.authors.forEach { author ->
            val authorId = author.id
            authorId?.let{id ->
                val count = libraryItemRepository.getCountByAuthorId(id)
                if (count == 0L) author.id?.let { authorsRepository.delete(id) }
            }
        }
        libraryItemRepository.deleteCrossRefLibraryItem(libraryItemWithAuthors.libraryItem)
    }

    fun deleteAll() = viewModelScope.launch {
        libraryItemRepository.deleteAll()
        authorsRepository.deleteAll()
        libraryItemRepository.deleteAllCrossRef()
    }

    fun openDeleteLibraryItemDialog(adapter: PagingLibraryItemAdapter, position: Int, libraryItem: LibraryItemWithAuthors, context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.library_delete_item_dialog_title))
            .setMessage(context.getString(R.string.library_delete_item_dialog_message)) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.ok
            ) { dialog, which ->
                delete(libraryItem)
                adapter.deleteItem(position)

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.cancel) { dialogInterface, i ->
                adapter.cancelDeletion(position)
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }


    val CHANNEL_ID = "LIBRARY"
    val notificationId = 0

    val refreshInProgress = MutableLiveData<Boolean>(false)

    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = App.getContext().getString(R.string.channel_name_library)
            val descriptionText = App.getContext().getString(R.string.channel_description_library)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                App.getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun readRecursive(context: Context, scanPoints: MutableList<String>) {
        scanPoints.forEach { scanPoint ->
            val resolver: ContentResolver = context.contentResolver

            val directoryUri = Uri.parse(scanPoint)
                ?: throw IllegalArgumentException("Must pass URI of directory to open")
            val documentsTree = DocumentFile.fromTreeUri(context, directoryUri)
            if ( documentsTree == null || ! documentsTree.isDirectory || ! documentsTree.canRead() ) {
                //
            } else {
                applicationScope.launch {
                    var builder = NotificationCompat.Builder(App.getContext(), CHANNEL_ID)
                        .setStyle(NotificationCompat.MessagingStyle("Me")
                            .setConversationTitle("Library refresh"))
                        .setSmallIcon(R.drawable.baseline_notifications_24)
                        .setContentText("Refreshing of library started")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    notify(context, builder)
                    refreshInProgress.postValue(true)
                    traverseDirectoryEntries(resolver, directoryUri)
                    refreshInProgress.postValue(false)

                    builder = NotificationCompat.Builder(App.getContext(), CHANNEL_ID)
                        .setStyle(NotificationCompat.MessagingStyle("Me")
                            .setConversationTitle("Library refresh"))
                        .setSmallIcon(R.drawable.baseline_notifications_24)
                        .setContentText("Refreshing of library ended")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    notify(context, builder)
                }
            }
        }
    }

    private fun notify(context: Context, builder: NotificationCompat.Builder) {
        NotificationManagerCompat.from(App.getContext()).apply {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            } else {
                notify(notificationId, builder.build())
            }
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
                        name.endsWith(".epub", true)
                        || name.endsWith(".fb2", true)
                        || name.endsWith(".fb2.zip", true)
                    ) {
                        val documentUri = DocumentsContract.buildDocumentUriUsingTree(rootUri, docId)
                        readBookInfo(documentUri, docId)
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

    private fun readBookInfo(uri: Uri, path: String) {
            val item = libraryItemRepository.getByPath(path)
            if (item.isEmpty()) {
                val bookInfo = readBookInfo(
                    App.getContext(),
                    uri.toString()
                )
                Log.d("LibraryViewModel", "bookInfo " + bookInfo.toString())
                bookInfo?.let {
                    val libraryItemId = libraryItemRepository.insert(LibraryItem(it))
                    if (libraryItemId != null) {
                        bookInfo.authors?.forEach { bookInfoAuthor ->
                            val dbAuthorList = authorsRepository.getByName(
                                firstname = bookInfoAuthor.firstname,
                                middlename = bookInfoAuthor.middlename,
                                lastname = bookInfoAuthor.lastname)
                            Log.d("LibraryVieWModel", "dbAuthorList size: ${dbAuthorList.size}")
                            if (dbAuthorList.isEmpty()) {
                                val authorId = authorsRepository.insert(bookInfoAuthor)
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
                    }
                    Log.d("LibraryViewModel", "save " + it.path)
                }
            }
        }

    fun updateLibraryItem(position: Int, libraryItemWithAuthors: LibraryItemWithAuthors) {
        applicationScope.launch {
            val bookInfo = readBookInfo(
                App.getContext(),
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
}

    private fun readBookInfo(mContext: Context, contentUriPath: String): BookInfo? {
        val result: BookInfo? = if (contentUriPath.endsWith(".epub", ignoreCase = true)) {
            try {
                EpubHelper(mContext, contentUriPath).getBookInfoTemporary(contentUriPath)
            } catch (e: Exception) {
                null
            }
        } else if (contentUriPath.endsWith(".fb2", ignoreCase = true)
            || contentUriPath.endsWith(".fb2.zip", ignoreCase = true)) {
            try {
                FB2Helper(mContext, contentUriPath).getBookInfoTemporary(contentUriPath)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

        if ( result != null) {
            if (result.cover == null) result.cover = ImageUtils.getBitmap(mContext, ImageEnum.Ebook)
        }
        return result
}



class LibraryViewModelFactory(
    private val libraryItemRepository: LibraryItemRepository,
    private val authorsRepository: AuthorsRepository,
    private val applicationScope: CoroutineScope
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LibraryViewModel(libraryItemRepository, authorsRepository, applicationScope) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}