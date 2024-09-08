package com.kontranik.koreader.compose

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.navigation.mainGraph
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.ui.library.LibraryViewModel
import com.kontranik.koreader.compose.ui.opds.OpdsViewModell
import com.kontranik.koreader.compose.ui.openfile.OpenFileViewModel
import com.kontranik.koreader.compose.ui.reader.BookReaderScreen
import com.kontranik.koreader.compose.ui.reader.BookReaderViewModel
import com.kontranik.koreader.compose.ui.settings.SettingsViewModel
import com.kontranik.koreader.database.BookStatusViewModel
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun MainCompose(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val backButtonPressedTime = remember {
        mutableLongStateOf(Date().time-3000)
    }

    val closeDrawer: () -> Unit = {
        if (drawerState.isOpen)
            scope.launch { drawerState.close() }
        else
            scope.launch {
                //println(Date().time - backButtonPressedTime.longValue)
                if (Date().time - backButtonPressedTime.longValue < 2000) {
                    val activity = (context as? Activity)
                    activity?.finish()
                } else {
                    backButtonPressedTime.longValue = Date().time
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.press_again_to_exit),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val bookReaderViewModel: BookReaderViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val bookStatusViewModel: BookStatusViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val opdsViewModell: OpdsViewModell = viewModel(factory = AppViewModelProvider.Factory)
    val openFileViewModel: OpenFileViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val libraryViewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory)

    LaunchedEffect(key1 = Unit) {
        bookStatusViewModel.cleanup(context)
    }

    AppTheme(
        darkTheme = settingsViewModel.isDarkMode(context)
    ) {
        Surface {
            BackHandler(enabled = true, onBack = closeDrawer)
            NavHost(
                navController,
                startDestination = NavRoutes.MainRoute.name
            ) {
                mainGraph(drawerState, navController, settingsViewModel, bookReaderViewModel,
                    openFileViewModel, bookStatusViewModel, opdsViewModell, libraryViewModel)
            }
        }
    }
}

enum class NavRoutes {
    MainRoute,
}