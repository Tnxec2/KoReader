package com.kontranik.koreader.compose.ui.reader

import android.content.Context
import android.graphics.Bitmap
import android.os.BatteryManager
import android.text.style.ImageSpan
import android.util.Log
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min


class BookReaderViewModel(
    private val bookStatusRepository: BookStatusRepository,
    private val bookmarkRepository: BookmarksRepository
) : ViewModel() {

    val bookPath = MutableLiveData<String?>(null)
    val book = MutableLiveData<Book?>(null)
    private var curPage = MutableLiveData(Page(startBookPosition = BookPosition()))

    var mAllBookmarksWithOffsetOnPage = bookPath.asFlow().combine(curPage.asFlow()) {
        path, page ->
        path?.let {
            bookmarkRepository
                .getByPathAndPosition(
                    it,
                    page.startBookPosition.section,
                    page.startBookPosition.offSet,
                    page.endBookPosition.offSet
                ).first().map { b ->
                    b.toBookmarkWithOffsetOnPage(page)
                }
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
        KoReaderApplication.getApplicationScope().launch {
            bookPath.asFlow().collect {
                println("collect bookpath $it")
                it?.let { path ->
                    loadBook(path)
                }
            }
        }

        viewModelScope.launch {
            book.asFlow().collect {
                getCur()?.let { curPage.postValue(it) }
            }
        }

        viewModelScope.launch {
            curPage.asFlow().collect {
                updateView(it)
            }
        }

        loadPrefs()
    }

    fun recalcCurrentPage() {
        if (book.value != null) {
            getCur()?.let { curPage.postValue(it) }
        }
    }

    private fun loadBook(path: String?) {
        try {
            if (path != null && book.value?.fileLocation != path) {
                if (!FileHelper.contentFileExist(KoReaderApplication.getContext(), path)) {
//                    Toast.makeText(
//                        context,
//                        KoReaderApplication.getContext().resources.getString(R.string.can_not_load_book, bookPath.value),
//                        Toast.LENGTH_LONG
//                    ).show()
                    return
                }

                val newBook = Book(path)

                val status = bookStatusRepository.getBookStatusByPath(newBook.fileLocation)
                if (status != null) {
                    val bookStatus = bookStatusRepository.getBookStatusByPath(newBook.fileLocation)
                    if (bookStatus != null) {
                        bookStatusRepository.updateLastOpenTime(bookStatus.id, Date().time)
                    } else {
                        bookStatusRepository.insert(
                            BookStatus(
                                newBook,
                                Page(null, BookPosition(), BookPosition())
                            )
                        )
                    }
                }

                val startPosition: BookPosition = if (status == null) {
                    BookPosition()
                } else {
                    BookPosition(status.position_section, status.position_offset)
                }

                curPage.postValue(Page(null, startPosition, BookPosition()))
                book.postValue(newBook)
            }
        } catch (e: Exception) {
            Log.e("tag", e.stackTraceToString())
        }
    }

    private fun getCur(): Page? {
        return getPage(curPage.value!!.startBookPosition.copy(), revers = false, recalc = true)
    }

    fun goToNextPage() {
        if (changingPage) return
        changingPage = true
        if (pageNext()) savePositionForBook()
        changingPage = false
    }

    fun doPagePrev() {
        if (changingPage) return
        changingPage = true
        if (pagePrev()) savePositionForBook()
        changingPage = false
    }

    private fun pageNext(): Boolean {
        if (book.value == null) return false
        getNext()?.let { curPage.postValue(it) }
        return true
    }

    private fun pagePrev(): Boolean {
        if (book.value == null) return false
        getPrev()?.let { curPage.postValue(it) }
        return true
    }

    private fun getNext(): Page? {
        val bookPosition = BookPosition(
            curPage.value!!.endBookPosition.section,
            offSet = curPage.value!!.endBookPosition.offSet + 1
        )
        bookPosition.offSet += 1
        return getPage(BookPosition(bookPosition), revers = false, recalc = false)
    }

    private fun getPrev(): Page? {
        println("getPrev")
        val bookPosition = BookPosition(curPage.value!!.startBookPosition)
        bookPosition.offSet -= 1
        return getPage(BookPosition(bookPosition), revers = true, recalc = false)
    }

    private fun getPage(bookPosition: BookPosition, revers: Boolean, recalc: Boolean): Page? {
        return pageLoader.getPage(
            book.value,
            pageViewSettings.value!!,
            themeColors.value!!,
            BookPosition(bookPosition),
            revers = revers,
            recalc = recalc
        )
    }

    private fun updateView(page: Page?) {
        println("updateView")
        if (page != null) {
            pageViewContent.postValue(page.content)
        } else {
            pageViewContent.postValue(
                KoReaderApplication.getContext().getString(R.string.no_page_content)
            )
        }
        updateInfo()
    }

    private fun updateInfo() {
        if (book.value != null) {
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
        } else {
            infoTextRight.value = KoReaderApplication.getContext().getString(R.string.no_book)
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
        val prefEditor = settings.edit()
        prefEditor.putString(PREF_BOOK_PATH, path)
        prefEditor.apply()
        bookPath.postValue(path)
    }

    fun changeBrightness() {
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            PREFS_FILE,
            Context.MODE_PRIVATE
        )
        val prefEditor = settings.edit()
        prefEditor.putFloat(
            PREF_SCREEN_BRIGHTNESS, screenBrightnessLevel
        )
        prefEditor.apply()
    }

    private fun removePathFromPrefs() {
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            PREFS_FILE,
            Context.MODE_PRIVATE
        )
        val prefEditor = settings.edit()
        prefEditor.remove(PREF_BOOK_PATH)
        prefEditor.apply()
    }

    private fun savePositionForBook() {
        if (bookPath.value != null && curPage.value != null) {
            BooksRoomDatabase.databaseWriteExecutor.execute {
                bookPath.value?.let { path ->
                    bookStatusRepository.getBookStatusByPath(path).let { bookStatus ->
                        if (bookStatus == null) {
                            Log.d(
                                "BookReaderViewModel",
                                "savePosition: bookstatus is null, insert new"
                            )
                            bookStatusRepository.insert(BookStatus(book.value!!, curPage.value!!))
                        } else {
                            Log.d(
                                "BookReaderViewModel",
                                "savePosition: section: ${bookStatus.position_section}, offset: ${bookStatus.position_offset}"
                            )
                            val bookPosition =
                                BookPosition(curPage.value!!.startBookPosition)
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
        curPage.value?.content?.let {
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
            position_section = curPage.value!!.startBookPosition.section,
            position_offset = curPage.value!!.startBookPosition.offSet,
        )
    }

    fun loadNote(url: String) {
        note.value = book.value?.getNote(url)
    }

    fun goToSection(section: Int) {
        goToPage(
            Page(null, BookPosition(section = section), BookPosition())
        )
    }

    fun goToPage(page: Int) {
        goToPage(
            Page(
                null,
                book.value!!.ebookHelper!!.pageScheme.getBookPositionForPage(page),
                BookPosition()
            )
        )
    }

    private fun goToPage(page: Page) {
        curPage.value = page
        recalcCurrentPage()
        savePositionForBook()
    }

    fun getCurSection(): Int {
        return curPage.value!!.endBookPosition.section
    }

    fun getCurTextPage(): Int {
        var curTextPage = 0
        for (i in 0 until curPage.value!!.endBookPosition.section) {
            if (book.value!!.getPageScheme()?.scheme?.get(i) != null)
                curTextPage += book.value!!.getPageScheme()!!.scheme[i]!!.countTextPages
        }
        curTextPage += (curPage.value!!.endBookPosition.offSet / BookPageScheme.CHAR_PER_PAGE)
        return curTextPage
    }

    private fun isBookmarkOnCurrentPage(bookmark: Bookmark): Boolean {
        return curPage.value?.let {
            bookmark.position_section >= curPage.value!!.startBookPosition.section
            &&
            bookmark.position_section <= curPage.value!!.endBookPosition.section
            &&
            bookmark.position_offset >= curPage.value!!.startBookPosition.offSet
            &&
            bookmark.position_offset < curPage.value!!.endBookPosition.offSet
        } ?: false
    }

    fun goToBookmark(bookmark: Bookmark) {
        if (isBookmarkOnCurrentPage(bookmark)) return
        goToPage(Page(null, BookPosition(bookmark), BookPosition()))
    }

    fun addBookmark(start: Int, text: CharSequence) {
        println("addBookmark: start: $start, curpage.offset: ${curPage.value!!.startBookPosition.offSet}")
        bookmarkRepository.insert(
        Bookmark(
            path = book.value!!.fileLocation,
            text = text.toString(),
            position_section = curPage.value!!.startBookPosition.section,
            position_offset = curPage.value!!.startBookPosition.offSet + start,
        ))
        recalcCurrentPage()
    }
}