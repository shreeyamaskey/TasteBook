package com.sm.tastebook.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

import com.sm.tastebook.presentation.user.SignUpScreen
import com.sm.tastebook.presentation.user.SignUpViewModel
import com.sm.tastebook.presentation.LandingScreen
import com.sm.tastebook.presentation.theme.TasteBookTheme
import com.sm.tastebook.presentation.user.LoginScreen
import com.sm.tastebook.presentation.user.LoginViewModel
import com.sm.tastebook.presentation.home.WelcomeScreen

import androidx.activity.compose.BackHandler
import com.sm.tastebook.domain.user.usecases.LogInUseCase


@Composable
@Preview
fun App() {
    TasteBookTheme {
        val navController = rememberNavController()


        NavHost(
            navController = navController,
            startDestination = "landing"
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
                        navController.navigate("welcome") {
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
                val loginViewModel = remember { LoginViewModel(loginUseCase) }

                LaunchedEffect(loginViewModel.uiState.isLoggedIn) {
                    if (loginViewModel.uiState.isLoggedIn) {
                        navController.navigate("welcome") {
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

            composable("welcome") {
                WelcomeScreen()

                // Optional: Handle system back press in the welcome screen
                BackHandler {
                    // Either do nothing or show a dialog asking if they want to exit the app
                    // For now, we'll just do nothing to prevent back navigation
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
