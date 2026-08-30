package com.aicane.app.nav

sealed class Screen(val route: String) {
    data object Splash          : Screen("splash")
    data object Login           : Screen("login")
    data object Signup          : Screen("signup")
    data object Verify          : Screen("verify?email={email}") {
        fun createRoute(email: String) = "verify?email=${android.net.Uri.encode(email)}"
    }
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
    data object Navigation      : Screen(
        "navigation?sessionId={sessionId}&destLat={destLat}&destLng={destLng}&destRadius={destRadius}&destName={destName}"
    ) {
        fun createRoute(
            sessionId: String,
            destLat: Double,
            destLng: Double,
            destRadius: Double,
            destName: String,
        ) = "navigation" +
            "?sessionId=$sessionId" +
            "&destLat=${destLat.toFloat()}" +
            "&destLng=${destLng.toFloat()}" +
            "&destRadius=${destRadius.toFloat()}" +
            "&destName=${android.net.Uri.encode(destName)}"
    }
    data object NavigationEnd   : Screen("navigation_end?outcome={outcome}&destName={destName}") {
        fun createRoute(outcome: String, destName: String) =
            "navigation_end?outcome=$outcome&destName=${android.net.Uri.encode(destName)}"
    }
    data object Mypage          : Screen("mypage")
}
