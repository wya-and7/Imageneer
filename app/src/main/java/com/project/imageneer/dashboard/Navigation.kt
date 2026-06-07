package com.project.imageneer.dashboard

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.imageneer.Admin.AddImageScreen
import com.project.imageneer.Admin.AdminDashboardScreen
import com.project.imageneer.Admin.ProfileScreen
import com.project.imageneer.authentication.AdminLoginScreen
import com.project.imageneer.authentication.LoginScreen
import com.project.imageneer.game.MultiplayerLobbyScreen
import com.project.imageneer.authentication.ForgotPasswordScreen
import com.project.imageneer.authentication.RegisterScreen
import com.project.imageneer.game.MultiplayerGameScreen
import com.project.imageneer.game.MultiplayerHistoryScreen
import com.project.imageneer.game.MultiplayerWaitingScreen
import com.project.imageneer.game.SoloGameScreen

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("home") { DashboardScreen(navController) }
        composable("solo_game") { SoloGameScreen(navController) }
        composable("multiplayer_lobby") { MultiplayerLobbyScreen(navController) }
        composable("multiplayer_waiting/{roomId}",listOf(navArgument("roomId") { type = NavType.StringType })) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            MultiplayerWaitingScreen(navController, roomId)
        }
        composable(
            route = "multiplayer_game/{roomId}",
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            MultiplayerGameScreen(navController = navController, roomId = roomId)
        }
        composable("multiplayer_history") { MultiplayerHistoryScreen(navController = navController) }

        // Admin
        composable ("admin_home"){ AdminDashboardScreen(navController) }
        composable("admin_login") { AdminLoginScreen(navController) }
        composable("add_image") { AddImageScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("Add_Image") { AddImageScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}