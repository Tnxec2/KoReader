package com.kontranik.koreader.compose.navigation


import android.net.Uri
import androidx.compose.material3.DrawerState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.kontranik.koreader.compose.NavRoutes
import com.kontranik.koreader.compose.ui.bookmarks.BoomkmarksScreen
import com.kontranik.koreader.compose.ui.bookmarks.BoomkmarksScreenDestination
import com.kontranik.koreader.compose.ui.lastopened.LastOpenedScreen
import com.kontranik.koreader.compose.ui.library.LibraryViewModel
import com.kontranik.koreader.compose.ui.library.byauthor.LibraryByAuthorDestination
import com.kontranik.koreader.compose.ui.library.byauthor.LibraryByAuthorScreen
import com.kontranik.koreader.compose.ui.library.bytitle.LibraryByTitleDestination
import com.kontranik.koreader.compose.ui.library.bytitle.LibraryByTitleScreen
import com.kontranik.koreader.compose.ui.library.main.LibraryMainMenuScreen
import com.kontranik.koreader.compose.ui.library.settings.LibrarySettingsScreen
import com.kontranik.koreader.compose.ui.mainmenu.MainMenuScreen
import com.kontranik.koreader.compose.ui.opds.OpdsListScreen
import com.kontranik.koreader.compose.ui.opds.OpdsViewModell
import com.kontranik.koreader.compose.ui.openfile.OpenFileScreen
import com.kontranik.koreader.compose.ui.openfile.OpenFileViewModel
import com.kontranik.koreader.compose.ui.reader.BookReaderScreen
import com.kontranik.koreader.compose.ui.reader.BookReaderViewModel
import com.kontranik.koreader.compose.ui.settings.ColorSettingsScreen
import com.kontranik.koreader.compose.ui.settings.ColorThemeSettingsDestination
import com.kontranik.koreader.compose.ui.settings.ColorThemeSettingsScreen
import com.kontranik.koreader.compose.ui.settings.InterfaceSettingsScreen
import com.kontranik.koreader.compose.ui.settings.RootSettingsScreen
import com.kontranik.koreader.compose.ui.settings.SettingsViewModel
import com.kontranik.koreader.compose.ui.settings.TapZonesDoubleClickSettingsScreen
import com.kontranik.koreader.compose.ui.settings.TapZonesInfoAreaClickSettingsScreen
import com.kontranik.koreader.compose.ui.settings.TapZonesOneClickSettingsScreen
import com.kontranik.koreader.compose.ui.settings.TapZonesSettingsScreen
import com.kontranik.koreader.compose.ui.settings.TextSettingsScreen
import com.kontranik.koreader.database.BookStatusViewModel


