package com.kontranik.koreader.compose.ui.reader

import android.content.Context
import android.graphics.Bitmap
import android.os.BatteryManager
import android.text.style.ImageSpan
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
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
import com.kontranik.koreader.database.repository.BookStatusRepository
import com.kontranik.koreader.database.repository.BookmarksRepository
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.Page
import com.kontranik.koreader.model.PageViewSettings
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.PageLoader
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min


data class PageLoaderToken(
    var pageSize: IntSize = IntSize(0, 0),
)

class BookReaderViewModel(
    private val mRepository: BookStatusRepository,
    private val mBookmarkRepository: BookmarksRepository
) : ViewModel()  {

    val book = MutableLiveData<Book?>(null)

    private var pageLoader = PageLoader()

    val pageLoaderToken = PageLoaderToken()

    private var changingPage = false

    val bookPath = MutableLiveData<String?>(null)

    var pageViewContent: MutableLiveData<CharSequence?> = MutableLiveData()
    var pageViewSettings = MutableLiveData(
        PageViewSettings()
    )
    var themeColors = MutableLiveData(
        defaultColors.first()
    )

    private val path = MutableLiveData<String>()
    val savedBookStatus: LiveData<BookStatus?> = path.switchMap<String?, BookStatus?> {
        it?.let { it1 ->
            mRepository.getLiveDataBookStatusByPath(it1)
        }
    }

    var screenBrightnessLevel: Float = 1f


    var infoTextLeft: MutableLiveData<CharSequence?> = MutableLiveData()
    var infoTextRight: MutableLiveData<CharSequence?> = MutableLiveData()
    var infoTextSystemstatus: MutableLiveData<CharSequence?> = MutableLiveData()

    var note = mutableStateOf<String?>(null)


    init {
        loadPrefs()

        book.observeForever {
            path.value = book.value?.fileLocation
        }
    }

    fun recalcCurrentPage() {
        if (book.value != null)
            updateView(getCur(recalc = true))
    }

    fun loadBook(context: Context) {
        try {
            println("loadBook. path = ${bookPath.value}")
            if (bookPath.value != null && book.value?.fileLocation != bookPath.value ) {
                if (!FileHelper.contentFileExist(KoReaderApplication.getContext(), bookPath.value)) {
                    Toast.makeText(
                        context,
                        KoReaderApplication.getContext().resources.getString(R.string.can_not_load_book, bookPath.value),
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                Toast.makeText(
                    context,
                    KoReaderApplication.getContext().resources.getString(R.string.loading_book),
                    Toast.LENGTH_SHORT
                ).show()
                book.value = Book(bookPath.value!!)
            }
        } catch (e: Exception) {
            Log.e("tag", e.stackTraceToString())
        }
    }

    fun goToPositionByBookStatus(pageView: TextView, bookStatus: BookStatus?) {
        Log.d("goToPositionByBookStat.", "bookstatus: $bookStatus")
        if (book.value != null) {
            val startPosition: BookPosition = if (bookStatus == null) {
                BookPosition()
            } else {
                BookPosition(bookStatus.position_section, bookStatus.position_offset)
            }
            Log.d("goToPositionByBookStat.", "startPosition: $startPosition")
            book.value!!.curPage = Page(null, startPosition, BookPosition())
            updateView(
                getCur(recalc = true)
            )
        }
    }

    private fun getCur(recalc: Boolean): Page? {
        return getPage(BookPosition(book.value!!.curPage.startBookPosition),false, recalc)
    }

    private fun getNext(): Page? {
        val bookPosition =  BookPosition(book.value!!.curPage.endBookPosition.section, offSet = book.value!!.curPage.endBookPosition.offSet+1)
        bookPosition.offSet += 1
        return getPage(BookPosition(bookPosition), revers = false, recalc = false)
    }

    private fun getPrev(): Page? {
        println("getPrev")
        val bookPosition =  BookPosition(book.value!!.curPage.startBookPosition)
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
            recalc = recalc)
    }

    private fun updateView(page: Page?) {
        println("updateView")
        if (page != null) {
            book.value?.curPage = Page(page)
            pageViewContent.value = page.content
        } else {
            pageViewContent.value = KoReaderApplication.getContext().getString(R.string.no_page_content)
        }
        updateInfo()
    }

    private fun updateInfo() {
        if (book.value != null) {
            val curSection = book.value!!.getCurSection()
            val curTextPage = book.value!!.getCurTextPage()

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
        val bm = KoReaderApplication.getContext().getSystemService(AppCompatActivity.BATTERY_SERVICE) as BatteryManager
        val batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val strTime: String = simpleDateFormatTime.format(Date().time)
        infoTextSystemstatus.value =
            KoReaderApplication.getContext().resources.getString(R.string.page_info_text_time, strTime, batLevel)
    }

    private fun pageNext(): Boolean {
        if (book.value == null) return false
        updateView(getNext())
        return true
    }

    private fun pagePrev(): Boolean {
        if (book.value == null) return false
        updateView(getPrev())
        return true
    }

    private fun loadPrefs() {
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            PREFS_FILE,
            AppCompatActivity.MODE_PRIVATE
        )
        bookPath.value = settings.getString(PREF_BOOK_PATH, null)
        screenBrightnessLevel = settings.getFloat(PREF_SCREEN_BRIGHTNESS, 1f)
    }

    fun changePath(path: String) {
        bookPath.value = path
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            PREFS_FILE,
            AppCompatActivity.MODE_PRIVATE
        )
        val prefEditor = settings.edit()
        prefEditor.putString(PREF_BOOK_PATH, bookPath.value)
        prefEditor.apply()
    }

    fun savePrefs() {
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            PREFS_FILE,
            AppCompatActivity.MODE_PRIVATE
        )
        val prefEditor = settings.edit()
        prefEditor.putString(PREF_BOOK_PATH, bookPath.value)
        prefEditor.putFloat(
            PREF_SCREEN_BRIGHTNESS, screenBrightnessLevel)
        prefEditor.apply()
    }

    private fun removePathFromPrefs() {
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            PREFS_FILE,
            AppCompatActivity.MODE_PRIVATE
        )
        val prefEditor = settings.edit()
        prefEditor.remove(PREF_BOOK_PATH)
        prefEditor.apply()
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

    private fun savePositionForBook() {
        if (bookPath.value != null
            && book.value?.curPage != null) {
            savePosition()
        }
    }

    private fun savePosition() {
        viewModelScope.launch {
            BooksRoomDatabase.databaseWriteExecutor.execute {
                book.value?.fileLocation?.let {
                    val bookStatus = mRepository.getBookStatusByPath(it)

                    if (bookStatus == null) {
                        Log.d("savePosition", "bookstatus is null")
                        mRepository.insert(BookStatus(book.value!!))
                    } else {
                        Log.d(
                            "savePosition",
                            bookStatus.position_section.toString() + " " + bookStatus.position_offset
                        )
                        val bookPosition =
                            BookPosition(book.value!!.curPage.startBookPosition)
                        bookStatus.updatePosition(bookPosition)
                        mRepository.update(bookStatus)
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
        mBookmarkRepository.insert(
            getBookmarkForCurrentPosition(pageText)
        )
    }

    private fun getBookmarkForCurrentPosition(pageText: String): Bookmark {
        return Bookmark(
            path = book.value!!.fileLocation,
            text = if (pageText.length <= 100)
                pageText else
                pageText.substring(0, 100),
            position_section = book.value!!.curPage.startBookPosition.section,
            position_offset = book.value!!.curPage.startBookPosition.offSet,
        )
    }

    fun loadNote(url: String){
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
            ))
    }

    private fun goToPage(page: Page) {
        book.value?.curPage = page
        recalcCurrentPage()
        savePositionForBook()
    }
}