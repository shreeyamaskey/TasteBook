package com.sm.tastebook.app

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

import com.sm.tastebook.presentation.user.SignUpScreen
import com.sm.tastebook.presentation.user.SignUpViewModel
import com.sm.tastebook.presentation.LandingScreen
import com.sm.tastebook.presentation.theme.TasteBookTheme
import com.sm.tastebook.presentation.user.LoginScreenWithBackground
import com.sm.tastebook.presentation.user.LoginScreenContent
import com.sm.tastebook.presentation.user.LoginViewModel


@Composable
@Preview
fun App() {
    TasteBookTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "landing"
        ) {
            // Landing screen route
            composable("landing") {
                LandingScreen(
                    onSignUpClick = { navController.navigate("signup") },
                    onLogInClick = { navController.navigate("login") }
                )
            }

            // Sign-up screen route
            composable("signup") {
                // If you're using the SignUpViewModel from earlier:
                val signUpViewModel = SignUpViewModel()
                SignUpScreen(viewModel = signUpViewModel)
            }

            composable("login") {
                val loginViewModel = LoginViewModel()
                LoginScreenContent (
                    viewModel = loginViewModel,
                    onBackClick = {
                        // Pop back to the previous screen (the landing page)
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
//    MaterialTheme {
//        val navController = rememberNavController()
//        NavHost(
//            navController = navController,
//            startDestination = Route.BookGraph
//        ) {
//            navigation<Route.BookGraph>(
//                startDestination = Route.BookList
//            ) {
//                composable<Route.BookList>(
//                    exitTransition = { slideOutHorizontally() },
//                    popEnterTransition = { slideInHorizontally() }
//                ) {
//                    val viewModel = koinViewModel<BookListViewModel>()
//                    val selectedBookViewModel =
//                        it.sharedKoinViewModel<SelectedBookViewModel>(navController)
//
//                    LaunchedEffect(true) {
//                        selectedBookViewModel.onSelectBook(null)
//                    }
//
//                    BookListScreenRoot(
//                        viewModel = viewModel,
//                        onBookClick = { book ->
//                            selectedBookViewModel.onSelectBook(book)
//                            navController.navigate(
//                                Route.BookDetail(book.id)
//                            )
//                        }
//                    )
//                }
//                composable<Route.BookDetail>(
//                    enterTransition = { slideInHorizontally { initialOffset ->
//                        initialOffset
//                    } },
//                    exitTransition = { slideOutHorizontally { initialOffset ->
//                        initialOffset
//                    } }
//                ) {
//                    val selectedBookViewModel =
//                        it.sharedKoinViewModel<SelectedBookViewModel>(navController)
//                    val viewModel = koinViewModel<BookDetailViewModel>()
//                    val selectedBook by selectedBookViewModel.selectedBook.collectAsStateWithLifecycle()
//
//                    LaunchedEffect(selectedBook) {
//                        selectedBook?.let {
//                            viewModel.onAction(BookDetailAction.OnSelectedBookChange(it))
//                        }
//                    }
//
//                    BookDetailScreenRoot(
//                        viewModel = viewModel,
//                        onBackClick = {
//                            navController.navigateUp()
//                        }
//                    )
//                }
//            }
//        }
//
//    }


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