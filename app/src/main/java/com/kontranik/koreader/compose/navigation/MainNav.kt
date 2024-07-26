package com.kontranik.koreader.compose.navigation


import androidx.compose.material3.DrawerState
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument


fun NavGraphBuilder.mainGraph(
    drawerState: DrawerState,
    navController: NavHostController,
) {

    navigation(
        startDestination = "BookReaderDestination",
        route = NavRoutes.MainRoute.name) {





    }
}

enum class NavRoutes {
    MainRoute,
}

