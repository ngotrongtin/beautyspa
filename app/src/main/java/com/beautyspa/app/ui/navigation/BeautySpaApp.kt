package com.beautyspa.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.beautyspa.app.ui.screens.booking.BookingScreen
import com.beautyspa.app.ui.screens.home.HomeScreen
import com.beautyspa.app.ui.screens.profile.ProfileScreen
import com.beautyspa.app.ui.screens.services.ServicesScreen
import com.beautyspa.app.ui.screens.chat.ChatScreen
import com.beautyspa.app.ui.screens.login.LoginScreen
import com.beautyspa.app.ui.screens.sharedViewmodel.BookingStatusViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Services : Screen("services", "Services", Icons.Default.Spa)
    object Booking : Screen("booking", "Booking", Icons.Default.CalendarMonth)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Chat : Screen("chat", "Chat", Icons.AutoMirrored.Filled.Chat)
    object Login : Screen("login", "Login")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Services,
    Screen.Booking,
    Screen.Profile
)

@Composable
fun BeautySpaApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bookingStatusViewModel: BookingStatusViewModel = viewModel()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntryInner by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntryInner?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            // If Chat is currently on top, pop it before switching tabs
                            val current = navController.currentBackStackEntry?.destination?.route
                            if (current == Screen.Chat.route) {
                                navController.popBackStack()
                            }
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute != Screen.Chat.route) {
                FloatingActionButton(onClick = { navController.navigate(Screen.Chat.route) }) {
                    if (Screen.Chat.icon != null) {
                        Icon(Screen.Chat.icon, contentDescription = Screen.Chat.title)
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Services.route) { ServicesScreen() }
            composable(Screen.Booking.route) {
                BookingScreen(
                    bookingStatusViewModel = bookingStatusViewModel,
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    bookingStatusViewModel = bookingStatusViewModel,
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            composable(Screen.Chat.route) {
                ChatScreen(bookingStatusViewModel = bookingStatusViewModel)
            }
            composable(Screen.Login.route) {
                LoginScreen(onLoginSuccess = {
                    // Navigate to Home screen after successful login
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                })
            }
        }
    }
}
