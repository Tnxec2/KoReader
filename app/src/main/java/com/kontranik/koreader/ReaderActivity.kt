package com.kontranik.koreader

import android.content.*
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.style.ImageSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.database.BookStatusViewModelFactory
import com.kontranik.koreader.database.BookmarksViewModel
import com.kontranik.koreader.database.BookmarksViewModelFactory
import com.kontranik.koreader.database.model.Bookmark
import com.kontranik.koreader.databinding.ActivityReaderMainBinding
import com.kontranik.koreader.model.*
import com.kontranik.koreader.ui.components.BookReaderTextview.BookReaderTextviewListener
import com.kontranik.koreader.ui.components.ReadInfoArea.ReadInfoAreaListener
import com.kontranik.koreader.ui.fragments.*
import com.kontranik.koreader.utils.*
import java.io.InputStream
import java.util.*


class ReaderActivity :
    AppCompatActivity(),
    QuickMenuFragment.QuickMenuDialogListener,
    BookmarkListFragment.BookmarkListDialogListener,
    GotoMenuFragment.GotoMenuDialogListener,
    ReadInfoAreaListener,
    BookReaderTextviewListener {

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

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = ActivityReaderMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mBookStatusViewModel = ViewModelProvider(this, BookStatusViewModelFactory((application as KoReaderApplication).bookStatusRepository))[BookStatusViewModel::class.java]
        mBookmarksViewModel = ViewModelProvider(this, BookmarksViewModelFactory((application as KoReaderApplication).bookmarksRepository))[BookmarksViewModel::class.java]
        mReaderActivityViewModel = ViewModelProvider(this, ReaderActivityViewModelFactory((application as KoReaderApplication).bookStatusRepository))[ReaderActivityViewModel::class.java]

        mBookStatusViewModel.cleanup(this)


        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                exitOnBackPressed()
            }
        } else {
            onBackPressedDispatcher.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        exitOnBackPressed()
                    }
                }
            )
        }

        TextViewInitiator.initiateTextView(binding.textViewPageview, "")

        mReaderActivityViewModel.loadSettings(this@ReaderActivity)
        mReaderActivityViewModel.loadPrefs(this@ReaderActivity)

        registerReceiver(mTimeInfoReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        binding.readInfoArrea.setListener(this)
        binding.textViewPageview.setListener(this)

        binding.textViewPageview.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            Log.d(TAG, "pageView layout changed")
            updateSizeInfo()
        }

        mBookStatusViewModel.savedBookStatus.observe(this) {
            mReaderActivityViewModel.goToPositionByBookStatus(binding.textViewPageview, it)
        }

        mReaderActivityViewModel.book.observe(this) {
            if (it != null) {
                loadPositionForBook()
                mBookStatusViewModel.updateLastOpenTime(it)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.can_not_load_book, PrefsHelper.bookPath),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        mReaderActivityViewModel.pageViewContent.observe(this) {
            binding.textViewPageview.text = it
        }

        mReaderActivityViewModel.pageViewSettings.observe(this) {
            binding.textViewPageview.changeSettings(it)
            mReaderActivityViewModel.recalcCurrentPage(binding.textViewPageview)
        }

        mReaderActivityViewModel.pageViewColorSettings.observe(this) {
            setColorTheme(it)
        }

        mReaderActivityViewModel.infoTextLeft.observe(this) {
            binding.readInfoArrea.setTextLeft(it)
        }
        mReaderActivityViewModel.infoTextRight.observe(this) {
            binding.readInfoArrea.setTextRight(it)
        }
        mReaderActivityViewModel.infoTextSystemstatus.observe(this) {
            binding.readInfoArrea.setTextMiddle(it)
        }

        mReaderActivityViewModel.note.observe(this) {
            if (it != null) {
                val floatTextViewFragment: FloatTextViewFragment =
                    FloatTextViewFragment.newInstance(
                        it,
                        PrefsHelper.textSize,
                        PrefsHelper.font
                    )
                floatTextViewFragment.show(supportFragmentManager, "fragment_floattextview")
            }
        }

//        supportFragmentManager.addOnBackStackChangedListener {
//            if (supportFragmentManager.backStackEntryCount == 0) {
//                binding.fragmentContainerView.visibility = View.GONE
//            }
//        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d(TAG, "onWindowFocusChanged")

        //Here you can get the size of pageView!
        Log.d(TAG, "onWindowFocusChanged hasFocus")
        if (PrefsHelper.bookPath != null) {
            mReaderActivityViewModel.loadBook(binding.textViewPageview.context)
        } else {
            if (supportFragmentManager.backStackEntryCount == 0) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.open_book),
                    Toast.LENGTH_LONG
                ).show()
                // TODO: test
                // openFile()
            }
        }
    }

    private fun exitOnBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
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
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun loadPositionForBook() {
        if (mReaderActivityViewModel.book.value == null) return
        if ( PrefsHelper.bookPath != null)
            mBookStatusViewModel.loadBookStatus(PrefsHelper.bookPath!!)
    }

    override fun onStop() {
        super.onStop()
        mReaderActivityViewModel.savePrefs()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTimeInfoReceiver)
    }


    override fun onTabActionOnBookReaderTextview(tapAction: String?) {
        Log.d(TAG, "doTapAction: $tapAction")
        if (tapAction == null) return
        when (tapAction) {
            "PagePrev" -> {
                mReaderActivityViewModel.doPagePrev(binding.textViewPageview)
            }
            "PageNext" -> {
                mReaderActivityViewModel.goToNextPage(binding.textViewPageview)
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
                onShowBookmarklistQuickMenuDialog()
            }
            else -> {
            }
        }
    }

    override fun onClickLinkOnBookReaderTextview(url: String) {
        mReaderActivityViewModel.loadNote(url)
    }

    override fun onSwipeLeftOnBookReaderTextview() {
        mReaderActivityViewModel.goToNextPage(binding.textViewPageview)
    }

    override fun onSwipeRightOnBookReaderTextview() {
        mReaderActivityViewModel.doPagePrev(binding.textViewPageview)
    }

    override fun onSlideUpOnBookReaderTextView(point: Point) {
        mReaderActivityViewModel.increaseScreenBrghtness(this@ReaderActivity, point)
    }

    override fun onSlideDownOnBookReaderTextView(point: Point) {
        mReaderActivityViewModel.decreaseScreenBrghtness(this@ReaderActivity, point)
    }

    override fun onClickImageOnBookReaderTextview(imageSpan: ImageSpan) {
        showImage(imageSpan)
    }

    private fun showImage(imageSpan: ImageSpan) {
        val b: ByteArray = mReaderActivityViewModel.getImageByteArray(imageSpan) ?: return
        val imageViewerFragment: ImageViewerFragment =
            ImageViewerFragment.newInstance(b, PrefsHelper.isDarkMode())
        imageViewerFragment.show(supportFragmentManager, "fragment_imageviewfragment")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_DOWN -> {
                mReaderActivityViewModel.goToNextPage(binding.textViewPageview)
                return true
            }
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_UP -> {
                mReaderActivityViewModel.doPagePrev(binding.textViewPageview)
                return true
            }
            KeyEvent.KEYCODE_M -> {
                openMainMenu()
                return true
            }
            KeyEvent.KEYCODE_Q -> {
                openQuickMenu()
                return true
            }
            KeyEvent.KEYCODE_G -> {
                openGotoMenu()
                return true
            }
            KeyEvent.KEYCODE_B -> {
                onShowBookmarklistQuickMenuDialog()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun updateSizeInfo() {
        mReaderActivityViewModel.updateSizeInfo( binding.textViewPageview)
    }

    private fun openMainMenu() {
        val mainMenuFragment = MainMenuFragment()

        binding.fragmentContainerView.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, mainMenuFragment, "fragment_main_menu")
            .addToBackStack("fragment_main_menu")
            .commit()
    }

    private fun openFile() {
        val fragment = FileChooseFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, "fragment_open_file")
            .addToBackStack("fragment_open_file")
            .commit()
    }

    private fun openQuickMenu() {
        val quickMenuFragment = QuickMenuFragment()
        quickMenuFragment.show(supportFragmentManager, "fragment_quick_menu")
    }

    override fun onOpenBookInfoQuickMenuDialog(bookUri: String?) {
        if ( bookUri != null) {
            val bookInfoFragment: BookInfoFragment = BookInfoFragment.newInstance(bookUri)
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, bookInfoFragment, "fragment_bookinfo")
                .addToBackStack("fragment_bookinfo")
                .commit()
        }
    }

    override fun onFinishQuickMenuDialog(
        textSize: Float,
        lineSpacingMultiplier: Float,
        letterSpacing: Float,
        colorThemeIndex: Int
    ) {
        mReaderActivityViewModel.finishQuickMenuSettings(
            textSize, lineSpacingMultiplier, letterSpacing, colorThemeIndex
        )
    }

    override fun onChangeColorThemeQuickMenuDialog(colorTheme: String, colorThemeIndex: Int) {
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
            else PrefsHelper.colorLinkTextDefault

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_INFOTEXT + colorTheme, 0)
        val colorInfoText =
            if (co != 0) "#" + Integer.toHexString(co)
            else PrefsHelper.colorTextDefault

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
        binding.readInfoArrea.changeStyle(Color.parseColor(colorInfoText))

        var marginTop = PrefsHelper.marginDefault
        var marginBottom = PrefsHelper.marginDefault
        var marginLeft = PrefsHelper.marginDefault
        var marginRight = PrefsHelper.marginDefault

        var sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_TOP + colorTheme, null)
        try {
            marginTop =
                if (sMargin != null) Integer.parseInt(sMargin)
                else PrefsHelper.marginDefault
        } catch (_: Exception) {

        }
        sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_BOTTOM + colorTheme, null)
        try {
            marginBottom =
                if (sMargin != null) Integer.parseInt(sMargin)
                else PrefsHelper.marginDefault
        } catch (_: Exception) {

        }
        sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_LEFT + colorTheme, null)
        try {
            marginLeft =
                if (sMargin != null) Integer.parseInt(sMargin)
                else PrefsHelper.marginDefault
        } catch (_: Exception) {

        }
        sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_RIGHT + colorTheme, null)
        try {
            marginRight =
                if (sMargin != null) Integer.parseInt(sMargin)
                else PrefsHelper.marginDefault
        } catch (_: Exception) {

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

    override fun onChangeTextSizeQuickMenuDialog(textSize: Float) {
        binding.textViewPageview.textSize = textSize
        mReaderActivityViewModel.reloadCurrentPage(binding.textViewPageview)
    }

    override fun onChangeLineSpacingQuickMenuDialog(lineSpacingMultiplier: Float) {
        if (binding.textViewPageview.lineSpacingMultiplier == lineSpacingMultiplier) return
        binding.textViewPageview.setLineSpacing(
            binding.textViewPageview.lineSpacingExtra,
            lineSpacingMultiplier
        )
        mReaderActivityViewModel.reloadCurrentPage(binding.textViewPageview)
    }

    override fun onChangeLetterSpacingQuickMenuDialog(letterSpacing: Float) {
        if (binding.textViewPageview.letterSpacing == letterSpacing) return
        binding.textViewPageview.letterSpacing = letterSpacing
        mReaderActivityViewModel.reloadCurrentPage(binding.textViewPageview)
    }

    override fun onCancelQuickMenuDialog() {
        mReaderActivityViewModel.resetQuickSettings()
    }

    override fun onAddBookmarkQuickMenuDialog() {
        addBookmark()
    }

    private fun addBookmark() {
        mBookmarksViewModel.insert(
            mReaderActivityViewModel.getBookmarkForCurrentPosition(
                binding.textViewPageview.text.toString().substring(0, 100)))
    }

    override fun onShowBookmarklistQuickMenuDialog() {
        if (PrefsHelper.bookPath == null) return
        val bookmarkListFragment: BookmarkListFragment =
            BookmarkListFragment.newInstance(
                PrefsHelper.bookPath!!)
        bookmarkListFragment.show(supportFragmentManager, "fragment_bookmark_list")
    }

    override fun onSelectBookmarkBookmarkListFragment(bookmark: Bookmark) {
        mReaderActivityViewModel.goToBookmark(binding.textViewPageview, bookmark)
    }

    override fun onAddBookmarkBookmarkListFragment() {
        addBookmark()
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
        mReaderActivityViewModel.goToPage(binding.textViewPageview, page)
    }

    override fun onFinishGotoMenuDialogSection(section: Int) {
        mReaderActivityViewModel.goToSection(binding.textViewPageview, section)
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
        binding.readInfoArrea.changeStyle(Color.parseColor(colorSettings.colorInfoText))

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

            val lFlags = window.decorView.systemUiVisibility

            window.decorView.systemUiVisibility =
                if (pIsDark) (lFlags and (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR).inv()) else
                    (lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClickReadInfoArea() {
        openGotoMenu()
    }

    override fun onLongClickReadInfoArea() {
        openGotoMenu()
    }

    companion object {
        private const val TAG = "ReaderActivity"
        internal const val PREFS_FILE = "MainActivitySettings"
    }

}