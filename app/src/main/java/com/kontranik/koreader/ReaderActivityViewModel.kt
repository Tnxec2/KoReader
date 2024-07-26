package com.kontranik.koreader

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.BatteryManager
import android.text.style.ImageSpan
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kontranik.koreader.database.BooksRoomDatabase
import com.kontranik.koreader.database.model.BookStatus
import com.kontranik.koreader.database.model.Bookmark
import com.kontranik.koreader.database.repository.BookStatusRepository
import com.kontranik.koreader.model.*
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.PrefsHelper
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min


class ReaderActivityViewModel(private val mRepository: BookStatusRepository) : ViewModel()  {

    var book: MutableLiveData<Book?> = MutableLiveData()

    private var changingPage = false

    var pageViewContent: MutableLiveData<CharSequence?> = MutableLiveData()
    var pageViewSettings: MutableLiveData<PageViewSettings> = MutableLiveData(
        PageViewSettings(
            textSize = PrefsHelper.textSize,
            lineSpacingMultiplier = PrefsHelper.lineSpacingMultiplier,
            letterSpacing = PrefsHelper.letterSpacing,
            typeFace = PrefsHelper.font.getTypeface(),
            marginTop = PrefsHelper.marginTop,
            marginBottom = PrefsHelper.marginBottom,
            marginLeft = PrefsHelper.marginLeft,
            marginRight = PrefsHelper.marginRight
        )
    )
    var pageViewColorSettings: MutableLiveData<PageViewColorSettings> = MutableLiveData(
        PageViewColorSettings(
            showBackgroundImage = PrefsHelper.showBackgroundImage,
            backgroundImageUri = PrefsHelper.backgroundImageUri,
            backgroundImageTiledRepeat = PrefsHelper.backgroundImageTiledRepeat,
            colorText = PrefsHelper.colorText,
            colorBack = PrefsHelper.colorBack,
            colorLink = PrefsHelper.colorLinkText,
            colorInfoText = PrefsHelper.colorInfoText,
        )
    )
    var infoTextLeft: MutableLiveData<CharSequence?> = MutableLiveData()
    var infoTextRight: MutableLiveData<CharSequence?> = MutableLiveData()
    var infoTextSystemstatus: MutableLiveData<CharSequence?> = MutableLiveData()

    var note: MutableLiveData<String> = MutableLiveData()

    private var width: Int = 100
    private var fullwidth: Int = 100
    private var height: Int = 100
    private var fullheight: Int = 100

