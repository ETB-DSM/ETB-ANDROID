package com.aicane.app.nav

sealed class Screen(val route: String) {
    data object Splash          : Screen("splash")
    data object Login           : Screen("login")
    data object Signup          : Screen("signup")
    data object Verify          : Screen("verify")
    data object Device          : Screen("device")
    data object Guardian        : Screen("guardian")
    data object DestinationList : Screen("destination_list")
    data object Search          : Screen("search")
    data object DestinationReg  : Screen("destination_reg")
    data object Navigation      : Screen("navigation/{sessionId}") {
        fun createRoute(sessionId: String) = "navigation/$sessionId"
    }
    data object NavigationEnd   : Screen("navigation_end")
    data object Mypage          : Screen("mypage")
}
