package com.project.imageneer.dashboard

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.imageneer.MultiplayerLobby

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {

        // Dashboard
        composable("dashboard") {
            DashboardScreen(navController)
        }

        // Solo Screen
//        composable("solo") {
//            SoloScreen()
//        }

        // Multiplayer Screen
        composable("multiplayer") {
            MultiplayerLobby()
        }
    }
}