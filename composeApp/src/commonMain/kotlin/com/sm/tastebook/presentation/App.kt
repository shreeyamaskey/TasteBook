package com.sm.tastebook.presentation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

import com.sm.tastebook.presentation.user.SignUpScreen
import com.sm.tastebook.presentation.user.SignUpViewModel
import com.sm.tastebook.presentation.theme.TasteBookTheme
import com.sm.tastebook.presentation.user.LoginScreen
import com.sm.tastebook.presentation.user.LoginViewModel
import com.sm.tastebook.presentation.home.WelcomeScreen
import com.sm.tastebook.presentation.components.TasteBookAppBar

import androidx.activity.compose.BackHandler
import com.sm.tastebook.domain.user.usecases.LogInUseCase


@Composable
@Preview
fun App(
    token: String?
) {
    TasteBookTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: "landing"

        // Determine the start destination based on token
        val startDestination = if (token != null && token.isNotEmpty()) "home" else "landing"

        Column(modifier = Modifier.fillMaxSize()) {
            // Add the AppBar at the top of the main layout
            TasteBookAppBar(
                currentRoute = currentRoute,
                navController = navController
            )
            
            // NavHost for screen content
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("landing") {
                    LandingScreen(
                        onSignUpClick = { navController.navigate("signup") },
                        onLogInClick = { navController.navigate("login") }
                    )
                }

                composable("signup") {
                    // Use koinViewModel() instead of manually creating the view model
                    val signUpViewModel: SignUpViewModel = koinViewModel()
                    
                    // Add this LaunchedEffect for navigation
                    LaunchedEffect(signUpViewModel.uiState.isSignedUp) {
                        if (signUpViewModel.uiState.isSignedUp) {
                            navController.navigate("home") {
                                popUpTo("landing") { inclusive = true }
                            }
                        }
                    }
                    
                    SignUpScreen(
                        viewModel = signUpViewModel,
                        onBackClick = { navController.navigateUp() },
                        onFirstNameChange = signUpViewModel::onFirstNameChange,
                        onLastNameChange = signUpViewModel::onLastNameChange,
                        onUsernameChange = signUpViewModel::onUsernameChange,
                        onEmailChange = signUpViewModel::onEmailChange,
                        onPasswordChange = signUpViewModel::onPasswordChange,
                        onConfirmPasswordChange = signUpViewModel::onConfirmPasswordChange,
                        onNavigateToHome = {
                            // This will be handled by the LaunchedEffect above
                        },
                        onSignUpClick = signUpViewModel::onSignUpClick
                    )
                }

                composable("login") {
                    val loginUseCase = remember { LogInUseCase() }
                    val loginViewModel: LoginViewModel = koinViewModel()

                    LaunchedEffect(loginViewModel.uiState.isLoggedIn) {
                        if (loginViewModel.uiState.isLoggedIn) {
                            navController.navigate("home") {
                                popUpTo("landing") { inclusive = true }
                            }
                        }
                    }

                    LoginScreen(
                        viewModel = loginViewModel,
                        onBackClick = { navController.navigateUp() },
                        onEmailChange = loginViewModel::onEmailChange,
                        onPasswordChange = loginViewModel::onPasswordChange,
                        onNavigateToHome = {
                            // Handled by Launch Effect
                        },
                        onLoginClick = loginViewModel::onLoginClick
                    )
                }

                composable("home") {
                    WelcomeScreen()

                    // Optional: Handle system back press in the welcome screen
                    BackHandler {
                        // Either do nothing or show a dialog asking if they want to exit the app
                        // For now, we'll just do nothing to prevent back navigation
                    }
                }
                
                // Add placeholders for future screens
                composable("inventory") {
                    // TODO: Add inventory screen
                }

            }
        }

        // This LaunchedEffect has issues - let's fix it
        LaunchedEffect(key1 = token) {
            // If token becomes null or empty after being valid, navigate to landing
            if (token == null || token.isEmpty()) {
                // Only navigate if we're not already on the landing screen
                if (currentRoute != "landing") {
                    navController.navigate("landing") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else if (token.isNotEmpty() && currentRoute == "landing") {
                // If we have a token but we're on landing, go to home
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
}

@Composable
private inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}