    fun loadBook(context: Context) {
        try {
            if (book.value == null || book.value!!.fileLocation != PrefsHelper.bookPath ) {
                if (!FileHelper.contentFileExist(KoReaderApplication.getContext(), PrefsHelper.bookPath)) {
                    Toast.makeText(
                        context,
                        KoReaderApplication.getContext().resources.getString(R.string.can_not_load_book, PrefsHelper.bookPath),
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                Toast.makeText(
                    context,
                    KoReaderApplication.getContext().resources.getString(R.string.loading_book),
                    Toast.LENGTH_SHORT
                ).show()
                book.value = Book(KoReaderApplication.getContext(), PrefsHelper.bookPath!!)
            }
        } catch (e: Exception) {
            Log.e("tag", e.stackTraceToString())
        }

    }

    fun pageNext(pageView: TextView): Boolean {
        if (book.value == null ) return false
        updateView(book.value?.getNext(pageView))
        return true
    }

    fun pagePrev(pageView: TextView): Boolean {
        if (book.value == null) return false
        updateView(book.value!!.getPrev(pageView))
        return true
    }

    fun loadNote(url: String){
        note.value = book.value?.getNote(url)
    }

    fun getBookmarkForCurrentPosition(text: String): Bookmark {
        return Bookmark(
            path = PrefsHelper.bookPath!!,
            text = text,
            position_section = book.value!!.curPage!!.startBookPosition.section,
            position_offset = book.value!!.curPage!!.startBookPosition.offSet,
        )
    }

    private fun isBookmarkOnCurrentPage(bookmark: Bookmark): Boolean {
        if ( book.value?.curPage == null ) return false
        return (
                bookmark.position_section >= book.value!!.curPage!!.startBookPosition.section
                &&
                bookmark.position_section <= book.value!!.curPage!!.endBookPosition.section
                &&
                bookmark.position_offset >= book.value!!.curPage!!.startBookPosition.offSet
                &&
                bookmark.position_offset < book.value!!.curPage!!.endBookPosition.offSet
        )
    }

    fun goToBookmark(pageView: TextView, bookmark: Bookmark) {
        if ( isBookmarkOnCurrentPage(bookmark)) return
        goToPage(pageView, Page(null, BookPosition(bookmark), BookPosition()))
    }

    fun goToSection(pageView: TextView, section: Int) {
        goToPage(
            pageView,
            Page(null, BookPosition(section = section), BookPosition())
        )
    }

    fun goToPage(pageView: TextView, page: Int) {
        goToPage(
            pageView,
            Page(
            null,
            book.value!!.ebookHelper!!.pageScheme.getBookPositionForPage(page),
            BookPosition()
        ))
    }

    private fun goToPage(pageView: TextView, page: Page) {
        book.value?.curPage = page
        recalcCurrentPage(pageView)
        savePositionForBook()
    }

    fun reloadCurrentPage(pageView: TextView) {
        if (book.value != null)
            updateView(book.value?.getCur(pageView, recalc = false))
    }

    fun recalcCurrentPage(pageView: TextView) {
        if (book.value != null)
            updateView(book.value?.getCur(pageView, recalc = true))
    }

    private fun updateView(page: Page?) {
        Log.d("updateView", "page")
        if (page != null) {
            book.value?.curPage = Page(page)
            pageViewContent.value = page.content
        } else {
            pageViewContent.value = KoReaderApplication.getContext().getString(R.string.no_page_content)
        }
        updateInfo()
    }

    private fun updateInfo() {
        if (book.value != null && book.value!!.curPage != null) {
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

    fun updateSystemStatus() {
        val simpleDateFormatTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        val bm = KoReaderApplication.getContext().getSystemService(AppCompatActivity.BATTERY_SERVICE) as BatteryManager
        val batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val strTime: String = simpleDateFormatTime.format(Date().time)
        infoTextSystemstatus.value =
            KoReaderApplication.getContext().resources.getString(R.string.page_info_text_time, strTime, batLevel)
    }

    fun goToPositionByBookStatus(pageView: TextView, bookStatus: BookStatus?) {
        Log.d("goToPositionByBookStat.", "load")
        if (book.value != null) {
            val startPosition: BookPosition = if (bookStatus == null) {
                BookPosition()
            } else {
                BookPosition(bookStatus.position_section, bookStatus.position_offset)
            }
            book.value!!.curPage = Page(null, startPosition, BookPosition())
            updateView(book.value!!.getCur(pageView, recalc = true))
        }
    }

    fun increaseScreenBrghtness(activity: Activity, point: Point) {
        PrefsHelper.increaseScreenBrghtness(activity, point, fullwidth)
    }

    fun decreaseScreenBrghtness(activity: Activity, point: Point) {
        PrefsHelper.decreaseScreenBrghtness(activity, point, fullwidth)
    }

    fun updateSizeInfo(pageView: TextView) {
        val fw = pageView.measuredWidth
        val fh = pageView.measuredHeight
        val w =
            pageView.measuredWidth - pageView.paddingLeft - pageView.paddingRight
        val h =
            pageView.measuredHeight - pageView.paddingTop - pageView.paddingBottom
        if (w != width || h != height) {
            fullwidth = fw
            fullheight = fh
            width = w
            height = h
            recalcCurrentPage(pageView)
        }
    }

    fun getZone(point: Point): ScreenZone {
        return ScreenZone.zone(point, width, height)
    }

    fun finishQuickMenuSettings(
        textSize: Float,
        lineSpacingMultiplier: Float,
        letterSpacing: Float,
        colorTheme: String
    ) {
        if (textSize != PrefsHelper.textSize
            || lineSpacingMultiplier != PrefsHelper.lineSpacingMultiplier
            || letterSpacing != PrefsHelper.letterSpacing
            || colorTheme != PrefsHelper.colorTheme
        ) {
            PrefsHelper.textSize = textSize
            PrefsHelper.lineSpacingMultiplier = lineSpacingMultiplier
            PrefsHelper.letterSpacing = letterSpacing
            PrefsHelper.colorTheme = colorTheme


            pageViewSettings.value = pageViewSettings.value?.also { it ->
                it.textSize = PrefsHelper.textSize
                it.lineSpacingMultiplier = PrefsHelper.lineSpacingMultiplier
                it.letterSpacing = PrefsHelper.letterSpacing
            }

            pageViewColorSettings.value = PrefsHelper.loadColorThemeSettings()
        }
    }

    fun loadSettings(activity: Activity) {
        PrefsHelper.loadSettings(activity)
        pageViewSettings.value = pageViewSettings.value?.also { it ->
            it.textSize = PrefsHelper.textSize
            it.lineSpacingMultiplier = PrefsHelper.lineSpacingMultiplier
            it.letterSpacing = PrefsHelper.letterSpacing
            it.typeFace = PrefsHelper.font.getTypeface()
            it.marginTop = PrefsHelper.marginTop
            it.marginBottom = PrefsHelper.marginBottom
            it.marginLeft = PrefsHelper.marginLeft
            it.marginRight = PrefsHelper.marginRight
        }

        pageViewColorSettings.value = PrefsHelper.loadColorThemeSettings()

    }

    fun loadPrefs(activity: Activity) {
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            ReaderActivity.PREFS_FILE,
            AppCompatActivity.MODE_PRIVATE
        )

        if (settings.contains(PrefsHelper.PREF_BOOK_PATH)) {
            PrefsHelper.bookPath = settings!!.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }

        if (settings.contains(PrefsHelper.PREF_SCREEN_BRIGHTNESS)) {
            PrefsHelper.screenBrightnessLevel = settings!!.getFloat(
                PrefsHelper.PREF_SCREEN_BRIGHTNESS,
                PrefsHelper.systemScreenBrightnessLevel
            )
            PrefsHelper.setScreenBrightness(activity, PrefsHelper.screenBrightnessLevel)
        }
    }

    fun savePrefs() {
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            ReaderActivity.PREFS_FILE,
            AppCompatActivity.MODE_PRIVATE
        )
        val prefEditor = settings.edit()
        prefEditor.putString(PrefsHelper.PREF_BOOK_PATH, PrefsHelper.bookPath)
        prefEditor.putFloat(PrefsHelper.PREF_SCREEN_BRIGHTNESS, PrefsHelper.screenBrightnessLevel)
        prefEditor.apply()
    }

    private fun removePathFromPrefs() {
        val settings = KoReaderApplication.getContext().getSharedPreferences(
            ReaderActivity.PREFS_FILE,
            AppCompatActivity.MODE_PRIVATE
        )
        val prefEditor = settings.edit()
        prefEditor.remove(PrefsHelper.PREF_BOOK_PATH)
        prefEditor.apply()
    }

    fun resetQuickSettings() {
        pageViewSettings.value = pageViewSettings.value?.also {
            it.textSize = PrefsHelper.textSize
            it.lineSpacingMultiplier = PrefsHelper.lineSpacingMultiplier
            it.letterSpacing = PrefsHelper.letterSpacing
        }

        pageViewColorSettings.value = PageViewColorSettings(
            showBackgroundImage = PrefsHelper.showBackgroundImage,
            backgroundImageUri = PrefsHelper.backgroundImageUri,
            backgroundImageTiledRepeat = PrefsHelper.backgroundImageTiledRepeat,
            colorText = PrefsHelper.colorText,
            colorBack = PrefsHelper.colorBack,
            colorLink = PrefsHelper.colorLinkText,
            colorInfoText = PrefsHelper.colorInfoText,
        )
    }

    fun setBookPath(context: Context, uriString: String) {
        PrefsHelper.bookPath = uriString
        savePrefs()
        loadBook(context)
    }

    fun deleteBook(uriString: String?): Boolean {
        val uri = Uri.parse(uriString)
        val doc = DocumentFile.fromSingleUri(KoReaderApplication.getContext().applicationContext, uri)
        if (doc != null) {
            if (doc.delete()) {
                if (uriString == PrefsHelper.bookPath) {
                    PrefsHelper.bookPath = null
                    book.value = null
                    removePathFromPrefs()
                }
                return true
            } else {
                Toast.makeText(KoReaderApplication.getContext().applicationContext, "Can't delete book", Toast.LENGTH_LONG)
                    .show()
            }
        }
        return false
    }

    fun goToNextPage(textView: TextView) {
        if (changingPage) return
        changingPage = true
        if (pageNext(textView)) savePositionForBook()
        changingPage = false
    }

    fun doPagePrev(textView: TextView) {
        if (changingPage) return
        changingPage = true
        if (pagePrev(textView)) savePositionForBook()
        changingPage = false
    }

    private fun savePositionForBook() {
        if (PrefsHelper.bookPath != null
            && book.value?.curPage != null) {
            savePosition()
        }
    }

    private fun savePosition() {
        viewModelScope.launch {
            BooksRoomDatabase.databaseWriteExecutor.execute {
                val bookStatus = mRepository.getBookStatusByPath(book.value!!.fileLocation)

                if (bookStatus == null) {
                    Log.d("savePosition", "bookstatus is null")
                    mRepository.insert(BookStatus(book.value!!))
                } else {
                    Log.d(
                        "savePosition",
                        bookStatus.position_section.toString() + " " + bookStatus.position_offset
                    )
                    val bookPosition =
                        if (book.value!!.curPage == null) BookPosition() else BookPosition(book.value!!.curPage!!.startBookPosition)
                    bookStatus.updatePosition(bookPosition)
                    mRepository.update(bookStatus)
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
}

class ReaderActivityViewModelFactory(private val repository: BookStatusRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReaderActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReaderActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}