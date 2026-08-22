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
    data object DestinationReg  : Screen("destination_reg?name={name}&address={address}&lat={lat}&lng={lng}") {
        fun createRoute(name: String, address: String, lat: Double, lng: Double): String {
            val encodedName    = android.net.Uri.encode(name)
            val encodedAddress = android.net.Uri.encode(address)
            return "destination_reg?name=$encodedName&address=$encodedAddress&lat=$lat&lng=$lng"
        }
    }
    data object Navigation      : Screen("navigation/{sessionId}") {
        fun createRoute(sessionId: String) = "navigation/$sessionId"
    }
    data object NavigationEnd   : Screen("navigation_end")
    data object Mypage          : Screen("mypage")
}
