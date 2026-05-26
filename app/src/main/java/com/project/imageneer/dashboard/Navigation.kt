package com.project.imageneer.dashboard

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.imageneer.authentication.AdminLoginScreen
import com.project.imageneer.authentication.LoginScreen
import com.project.imageneer.game.MultiplayerLobbyScreen
import com.project.imageneer.authentication.RegisterScreen
import com.project.imageneer.game.SoloGameScreen

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("admin_login") { AdminLoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { DashboardScreen(navController) }
//        composable ("admin_dashboard"){ AdminDashboardScreen(navController) }
        composable("solo_game") { SoloGameScreen(navController) }
        composable("multiplayer_lobby") { MultiplayerLobbyScreen(navController) }
    }
}