package com.kontranik.koreader.compose.ui.reader

import android.content.Context
import android.graphics.Bitmap
import android.os.BatteryManager
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.ui.settings.PREFS_FILE
import com.kontranik.koreader.compose.ui.settings.PREF_BOOK_PATH
import com.kontranik.koreader.compose.ui.settings.PREF_SCREEN_BRIGHTNESS
import com.kontranik.koreader.compose.ui.settings.defaultColors
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.model.BookStatus
import com.kontranik.koreader.database.model.Bookmark
import com.kontranik.koreader.database.model.toBookmarkWithOffsetOnPage
import com.kontranik.koreader.database.repository.BookStatusRepository
import com.kontranik.koreader.database.repository.BookmarksRepository
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookPageScheme
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.Page
import com.kontranik.koreader.model.PageViewSettings
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.PageLoader
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min
import androidx.core.content.edit


class BookReaderViewModel(
    private val bookStatusRepository: BookStatusRepository,
    private val bookmarkRepository: BookmarksRepository
) : ViewModel() {

    val bookPath = MutableLiveData<String?>(null)
    val book = MutableLiveData<Book?>(null)
    private var currentPage = MutableLiveData(Page())

    var mAllBookmarksWithOffsetOnPage = bookPath
        .asFlow()
        .filterNotNull()
        .combine(currentPage.asFlow()) { path, page ->
            bookmarkRepository
                .getByPathAndPosition(
                    path,
                    page.pageStartPosition.section,
                    page.pageStartPosition.offSet,
                    page.pageEndPosition.offSet
                )
                .first()
                .map { b ->
                    b.toBookmarkWithOffsetOnPage(page)
                }
        }.asLiveData()

    private var pageLoader = PageLoader()

    private var changingPage = false

    var pageViewContent: MutableLiveData<CharSequence?> = MutableLiveData()

    var pageViewSettings = MutableLiveData(
        PageViewSettings()
    )
    var themeColors = MutableLiveData(
        defaultColors.first()
    )

    var screenBrightnessLevel: Float = 1f

    var infoTextLeft: MutableLiveData<CharSequence?> = MutableLiveData()
    var infoTextRight: MutableLiveData<CharSequence?> = MutableLiveData()
    var infoTextSystemstatus: MutableLiveData<CharSequence?> = MutableLiveData()

    var note = mutableStateOf<String?>(null)


    init {
        initializeObservers()
        loadPrefs()
    }

    private fun initializeObservers() {
        KoReaderApplication.getApplicationScope().launch {
            bookPath
                .asFlow()
                .filterNotNull()
                .collect { path ->
                    loadBook(path)
                }

        }

        viewModelScope.launch {
            book
                .asFlow()
                .collect {
                    getCur()?.let { currentPage.postValue(it) }
                }
        }

        viewModelScope.launch {
            currentPage
                .asFlow()
                .collect {
                    updateView(it)
                }
        }
    }

    fun recalcCurrentPage() {
        if (book.value != null) {
            getCur()?.let { currentPage.postValue(it) }
        }
    }

    private fun loadBook(path: String?) {
        try {
            currentPage.postValue(Page(content = SpannableStringBuilder("load book...")))
            path?.takeIf { it != book.value?.fileLocation }?.let { mPath ->
                if (!FileHelper.contentFileExist(KoReaderApplication.getContext(), mPath)) {
                    return
                }

                val newBook = Book(mPath)
                val status = updateBookStatus(newBook)

                val startPosition: BookPosition = if (status == null) {
                    BookPosition()
                } else {
                    BookPosition(status.position_section, status.position_offset + 1)
                }

                currentPage.postValue(
                    Page(pageStartPosition = startPosition, pageEndPosition = BookPosition())
                )
                book.postValue(newBook)
            }
        } catch (e: Exception) {
            Log.e("BookReaderViewModel", e.stackTraceToString())
        }
    }

    private fun updateBookStatus(newBook: Book): BookStatus? {
        val status = bookStatusRepository.getBookStatusByPath(newBook.fileLocation)
        if (status != null) {
            val bookStatus = bookStatusRepository.getBookStatusByPath(newBook.fileLocation)
            if (bookStatus != null) {
                bookStatusRepository.updateLastOpenTime(bookStatus.id, Date().time)
            } else {
                bookStatusRepository.insert(
                    BookStatus(
                        newBook,
                        Page(
                            content = null,
                            pageStartPosition = BookPosition(),
                            pageEndPosition = BookPosition()
                        )
                    )
                )
            }
        }
        return status
    }

    private fun getCur(): Page? {
        return getPage(null, currentPage.value!!.pageStartPosition, revers = false, recalc = true)
    }

    fun goToNextPage() = changePage { pageNext() }

    fun doPagePrev() = changePage { pagePrev() }

    private fun changePage(action: () -> Boolean) {
        if (changingPage) return
        changingPage = true
        if (action()) savePositionForBook()
        changingPage = false
    }

    private fun pageNext(): Boolean {
        if (book.value == null) return false
        getNext()?.let { currentPage.postValue(it) }
        return true
    }

    private fun pagePrev(): Boolean {
        if (book.value == null) return false
        getPrev()?.let { currentPage.postValue(it) }
        return true
    }

    private fun getNext(): Page? {
        return currentPage.value?.let { curPage ->
            val bookPosition = BookPosition(
                curPage.pageEndPosition.section,
                offSet = curPage.pageEndPosition.offSet + 1
            )
            getPage(curPage.pageIndex+1, bookPosition, revers = false, recalc = false)
        }
    }

    private fun getPrev(): Page? {
        currentPage.value?.let { curPage ->
            val bookPosition = BookPosition(
                curPage.pageStartPosition.section,
                curPage.pageStartPosition.offSet - 1
            )
            return getPage(curPage.pageIndex-1, bookPosition, revers = true, recalc = false)
        }
        return null
    }

    private fun getPage(
        pageIndex: Int?,
        bookPosition: BookPosition,
        revers: Boolean,
        recalc: Boolean): Page? {
        return pageLoader.getPage(
            book.value,
            pageViewSettings.value!!,
            themeColors.value!!,
            pageIndex,
            bookPosition,
            revers = revers,
            recalc = recalc
        )
    }

    private fun updateView(page: Page?) {
        println("updateView")
        pageViewContent.postValue(
            page?.content ?:
            KoReaderApplication.getContext().getString(R.string.no_page_content)
        )
        updateInfo()
    }

    private fun updateInfo() {
        book.value?.let {
            val curSection = getCurSection()
            val curTextPage = getCurTextPage()

            infoTextLeft.value =
                KoReaderApplication.getContext().getString(
                    R.string.page_info_text_left,
                    min(curTextPage, book.value!!.getPageScheme()!!.countTextPages),
                    book.value!!.getPageScheme()!!.countTextPages,
                    if (book.value!!.getPageScheme()!!.countTextPages == 0) 0
                    else min(curTextPage * 100 / book.value!!.getPageScheme()!!.countTextPages, 100)
                )

            infoTextRight.value =
                KoReaderApplication.getContext().getString(
                    R.string.page_info_text_right,
                    curSection,
                    book.value!!.getPageScheme()!!.sectionCountWithOutNotes,
                    book.value!!.getPageScheme()!!.sectionCount
                )
        } ?: run {
            infoTextLeft.value = KoReaderApplication.getContext().getString(R.string.no_book)
        }
        updateSystemStatus()
    }

    private fun updateSystemStatus() {
        val simpleDateFormatTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        val bm = KoReaderApplication.getContext()
            .getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val strTime: String = simpleDateFormatTime.format(Date().time)
        infoTextSystemstatus.value =
            KoReaderApplication.getContext().resources.getString(
                R.string.page_info_text_time,
                strTime,
                batLevel
            )
    }

    private fun loadPrefs() {
        println("loadPrefs")
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            PREFS_FILE,
            Context.MODE_PRIVATE
        )
        bookPath.postValue(settings.getString(PREF_BOOK_PATH, null))
        screenBrightnessLevel = settings.getFloat(PREF_SCREEN_BRIGHTNESS, 1f)
    }

    fun changePath(path: String) {
        println("changePath")
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            PREFS_FILE,
            Context.MODE_PRIVATE
        )
        settings.edit {
            putString(PREF_BOOK_PATH, path)
        }
        bookPath.postValue(path)
    }

    fun changeBrightness() {
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            PREFS_FILE,
            Context.MODE_PRIVATE
        )
        settings.edit {
            putFloat(
                PREF_SCREEN_BRIGHTNESS, screenBrightnessLevel
            )
        }
    }

    private fun removePathFromPrefs() {
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            PREFS_FILE,
            Context.MODE_PRIVATE
        )
        settings.edit {
            remove(PREF_BOOK_PATH)
        }
    }

    private fun savePositionForBook() {
        bookPath.value?.let { path ->
            currentPage.value?.let { page ->
                BooksRoomDatabase.databaseWriteExecutor.execute {
                    bookStatusRepository.getBookStatusByPath(path).let { bookStatus ->
                        if (bookStatus == null) {
                            Log.d(
                                "BookReaderViewModel",
                                "savePosition: bookstatus is null, insert new"
                            )
                            bookStatusRepository.insert(BookStatus(book.value!!, page))
                        } else {
                            Log.d(
                                "BookReaderViewModel",
                                "savePosition: section: ${bookStatus.position_section}, offset: ${bookStatus.position_offset}"
                            )
                            val bookPosition =
                                BookPosition(page.pageStartPosition)
                            bookStatus.updatePosition(bookPosition)
                            bookStatusRepository.update(bookStatus)
                        }
                    }

                }
            }
        }
    }

    fun getImageByteArray(imageSpan: ImageSpan): ByteArray? {
        return if (imageSpan.source != null) {
            book.value?.getImageByteArray(imageSpan.source!!)
        } else {
            val bitmap = ImageUtils.drawableToBitmap(imageSpan.drawable)
            val byteArrayStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayStream)
            byteArrayStream.toByteArray()
        }
    }

    fun addBookmark(pageText: String) {
        bookmarkRepository.insert(
            getBookmarkForCurrentPosition(pageText)
        )
    }

    fun addBookmarkForCurrentPage() {
        currentPage.value?.content?.let {
            bookmarkRepository.insert(
                getBookmarkForCurrentPosition(it.toString())
            )
        }
    }

    private fun getBookmarkForCurrentPosition(pageText: String): Bookmark {
        return Bookmark(
            path = book.value!!.fileLocation,
            text = if (pageText.length <= 100)
                pageText else
                pageText.substring(0, 100),
            position_section = currentPage.value!!.pageStartPosition.section,
            position_offset = currentPage.value!!.pageStartPosition.offSet,
        )
    }

    fun loadNote(url: String) {
        note.value = book.value?.getNote(url)
    }

    fun goToSection(section: Int) {
        goToPage(
            Page(pageStartPosition = BookPosition(section = section))
        )
    }

    fun goToPage(page: Int) {
        goToPage(
            Page(pageStartPosition =  book.value!!.ebookHelper!!.pageScheme.getBookPositionForPage(page))
        )
    }

    private fun goToPage(page: Page) {
        currentPage.value = page
        recalcCurrentPage()
        savePositionForBook()
    }

    fun getCurSection(): Int {
        return currentPage.value!!.pageEndPosition.section
    }

    fun getCurTextPage(): Int {
        var curTextPage = 0
        for (i in 0 until currentPage.value!!.pageEndPosition.section) {
            if (book.value!!.getPageScheme()?.scheme?.get(i) != null)
                curTextPage += book.value!!.getPageScheme()!!.scheme[i]!!.countTextPages
        }
        curTextPage += (currentPage.value!!.pageEndPosition.offSet / BookPageScheme.CHAR_PER_PAGE)
        return curTextPage
    }

    private fun isBookmarkOnCurrentPage(bookmark: Bookmark): Boolean {
        return currentPage.value?.let {
            bookmark.position_section >= currentPage.value!!.pageStartPosition.section
            &&
            bookmark.position_section <= currentPage.value!!.pageEndPosition.section
            &&
            bookmark.position_offset >= currentPage.value!!.pageStartPosition.offSet
            &&
            bookmark.position_offset < currentPage.value!!.pageEndPosition.offSet
        } ?: false
    }

    fun goToBookmark(bookmark: Bookmark) {
        if (isBookmarkOnCurrentPage(bookmark)) return
        goToPage(Page(pageStartPosition = BookPosition(bookmark)))
    }

    fun addBookmark(start: Int, text: CharSequence) {
        Log.d("BOOKREADER", "addBookmark: start: $start, curpage.offset: ${currentPage.value!!.pageStartPosition.offSet}")
        bookmarkRepository.insert(
        Bookmark(
            path = book.value!!.fileLocation,
            text = text.toString(),
            position_section = currentPage.value!!.pageStartPosition.section,
            position_offset = currentPage.value!!.pageStartPosition.offSet + start,
        ))
        recalcCurrentPage()
    }

    fun changeReaderSize(size: IntSize) {
        if (pageViewSettings.value!!.pageSize.width != size.width ||
            pageViewSettings.value!!.pageSize.height != size.height
        ) {
            pageViewSettings.value =
                pageViewSettings.value!!.copy(
                    pageSize = size)
            recalcCurrentPage()
        }
    }
}