fun NavGraphBuilder.mainGraph(
    drawerState: DrawerState,
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    bookReaderViewModel: BookReaderViewModel,
    bookStatusViewModel: BookStatusViewModel,
    opdsViewModell: OpdsViewModell,
    libraryViewModel: LibraryViewModel
) {

    navigation(
        startDestination = NavOptions.BookReader.name,
        route = NavRoutes.MainRoute.name) {

        composable(NavOptions.BookReader.name){
            BookReaderScreen(
                drawerState = drawerState,
                navigateToMainMenu = { navController.navigate(NavOptions.MainMenu.name)},
                navigateToOpenFile = { navController.navigate(NavOptions.OpenFile.name) },
                navigateToLastOpened = { navController.navigate(NavOptions.LastOpened.name) },
                navigateToBookmarks = { path ->
                    val encoded = Uri.encode(path.replace('%','|'))
                    navController.navigate("${BoomkmarksScreenDestination.route}?path=${encoded}")
                },
                navigateToLibrary = {  title ->
                    navController.navigate("${LibraryByTitleDestination.route}?authorid=${null}&title=${title}")
                },
                navigateToOpdsNetworkLibrary = { navController.navigate(NavOptions.OPDS.name)},
                navigateToSettings = { navController.navigate(NavOptions.Settings.name)},
                navigateToAuthor = { authorId: Long ->
                    navController.navigate("${LibraryByTitleDestination.route}?authorid=${authorId}&title=${null}")
                },
                settingsViewModel = settingsViewModel,
                bookReaderViewModel = bookReaderViewModel,
                bookStatusViewModel = bookStatusViewModel,
            )
        }

        composable(NavOptions.MainMenu.name){
            MainMenuScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                navigateToOpenFile = { navController.navigate(NavOptions.OpenFile.name) },
                navigateToLastOpened = { navController.navigate(NavOptions.LastOpened.name) },
                navigateToBookmarks = { path ->
                    val encoded = Uri.encode(path.replace('%','|'))
                    navController.navigate("${BoomkmarksScreenDestination.route}?path=${encoded}")
                },
                navigateToLibrary = { navController.navigate(NavOptions.Library.name) },
                navigateToOpdsNetworkLibrary = { navController.navigate(NavOptions.OPDS.name)},
                navigateToSettings = { navController.navigate(NavOptions.Settings.name)},
                navigateToAuthor = { authorId: Long ->
                    navController.navigate("${LibraryByTitleDestination.route}?authorid=${authorId}&title=${null}")
                },
                bookReaderViewModel = bookReaderViewModel,
                bookStatusViewModel = bookStatusViewModel,
            )
        }

        composable(NavOptions.OpenFile.name) {
            OpenFileScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                libraryViewModel = libraryViewModel,
                bookReaderViewModel = bookReaderViewModel,
                bookStatusViewModel = bookStatusViewModel,
                navigateToReader = {
                    navController.navigate(NavOptions.BookReader.name){
                        popUpTo(NavOptions.BookReader.name) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                navigateToAuthor = { authorId: Long ->
                    navController.navigate("${LibraryByTitleDestination.route}?authorid=${authorId}&title=${null}")
                },
            )
        }


        composable(NavOptions.LastOpened.name) {
            LastOpenedScreen(drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                navigateToReader = {
                    navController.navigate(NavOptions.BookReader.name){
                        popUpTo(NavOptions.BookReader.name) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                navigateToAuthor = { authorId: Long ->
                    navController.navigate("${LibraryByTitleDestination.route}?authorid=${authorId}&title=${null}")
                },
                bookReaderViewModel = bookReaderViewModel,
            )
        }

        composable(NavOptions.Library.name) {
            LibraryMainMenuScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                navigateToBooksByTitle = {
                    navController.navigate("${LibraryByTitleDestination.route}?authorid=${null}&title=${null}")
                },
                navigateToBooksByAuthor = {
                    navController.navigate(LibraryByAuthorDestination.route)
                },
                navigateToSettings = { navController.navigate(NavOptions.LibrarySettings.name) })
        }

        composable(NavOptions.LibrarySettings.name) {
            LibrarySettingsScreen(
                drawerState = drawerState, navigateBack = { navController.popBackStack()}
            )
        }

        composable(
            route = LibraryByTitleDestination.routeWithArgs,
            arguments = listOf(
                navArgument(LibraryByTitleDestination.KEY_AUTHOR_ID) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(LibraryByTitleDestination.KEY_TITLE) {
                    type = NavType.StringType
                    nullable = true
                }
            ),
        ) {
            LibraryByTitleScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                bookReaderViewModel = bookReaderViewModel,
                bookStatusViewModel = bookStatusViewModel,
                navigateToReader = {
                    navController.navigate(NavOptions.BookReader.name){
                        popUpTo(NavOptions.BookReader.name) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                navigateToAuthor = { authorId: Long ->
                    navController.navigate("${LibraryByTitleDestination.route}?authorid=${authorId}&title=${null}")
                },
            )
        }

        composable(LibraryByAuthorDestination.route) {
            LibraryByAuthorScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                navigateToAuthor = { author ->
                    navController.navigate("${LibraryByTitleDestination.route}?authorid=${author.id}&title=${null}")
                }
            )
        }

        composable(NavOptions.OPDS.name) {
            OpdsListScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                opdsViewModell = opdsViewModell
            )
        }

        composable(BoomkmarksScreenDestination.routeWithArgs,
            arguments = listOf(
                navArgument(BoomkmarksScreenDestination.PATH_ARG) {
                    type = NavType.StringType
                }
            ),
            ) {
            BoomkmarksScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                navigateToReader = {
                    navController.navigate(NavOptions.BookReader.name){
                        popUpTo(NavOptions.BookReader.name) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                bookReaderViewModel = bookReaderViewModel
                )
        }

        composable(NavOptions.Settings.name) {
            RootSettingsScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                navigateToInterfaceSettings = { navController.navigate(NavOptions.InterfaceSettings.name) },
                navigateToColorThemeSettings = { navController.navigate(NavOptions.ColorSettings.name) },
                navigateToTextSettings = { navController.navigate(NavOptions.TextSettings.name) },
                navigateToTapZonesSettings = { navController.navigate(NavOptions.TapZonesSettings.name) },
            )
        }

        composable(NavOptions.InterfaceSettings.name) {
            InterfaceSettingsScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                settingsViewModel = settingsViewModel
            )
        }

        composable(NavOptions.ColorSettings.name) {
            ColorSettingsScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                navigateToTheme = { index -> navController.navigate("${ColorThemeSettingsDestination.route}/${index}") },
                settingsViewModel = settingsViewModel
            )
        }

        composable(
            route = ColorThemeSettingsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ColorThemeSettingsDestination.THEME_INDEX) { type = NavType.StringType},
            )
        ){
            ColorThemeSettingsScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                settingsViewModel = settingsViewModel
            )
        }

        composable(NavOptions.TextSettings.name) {
            TextSettingsScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                settingsViewModel = settingsViewModel
            )
        }


        composable(NavOptions.TapZonesSettings.name) {
            TapZonesSettingsScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                navigateToSettingsTapZonesOneClick = { navController.navigate(NavOptions.SettingsTapZonesOneClick.name) },
                navigateToSettingsTapZonesDoubleClick = { navController.navigate(NavOptions.SettingsTapZonesDoubleClick.name) },
                navigateToSettingsTapZonesInfoAreaClick = { navController.navigate(NavOptions.SettingsTapZonesInfoAreaClick.name) },
            )
        }

        composable(NavOptions.SettingsTapZonesOneClick.name) {
            TapZonesOneClickSettingsScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                settingsViewModel = settingsViewModel
            )
        }

        composable(NavOptions.SettingsTapZonesDoubleClick.name) {
            TapZonesDoubleClickSettingsScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                settingsViewModel = settingsViewModel
            )
        }
        composable(NavOptions.SettingsTapZonesInfoAreaClick.name) {
            TapZonesInfoAreaClickSettingsScreen(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() },
                settingsViewModel = settingsViewModel
            )
        }

    }
}

enum class NavOptions {
    BookReader,
    MainMenu,
    OpenFile,
    LastOpened,
    Library,
    LibrarySettings,
    OPDS,
    Settings,
    InterfaceSettings,
    ColorSettings,
    TextSettings,
    TapZonesSettings,
    SettingsTapZonesOneClick,
    SettingsTapZonesDoubleClick,
    SettingsTapZonesInfoAreaClick
}

