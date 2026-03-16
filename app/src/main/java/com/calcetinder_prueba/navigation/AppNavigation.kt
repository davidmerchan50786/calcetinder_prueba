package com.calcetinder_prueba.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.calcetinder_prueba.ui.screens.auth.AuthScreen
import com.calcetinder_prueba.ui.screens.matches.MatchesScreen
import com.calcetinder_prueba.ui.screens.profile.ProfileScreen
import com.calcetinder_prueba.ui.screens.swipe.SwipeScreen
import com.calcetinder_prueba.ui.screens.upload.UploadScreen
import com.calcetinder_prueba.ui.theme.CalcetinderPink

sealed class Screen(val route: String) {
    object Auth    : Screen("auth")
    object Swipe   : Screen("swipe")
    object Upload  : Screen("upload")
    object Matches : Screen("matches")
    object Profile : Screen("profile")
}

// Pantallas que muestran la barra de navegación inferior
private val bottomNavScreens = listOf(
    Screen.Swipe.route,
    Screen.Matches.route,
    Screen.Profile.route
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavScreens) {
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Swipe.route,
                        onClick = {
                            navController.navigate(Screen.Swipe.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Swipe",
                                tint = if (currentRoute == Screen.Swipe.route) CalcetinderPink else Color.Gray
                            )
                        },
                        label = { Text("Calcetines") },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = CalcetinderPink.copy(alpha = 0.15f))
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Matches.route,
                        onClick = {
                            navController.navigate(Screen.Matches.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Matches",
                                tint = if (currentRoute == Screen.Matches.route) CalcetinderPink else Color.Gray
                            )
                        },
                        label = { Text("Matches") },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = CalcetinderPink.copy(alpha = 0.15f))
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Profile.route,
                        onClick = {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = if (currentRoute == Screen.Profile.route) CalcetinderPink else Color.Gray
                            )
                        },
                        label = { Text("Perfil") },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = CalcetinderPink.copy(alpha = 0.15f))
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Auth.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    onAuthSuccess = {
                        navController.navigate(Screen.Swipe.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Swipe.route) {
                SwipeScreen(
                    onNavigateToUpload = { navController.navigate(Screen.Upload.route) }
                )
            }
            composable(Screen.Upload.route) {
                UploadScreen(
                    onUploadSuccess = {
                        navController.navigate(Screen.Swipe.route) {
                            popUpTo(Screen.Upload.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Matches.route) {
                MatchesScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Auth.route) {
                            // Limpia todo el backstack — el usuario no puede volver
                            // con el botón atrás una vez que ha hecho logout
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
