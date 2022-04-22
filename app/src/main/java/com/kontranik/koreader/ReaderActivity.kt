package com.kontranik.koreader

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.database.BookmarksViewModel
import com.kontranik.koreader.databinding.ActivityReaderMainBinding
import com.kontranik.koreader.model.*
import com.kontranik.koreader.reader.*
import com.kontranik.koreader.utils.*
import java.io.ByteArrayOutputStream

import java.io.InputStream
import java.util.*


class ReaderActivity :
    AppCompatActivity(),
    QuickMenuFragment.QuickMenuDialogListener,
    BookmarkListFragment.BookmarkListDialogListener,
    GotoMenuFragment.GotoMenuDialogListener {

    private lateinit var binding: ActivityReaderMainBinding

    private val mTimeInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent) {
            mReaderActivityViewModel.updateSystemStatus()
        }
    }

    private lateinit var mBookStatusViewModel: BookStatusViewModel
    private lateinit var mBookmarksViewModel: BookmarksViewModel
    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel

    private var backButtonPressedTime = Date().time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = ActivityReaderMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mBookStatusViewModel = ViewModelProvider(this)[BookStatusViewModel::class.java]
        mBookmarksViewModel = ViewModelProvider(this)[BookmarksViewModel::class.java]
        mReaderActivityViewModel = ViewModelProvider(this)[ReaderActivityViewModel::class.java]

        mBookStatusViewModel.cleanup(this)

        // nachputzen falsche prefs
        // linespace und LetterSpace  pref muss string sein - float ist falsch
        val mySPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = mySPrefs.edit()
        try {
            if (mySPrefs.getFloat(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, -1f) != -1f) {
                editor.remove(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING)
                editor.apply()
            }
        } catch (e: ClassCastException) {
            // its ok
        }
        try {
            if (mySPrefs.getFloat(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING, -1f) != -1f) {
                editor.remove(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING)
                editor.apply()
            }
        } catch (e: ClassCastException) {
            // its ok
        }

        TextViewInitiator.initiateTextView(binding.textViewPageview, "")

        mReaderActivityViewModel.loadSettings(this@ReaderActivity)
        mReaderActivityViewModel.loadPrefs(this@ReaderActivity)

        registerReceiver(mTimeInfoReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        setOnClickListener()

        binding.textViewPageview.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            Log.d(TAG, "pageView layout changed")
            updateSizeInfo()
        }

        mBookStatusViewModel.savedBookStatus.observe(this) {
            mReaderActivityViewModel.goToPositionByBookStatus(it)
        }

        mReaderActivityViewModel.book.observe(this) {
            if (it != null) {
                loadPositionForBook()
                mBookStatusViewModel.updateLastOpenTime(it)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.can_not_load_book, mReaderActivityViewModel.prefsHelper.bookPath),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        mReaderActivityViewModel.pageViewContent.observe(this) {
            binding.textViewPageview.text = it
        }

        mReaderActivityViewModel.pageViewSettings.observe(this) {
            setPageView(it)
        }
        mReaderActivityViewModel.pageViewColorSettings.observe(this) {
            setColorTheme(it)
        }

        mReaderActivityViewModel.infoTextLeft.observe(this) {
            binding.tvInfotextLeft.text = it
        }
        mReaderActivityViewModel.infoTextRight.observe(this) {
            binding.tvInfotextRight.text = it
        }
        mReaderActivityViewModel.infoTextSystemstatus.observe(this) {
            binding.tvInfotextSystemstatus.text = it
        }

        mReaderActivityViewModel.note.observe(this) {
            if (it != null) {
                val floatTextViewFragment: FloatTextViewFragment =
                    FloatTextViewFragment.newInstance(
                        it,
                        binding.textViewPageview.textSize,
                        mReaderActivityViewModel.prefsHelper.font
                    )
                floatTextViewFragment.show(supportFragmentManager, "fragment_floattextview")
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //Here you can get the size of pageView!
        Log.d(TAG, "onWindowFocusChanged")
        if (mReaderActivityViewModel.prefsHelper.bookPath != null) {
            runOnUiThread {
                try {
                    if (mReaderActivityViewModel.book.value == null
                        || mReaderActivityViewModel.book.value!!.fileLocation != mReaderActivityViewModel.prefsHelper.bookPath) {
                        loadBook()
                    }
                } catch (e: Exception) {
                    Log.e("tag", e.stackTraceToString())
                }
            }
        } else {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.open_book),
                Toast.LENGTH_LONG
            ).show()
            openMainMenu()
        }
    }

    override fun onBackPressed() {
        if (Date().time - backButtonPressedTime < 2000) {
            finish()
        } else {
            backButtonPressedTime = Date().time
            Toast.makeText(
                this,
                resources.getString(R.string.press_again_to_exit),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadBook() {
        if (!FileHelper.contentFileExist(applicationContext, mReaderActivityViewModel.prefsHelper.bookPath)) {
            Toast.makeText(
                this,
                resources.getString(R.string.can_not_load_book, mReaderActivityViewModel.prefsHelper.bookPath),
                Toast.LENGTH_LONG
            ).show()
            openMainMenu()
        }
        Toast.makeText(this, resources.getString(R.string.loading_book), Toast.LENGTH_SHORT).show()
        mReaderActivityViewModel.loadBook(binding.textViewPageview)

    }

    private fun openBookFromIntent(data: Intent) {
        Log.d(TAG, "openBookFromIntent")
        if (data.hasExtra(PrefsHelper.PREF_BOOK_PATH)) {
            mReaderActivityViewModel.prefsHelper.bookPath = data.getStringExtra(PrefsHelper.PREF_BOOK_PATH)
            mReaderActivityViewModel.savePrefs()
            startProgress()
            loadBook()
        }
    }

    private fun loadPositionForBook() {
        if (mReaderActivityViewModel.book.value == null) return
        if ( mReaderActivityViewModel.prefsHelper.bookPath != null)
            mBookStatusViewModel.loadBookStatus(mReaderActivityViewModel.prefsHelper.bookPath!!)
    }

    private fun savePositionForBook() {
        if (mReaderActivityViewModel.prefsHelper.bookPath != null
            && mReaderActivityViewModel.book.value?.curPage != null) {
            mBookStatusViewModel.savePosition(mReaderActivityViewModel.book.value!!)
        }
    }

    override fun onStop() {
        super.onStop()
        mReaderActivityViewModel.savePrefs()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTimeInfoReceiver)
    }

    private fun checkTap(point: Point, tap: Int) {
        val zone = mReaderActivityViewModel.getZone(point)
        // textViewInfoRight!!.text = resources.getString(R.string.click_in_zone, tap, zone)
        Log.d(TAG, "Tap $tap in Zone $zone")
        when (tap) {
            1 -> { // double tap
                doTapAction(mReaderActivityViewModel.prefsHelper.tapOneAction[zone])
            }
            2 -> { // double tap
                doTapAction(mReaderActivityViewModel.prefsHelper.tapDoubleAction[zone])
            }
            3 -> { // long tap
                doTapAction(mReaderActivityViewModel.prefsHelper.tapLongAction[zone])
            }
        }
    }

    private fun doTapAction(tapAction: String?) {
        Log.d(TAG, "doTapAction: $tapAction")
        if (tapAction == null) return
        when (tapAction) {
            "PagePrev" -> {
                doPagePrev()
            }
            "PageNext" -> {
                doPageNext()
            }
            "QuickMenu" -> {
                openQuickMenu()
            }
            "MainMenu" -> {
                openMainMenu()
            }
            "GoTo" -> {
                openGotoMenu()
            }
            "Bookmarks" -> {
                onShowBookmarklist()
            }
            "None" -> {
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {
        binding.llInfobereich.setOnTouchListener(object :
            OnSwipeTouchListener(this@ReaderActivity) {
            override fun onClick(point: Point) {
                super.onClick(point)
                openGotoMenu()
            }

            override fun onLongClick(point: Point) {
                super.onLongClick(point)
                openGotoMenu()
            }
        })

        binding.textViewPageview.setOnTouchListener(object :
            OnSwipeTouchListener(this@ReaderActivity) {
            override fun onClick(point: Point) {
                super.onClick(point)

                // Find the URL that was pressed
                val off = getClickedOffset(point)
                val spannable = binding.textViewPageview.text as Spannable
                val link = spannable.getSpans(off, off, URLSpan::class.java)
                if (link.isNotEmpty()) {
                    // link clicked
                    val url = link[0].url
                    mReaderActivityViewModel.loadNote(url)
                } else {
                    // not a link, normal click
                    checkTap(point, 1)
                }
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                doPageNext()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                doPagePrev()
            }

            override fun onSlideUp(point: Point) {
                super.onSlideUp(point)
                mReaderActivityViewModel.increaseScreenBrghtness(this@ReaderActivity, point)
            }

            override fun onSlideDown(point: Point) {
                super.onSlideDown(point)
                mReaderActivityViewModel.decreaseScreenBrghtness(this@ReaderActivity, point)
            }

            override fun onDoubleClick(point: Point) {
                //super.onDoubleClick(point)
                checkTap(point, 2)
            }

            override fun onLongClick(point: Point) {
                super.onLongClick(point)

                // Find the Image that was pressed
                val off = getClickedOffset(point)
                val spannable = binding.textViewPageview.text as Spannable
                val image = spannable.getSpans(off, off, ImageSpan::class.java)
                if (image.isNotEmpty()) {
                    showImage(image[0])
                } else {
                    checkTap(point, 3)
                }
            }

        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                doPageNext()
                return true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                doPagePrev()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun updateSizeInfo() {
        val fw = binding.textViewPageview.measuredWidth
        val fh = binding.textViewPageview.measuredHeight
        val w =
            binding.textViewPageview.measuredWidth - binding.textViewPageview.paddingLeft - binding.textViewPageview.paddingRight
        val h =
            binding.textViewPageview.measuredHeight - binding.textViewPageview.paddingTop - binding.textViewPageview.paddingBottom

        mReaderActivityViewModel.updateSizeInfo(fw, fh, w, h)
    }

    private fun doPageNext() {
        if (mReaderActivityViewModel.pageNext())
            savePositionForBook()
    }

    private fun doPagePrev() {
        if (mReaderActivityViewModel.pagePrev())
            savePositionForBook()
    }

    private fun getClickedOffset(point: Point): Int {
        // check if Link or image clicked
        var x = point.x
        var y = point.y
        x -= binding.textViewPageview.totalPaddingLeft
        y -= binding.textViewPageview.totalPaddingTop
        x += binding.textViewPageview.scrollX
        y += binding.textViewPageview.scrollY
        // Locate the clicked span
        val layout = binding.textViewPageview.layout
        val line = layout.getLineForVertical(y)
        return layout.getOffsetForHorizontal(line, x.toFloat())
    }

    private fun showImage(imageSpan: ImageSpan) {
        val b: ByteArray = if (imageSpan.source != null) {
            mReaderActivityViewModel.book.value!!.getImageByteArray(imageSpan.source!!) ?: return
        } else {
            val bitmap = ImageUtils.drawableToBitmap(imageSpan.drawable)
            val byteArrayStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayStream)
            byteArrayStream.toByteArray()
        }
        val imageViewerFragment: ImageViewerFragment =
            ImageViewerFragment.newInstance(b)
        imageViewerFragment.show(supportFragmentManager, "fragment_imageviewfragment")
    }

    private fun openMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java)
        resultLauncherMainMenu.launch(intent)
    }

    private var resultLauncherMainMenu = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intentData: Intent? = result.data

            if (intentData != null && intentData.hasExtra(PREF_TYPE)) {
                when (intentData.getIntExtra(PREF_TYPE, 0)) {
                    PREF_TYPE_OPEN_BOOK -> openBookFromIntent(intentData)
                    PREF_TYPE_SETTINGS -> {
                        mReaderActivityViewModel.loadSettings(this@ReaderActivity)
                    }
                }
            }
        }
    }

    private fun openQuickMenu() {
        val quickMenuFragment = QuickMenuFragment()
        quickMenuFragment.show(supportFragmentManager, "fragment_quick_menu")
    }

    override fun onFinishQuickMenuDialog(
        textSize: Float,
        lineSpacing: Float,
        letterSpacing: Float,
        colorTheme: String
    ) {
        mReaderActivityViewModel.finishQuickMenuSettings(
            textSize, lineSpacing, letterSpacing, colorTheme
        )
    }

    override fun onChangeColorTheme(colorTheme: String, colorThemeIndex: Int) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        var co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_BACK + colorTheme, 0)
        val colorBack =
            if (co != 0) "#" + Integer.toHexString(co)
            else resources.getString(
                PrefsHelper.colorBackgroundDefaultArray[colorThemeIndex]
            )

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_TEXT + colorTheme, 0)
        val colorText =
            if (co != 0) "#" + Integer.toHexString(co)
            else resources.getString(
                PrefsHelper.colorForegroundDefaultArray[colorThemeIndex]
            )

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_LINKTEXT + colorTheme, 0)
        val colorLinkText =
            if (co != 0) "#" + Integer.toHexString(co)
            else mReaderActivityViewModel.prefsHelper.colorLinkTextDefault

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_INFOTEXT + colorTheme, 0)
        val colorInfoText =
            if (co != 0) "#" + Integer.toHexString(co)
            else mReaderActivityViewModel.prefsHelper.colorTextDefault

        val showBackgroundImage =
            prefs.getBoolean(PrefsHelper.PREF_KEY_SHOW_BACKGROUND_IMAGE + colorTheme, false)
        val backgroundImageUri =
            prefs.getString(PrefsHelper.PREF_KEY_BACKGROUND_IMAGE_URI + colorTheme, null)
        val backgroundImageTiledRepeat =
            prefs.getBoolean(PrefsHelper.PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT + colorTheme, false)

        if (showBackgroundImage
            && backgroundImageUri != null
            && backgroundImageUri != "") {
            binding.textViewHolder.setBackgroundColor(Color.TRANSPARENT)
            try {
                val uri = Uri.parse(backgroundImageUri)
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bitmap = BitmapDrawable(resources, inputStream)
                if (backgroundImageTiledRepeat)
                    bitmap.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                binding.textViewHolder.background = bitmap
                // val backgroundDrawable = Drawable.createFromStream(inputStream, uri.toString())
                // textViewHolder!!.background = backgroundDrawable
            } catch (e: Exception) {
                binding.textViewHolder.background = null
                binding.textViewHolder.setBackgroundColor(Color.parseColor(colorBack))
            }
        } else {
            binding.textViewHolder.background = null
            binding.textViewHolder.setBackgroundColor(Color.parseColor(colorBack))
        }

        binding.textViewPageview.setTextColor(Color.parseColor(colorText))
        binding.textViewPageview.setLinkTextColor(Color.parseColor(colorLinkText))
        binding.tvInfotextLeft.setTextColor(Color.parseColor(colorInfoText))
        binding.tvInfotextRight
            .setTextColor(Color.parseColor(colorInfoText))

        var marginTop = mReaderActivityViewModel.prefsHelper.marginDefault
        var marginBottom = mReaderActivityViewModel.prefsHelper.marginDefault
        var marginLeft = mReaderActivityViewModel.prefsHelper.marginDefault
        var marginRight = mReaderActivityViewModel.prefsHelper.marginDefault

        var sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_TOP + colorTheme, null)
        try {
            marginTop =
                if (sMargin != null) Integer.parseInt(sMargin)
                else mReaderActivityViewModel.prefsHelper.marginDefault
        } catch (e: Exception) {

        }
        sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_BOTTOM + colorTheme, null)
        try {
            marginBottom =
                if (sMargin != null) Integer.parseInt(sMargin)
                else mReaderActivityViewModel.prefsHelper.marginDefault
        } catch (e: Exception) {

        }
        sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_LEFT + colorTheme, null)
        try {
            marginLeft =
                if (sMargin != null) Integer.parseInt(sMargin)
                else mReaderActivityViewModel.prefsHelper.marginDefault
        } catch (e: Exception) {

        }
        sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_RIGHT + colorTheme, null)
        try {
            marginRight =
                if (sMargin != null) Integer.parseInt(sMargin)
                else mReaderActivityViewModel.prefsHelper.marginDefault
        } catch (e: Exception) {

        }

        val density = this.resources.displayMetrics.density
        val marginTopPixel = (marginTop * density).toInt()
        val marginBottomPixel = (marginBottom * density).toInt()
        val marginLeftPixel = (marginLeft * density).toInt()
        val marginRightPixel = (marginRight * density).toInt()
        binding.textViewPageview.setPadding(
            marginLeftPixel,
            marginTopPixel,
            marginRightPixel,
            marginBottomPixel
        )

    }

    override fun onChangeTextSize(textSize: Float) {
        binding.textViewPageview.textSize = textSize
        mReaderActivityViewModel.reloadCurrentPage()
    }

    override fun onChangeLineSpacing(lineSpacing: Float) {
        if (binding.textViewPageview.lineSpacingMultiplier == lineSpacing) return
        binding.textViewPageview.setLineSpacing(
            binding.textViewPageview.lineSpacingExtra,
            lineSpacing
        )
        mReaderActivityViewModel.reloadCurrentPage()
    }

    override fun onChangeLetterSpacing(letterSpacing: Float) {
        if (binding.textViewPageview.letterSpacing == letterSpacing) return
        binding.textViewPageview.letterSpacing = letterSpacing
        mReaderActivityViewModel.reloadCurrentPage()
    }

    override fun onCancelQuickMenu() {
        mReaderActivityViewModel.resetQuickSettings()
    }

    override fun onAddBookmark() {
        mBookmarksViewModel.insert(
            mReaderActivityViewModel.getBookmarkForCurrentPosition(
                binding.textViewPageview.text.toString().substring(0, 100)))
    }

    override fun onShowBookmarklist() {
        if (mReaderActivityViewModel.prefsHelper.bookPath == null) return
        val bookmarkListFragment: BookmarkListFragment =
            BookmarkListFragment.newInstance(
                mReaderActivityViewModel.prefsHelper.bookPath!!)
        bookmarkListFragment.show(supportFragmentManager, "fragment_bookmark_list")
    }

    override fun onSelectBookmark(bookmark: Bookmark) {
        if ( mReaderActivityViewModel.isBookmarkOnCurrentPage(bookmark)) return
        mReaderActivityViewModel.goToBookmark(bookmark)
        savePositionForBook()
    }

    private fun openGotoMenu() {
        if (mReaderActivityViewModel.book.value == null
            || mReaderActivityViewModel.book.value?.curPage == null) return
        val gotoMenuFragment: GotoMenuFragment =
            GotoMenuFragment
                .newInstance(
                    mReaderActivityViewModel.book.value!!.curPage!!.endBookPosition.section,
                    mReaderActivityViewModel.book.value!!.getCurTextPage(),
                    mReaderActivityViewModel.book.value!!.getPageScheme()!!.countTextPages,
                    mReaderActivityViewModel.book.value!!.getPageScheme()!!.sections.toTypedArray()
                )
        gotoMenuFragment.show(supportFragmentManager, "fragment_goto_menu")
    }

    override fun onFinishGotoMenuDialogPage(page: Int) {
        mReaderActivityViewModel.goToPage(page)
        savePositionForBook()
    }

    override fun onFinishGotoMenuDialogSection(section: Int) {
        mReaderActivityViewModel.goToSection(section)
        savePositionForBook()
    }

    private fun startProgress() {
        binding.textViewPageview.text = resources.getString(R.string.loading_book)
    }

    private fun setPageView(pageViewSettings: PageViewSettings) {
        binding.textViewPageview.textSize = pageViewSettings.textSize
        binding.textViewPageview.letterSpacing = pageViewSettings.letterSpacing
        binding.textViewPageview.typeface = pageViewSettings.typeFace
        binding.textViewPageview.setLineSpacing(
            binding.textViewPageview.lineSpacingExtra,
            pageViewSettings.lineSpacing
        )
        val density = this.resources.displayMetrics.density
        val marginTopPixel = (pageViewSettings.marginTop * density).toInt()
        val marginBottomPixel = (pageViewSettings.marginBottom * density).toInt()
        val marginLeftPixel = (pageViewSettings.marginLeft * density).toInt()
        val marginRightPixel = (pageViewSettings.marginRight * density).toInt()
        binding.textViewPageview.setPadding(
            marginLeftPixel,
            marginTopPixel,
            marginRightPixel,
            marginBottomPixel
        )
        mReaderActivityViewModel.recalcCurrentPage()
    }

    private fun setColorTheme(colorSettings: PageViewColorSettings) {
        if (colorSettings.showBackgroundImage
            && colorSettings.backgroundImageUri != null
            && !colorSettings.backgroundImageUri.equals(
                ""
            )
        ) {
            binding.textViewHolder.setBackgroundColor(Color.TRANSPARENT)
            try {
                val uri = Uri.parse(colorSettings.backgroundImageUri)
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bitmap = BitmapDrawable(resources, inputStream)
                if (colorSettings.backgroundImageTiledRepeat)
                    bitmap.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                binding.textViewHolder.background = bitmap
                // val backgroundDrawable = Drawable.createFromStream(inputStream, uri.toString())
                // binding.textViewHolder!!.background = backgroundDrawable
            } catch (e: Exception) {
                binding.textViewHolder.background = null
                binding.textViewHolder.setBackgroundColor(Color.parseColor(colorSettings.colorBack))
            }
        } else {
            binding.textViewHolder.background = null
            binding.textViewHolder.setBackgroundColor(Color.parseColor(colorSettings.colorBack))
        }

        binding.textViewPageview.setTextColor(Color.parseColor(colorSettings.colorText))
        binding.textViewPageview.setLinkTextColor(Color.parseColor(colorSettings.colorLink))
        binding.tvInfotextLeft.setTextColor(Color.parseColor(colorSettings.colorInfoText))
        binding.tvInfotextRight.setTextColor(Color.parseColor(colorSettings.colorInfoText))
        binding.tvInfotextSystemstatus.setTextColor(Color.parseColor(colorSettings.colorInfoText))

        try {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

            var pIsDark = false
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    //window.statusBarColor = Color.parseColor(prefsHelper!!.colorDarkBack)
                    pIsDark = true
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    //window.statusBarColor = Color.parseColor(prefsHelper!!.colorLightBack)
                }
                Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    //window.statusBarColor = Color.parseColor(prefsHelper!!.colorDarkBack)
                    pIsDark = true
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val lFlags = window.decorView.systemUiVisibility
                window.decorView.systemUiVisibility =
                    if (pIsDark) (lFlags and (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR).inv()) else
                        (lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "ReaderActivity"
        internal const val PREFS_FILE = "MainActivitySettings"

        const val REQUEST_ACCESS_QUICK_MENU = 2
        const val PREF_TYPE = "ReturnType"
        const val PREF_TYPE_OPEN_BOOK = 121
        const val PREF_TYPE_SETTINGS = 122
    }

}