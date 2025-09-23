package com.example.dozaria.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dozaria.ui.auth.LoginScreen
import com.example.dozaria.ui.auth.SignupScreen
import com.example.dozaria.ui.onboarding.OnboardingScreen
import com.example.dozaria.ui.splash.SplashScreen
import com.example.dozaria.ui.main.MainScreen

@Composable
fun DozariaApp() {
    val navController = rememberNavController()
    val splashViewModel: SplashViewModel = hiltViewModel()
    val splashState by splashViewModel.splashState.collectAsState()
    
    LaunchedEffect(splashState) {
        when (splashState) {
            is SplashState.NavigateToLogin -> {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            is SplashState.NavigateToOnboarding -> {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            is SplashState.NavigateToMain -> {
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> {}
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen()
        }
        
        composable("login") {
            LoginScreen(
                onLoginSuccess = { hasCompletedOnboarding ->
                    if (hasCompletedOnboarding) {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("onboarding") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate("signup")
                }
            )
        }
        
        composable("signup") {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate("onboarding") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("onboarding") {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate("main") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        
        composable("main") {
            MainScreen()
        }
    }
}