package com.ontime.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ontime.ui.screens.DashboardScreen
import com.ontime.ui.screens.SessionEditScreen
import com.ontime.viewmodel.SessionViewModel

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object EditSession : Screen("edit_session")
    data object NewSession : Screen("new_session")
}

@Composable
fun OnTimeNavHost(
    navController: NavHostController,
    viewModel: SessionViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onAddSessionClick = {
                    viewModel.startAddingNewSession()
                    navController.navigate(Screen.NewSession.route)
                },
                onSessionClick = { sessionId ->
                    navController.navigate(Screen.EditSession.route)
                }
            )
        }

        composable(Screen.EditSession.route) {
            SessionEditScreen(
                viewModel = viewModel,
                onBackClick = {
                    viewModel.cancelEditing()
                    navController.popBackStack()
                },
                isNewSession = false
            )
        }

        composable(Screen.NewSession.route) {
            SessionEditScreen(
                viewModel = viewModel,
                onBackClick = {
                    viewModel.cancelEditing()
                    navController.popBackStack()
                },
                isNewSession = true
            )
        }
    }
}
