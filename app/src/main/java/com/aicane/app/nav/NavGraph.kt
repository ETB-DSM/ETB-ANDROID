package com.aicane.app.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aicane.app.ui.screen.destination.PlaceResult
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
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onNavigateToList  = { navController.navigate(Screen.DestinationList.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                hasSession = false,  // Phase 3 인증 ViewModel 연동 후 실제 토큰 체크로 교체
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                onLoginSuccess     = { isFirst ->
                    if (isFirst) navController.navigate(Screen.Device.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    else navController.navigate(Screen.DestinationList.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
            )
        }
        composable(Screen.Signup.route) {
            SignupScreen(
                onBack          = { navController.popBackStack() },
                onSignupSuccess = { navController.navigate(Screen.Verify.route) },
            )
        }
        composable(Screen.Verify.route) {
            VerifyScreen(
                onBack          = { navController.popBackStack() },
                onVerifySuccess = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Signup.route) { inclusive = true } } },
            )
        }
        composable(Screen.Device.route) {
            DeviceScreen(
                onNext = { navController.navigate(Screen.Guardian.route) },
            )
        }
        composable(Screen.Guardian.route) {
            GuardianScreen(
                onNext = { navController.navigate(Screen.DestinationList.route) { popUpTo(Screen.Device.route) { inclusive = true } } },
                onSkip = { navController.navigate(Screen.DestinationList.route) { popUpTo(Screen.Device.route) { inclusive = true } } },
            )
        }
        composable(Screen.DestinationList.route) {
            DestinationListScreen(
                onNavigateToSearch  = { navController.navigate(Screen.Search.route) },
                onNavigateToMypage  = { navController.navigate(Screen.Mypage.route) },
                onStartNavigation   = { sessionId -> navController.navigate(Screen.Navigation.createRoute(sessionId)) },
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onBack          = { navController.popBackStack() },
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
            DestinationRegScreen(
                onBack          = { navController.popBackStack() },
                onSaved         = { navController.navigate(Screen.DestinationList.route) { popUpTo(Screen.Search.route) { inclusive = true } } },
                prefilledName    = backStackEntry.arguments?.getString("name").orEmpty(),
                prefilledAddress = backStackEntry.arguments?.getString("address").orEmpty(),
                prefilledLat     = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0,
                prefilledLng     = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 0.0,
            )
        }
        composable(
            route = Screen.Navigation.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId").orEmpty()
            NavigationScreen(
                sessionId = sessionId,
                onEnd = { navController.navigate(Screen.NavigationEnd.route) { popUpTo(Screen.Navigation.route) { inclusive = true } } },
            )
        }
        composable(Screen.NavigationEnd.route) {
            NavigationEndScreen(
                onHome = { navController.navigate(Screen.DestinationList.route) { popUpTo(Screen.NavigationEnd.route) { inclusive = true } } },
            )
        }
        composable(Screen.Mypage.route) {
            MypageScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
