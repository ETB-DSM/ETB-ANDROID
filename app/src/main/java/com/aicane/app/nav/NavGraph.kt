package com.aicane.app.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aicane.app.presentation.app.AppViewModel
import com.aicane.app.presentation.auth.LoginEvent
import com.aicane.app.presentation.auth.LoginViewModel
import com.aicane.app.presentation.auth.SignupEvent
import com.aicane.app.presentation.auth.SignupViewModel
import com.aicane.app.presentation.auth.SplashViewModel
import com.aicane.app.presentation.auth.VerifyEvent
import com.aicane.app.presentation.auth.VerifyViewModel
import com.aicane.app.presentation.destination.DestinationListEvent
import com.aicane.app.presentation.destination.DestinationListViewModel
import com.aicane.app.presentation.destination.DestinationRegEvent
import com.aicane.app.presentation.destination.DestinationRegViewModel
import com.aicane.app.presentation.device.DeviceEvent
import com.aicane.app.presentation.device.DeviceViewModel
import com.aicane.app.presentation.guardian.GuardianEvent
import com.aicane.app.presentation.guardian.GuardianViewModel
import com.aicane.app.presentation.mypage.MypageEvent
import com.aicane.app.presentation.mypage.MypageViewModel
import com.aicane.app.presentation.navigation.NavigationEvent
import com.aicane.app.presentation.navigation.NavigationViewModel
import com.aicane.app.ui.screen.SplashScreen
import com.aicane.app.ui.screen.auth.LoginScreen
import com.aicane.app.ui.screen.auth.SignupScreen
import com.aicane.app.ui.screen.auth.VerifyScreen
import com.aicane.app.ui.screen.destination.DestinationListScreen
import com.aicane.app.ui.screen.destination.DestinationRegScreen
import com.aicane.app.ui.screen.destination.SearchScreen
import com.aicane.app.ui.screen.mypage.MypageScreen
import com.aicane.app.ui.screen.navigation.NavigationEndScreen
import com.aicane.app.ui.screen.navigation.NavigationScreen
import com.aicane.app.ui.screen.setup.DeviceScreen
import com.aicane.app.ui.screen.setup.GuardianScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    val appViewModel: AppViewModel = hiltViewModel()
    LaunchedEffect(appViewModel) {
        appViewModel.sessionExpiredFlow.collect {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
    ) {
        composable(Screen.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onNavigateToList  = { navController.navigate(Screen.DestinationList.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                hasSession = viewModel.hasSession,
            )
        }
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        is LoginEvent.NavigateAfterLogin -> {
                            val dest = if (event.isFirst) Screen.Device.route else Screen.DestinationList.route
                            navController.navigate(dest) { popUpTo(Screen.Login.route) { inclusive = true } }
                        }
                    }
                }
            }
            LoginScreen(
                onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                viewModel = viewModel,
            )
        }
        composable(Screen.Signup.route) {
            val viewModel: SignupViewModel = hiltViewModel()
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        is SignupEvent.NavigateToVerify ->
                            navController.navigate(Screen.Verify.createRoute(event.email))
                    }
                }
            }
            SignupScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel,
            )
        }
        composable(
            route = Screen.Verify.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" }),
        ) { backStackEntry ->
            val viewModel: VerifyViewModel = hiltViewModel()
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        VerifyEvent.NavigateToLogin ->
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Signup.route) { inclusive = true }
                            }
                    }
                }
            }
            VerifyScreen(
                email = email,
                onBack = { navController.popBackStack() },
                viewModel = viewModel,
            )
        }
        composable(Screen.Device.route) {
            val viewModel: DeviceViewModel = hiltViewModel()
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        DeviceEvent.NavigateToGuardian ->
                            navController.navigate(Screen.Guardian.route)
                    }
                }
            }
            DeviceScreen(viewModel = viewModel)
        }
        composable(Screen.Guardian.route) {
            val viewModel: GuardianViewModel = hiltViewModel()
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        GuardianEvent.NavigateToDestinationList ->
                            navController.navigate(Screen.DestinationList.route) {
                                popUpTo(Screen.Device.route) { inclusive = true }
                            }
                    }
                }
            }
            GuardianScreen(
                onSkip = {
                    navController.navigate(Screen.DestinationList.route) {
                        popUpTo(Screen.Device.route) { inclusive = true }
                    }
                },
                viewModel = viewModel,
            )
        }
        composable(Screen.DestinationList.route) {
            val viewModel: DestinationListViewModel = hiltViewModel()
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        is DestinationListEvent.NavigateToNavigation ->
                            navController.navigate(
                                Screen.Navigation.createRoute(
                                    sessionId  = event.sessionId,
                                    destLat    = event.destination.latitude,
                                    destLng    = event.destination.longitude,
                                    destRadius = event.destination.radius,
                                    destName   = event.destination.name,
                                )
                            )
                    }
                }
            }
            DestinationListScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToMypage = { navController.navigate(Screen.Mypage.route) },
                viewModel = viewModel,
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onPlaceSelected = { place ->
                    navController.navigate(
                        Screen.DestinationReg.createRoute(
                            name    = place.name,
                            address = place.address,
                            lat     = place.latitude,
                            lng     = place.longitude,
                        )
                    )
                },
            )
        }
        composable(
            route = Screen.DestinationReg.route,
            arguments = listOf(
                navArgument("name")    { type = NavType.StringType; defaultValue = "" },
                navArgument("address") { type = NavType.StringType; defaultValue = "" },
                navArgument("lat")     { type = NavType.FloatType;  defaultValue = 0f },
                navArgument("lng")     { type = NavType.FloatType;  defaultValue = 0f },
            ),
        ) { backStackEntry ->
            val viewModel: DestinationRegViewModel = hiltViewModel()
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        DestinationRegEvent.Saved ->
                            navController.navigate(Screen.DestinationList.route) {
                                popUpTo(Screen.Search.route) { inclusive = true }
                            }
                    }
                }
            }
            DestinationRegScreen(
                onBack           = { navController.popBackStack() },
                prefilledName    = backStackEntry.arguments?.getString("name").orEmpty(),
                prefilledAddress = backStackEntry.arguments?.getString("address").orEmpty(),
                prefilledLat     = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0,
                prefilledLng     = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 0.0,
                viewModel        = viewModel,
            )
        }
        composable(
            route = Screen.Navigation.route,
            arguments = listOf(
                navArgument("sessionId")  { type = NavType.StringType; defaultValue = "" },
                navArgument("destLat")    { type = NavType.FloatType;  defaultValue = 0f },
                navArgument("destLng")    { type = NavType.FloatType;  defaultValue = 0f },
                navArgument("destRadius") { type = NavType.FloatType;  defaultValue = 30f },
                navArgument("destName")   { type = NavType.StringType; defaultValue = "목적지" },
            ),
        ) { backStackEntry ->
            val viewModel: NavigationViewModel = hiltViewModel()
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        NavigationEvent.NavigateToEnd ->
                            navController.navigate(Screen.NavigationEnd.route) {
                                popUpTo(Screen.Navigation.route) { inclusive = true }
                            }
                    }
                }
            }
            NavigationScreen(
                sessionId  = backStackEntry.arguments?.getString("sessionId").orEmpty(),
                destLat    = backStackEntry.arguments?.getFloat("destLat")?.toDouble() ?: 0.0,
                destLng    = backStackEntry.arguments?.getFloat("destLng")?.toDouble() ?: 0.0,
                destRadius = backStackEntry.arguments?.getFloat("destRadius")?.toDouble() ?: 30.0,
                destName   = backStackEntry.arguments?.getString("destName") ?: "목적지",
                viewModel  = viewModel,
            )
        }
        composable(Screen.NavigationEnd.route) {
            NavigationEndScreen(
                onHome = { navController.navigate(Screen.DestinationList.route) { popUpTo(Screen.NavigationEnd.route) { inclusive = true } } },
            )
        }
        composable(Screen.Mypage.route) {
            val viewModel: MypageViewModel = hiltViewModel()
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        MypageEvent.NavigateToLogin ->
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                    }
                }
            }
            MypageScreen(
                onBack    = { navController.popBackStack() },
                viewModel = viewModel,
            )
        }
    }
}
