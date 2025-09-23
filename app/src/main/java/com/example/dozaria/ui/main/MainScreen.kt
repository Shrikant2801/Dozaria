package com.example.dozaria.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dozaria.R
import com.example.dozaria.ui.home.HomeScreen
import com.example.dozaria.ui.challenges.ChallengesScreen
import com.example.dozaria.ui.addpost.AddPostScreen
import com.example.dozaria.ui.leaderboard.LeaderboardScreen
import com.example.dozaria.ui.profile.ProfileScreen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val iconRes: Int? = null
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Challenges : BottomNavItem("challenges", "Challenges", iconRes = android.R.drawable.ic_menu_agenda)
    object AddPost : BottomNavItem("add_post", "Add Post", Icons.Default.Add)
    object Leaderboard : BottomNavItem("leaderboard", "Leaderboard", iconRes = android.R.drawable.ic_menu_sort_by_size)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Challenges,
        BottomNavItem.AddPost,
        BottomNavItem.Leaderboard,
        BottomNavItem.Profile
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            if (item.icon != null) {
                                Icon(item.icon, contentDescription = item.title)
                            } else if (item.iconRes != null) {
                                Icon(
                                    painterResource(id = item.iconRes), 
                                    contentDescription = item.title
                                )
                            }
                        },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
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
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen()
            }
            composable(BottomNavItem.Challenges.route) {
                ChallengesScreen()
            }
            composable(BottomNavItem.AddPost.route) {
                AddPostScreen(onPostCreated = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo(BottomNavItem.Home.route) { inclusive = true }
                    }
                })
            }
            composable(BottomNavItem.Leaderboard.route) {
                LeaderboardScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
        }
    }
}