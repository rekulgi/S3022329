package uk.ac.tees.mad.plasmalink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.plasmalink.ui.screens.HomeScreen
import uk.ac.tees.mad.plasmalink.ui.screens.LoginScreen
import uk.ac.tees.mad.plasmalink.ui.screens.RegisterScreen
import uk.ac.tees.mad.plasmalink.ui.screens.SplashScreen
import uk.ac.tees.mad.plasmalink.ui.theme.PlasmaLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlasmaLinkTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Destinations.SPLASH_ROUTE
                ) {
                    composable(Destinations.SPLASH_ROUTE) {
                        SplashScreen(
                            onTimeout = {
                                navController.navigate(Destinations.LOGIN_ROUTE) {
                                    popUpTo(Destinations.SPLASH_ROUTE) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(Destinations.LOGIN_ROUTE) {
                        LoginScreen()
                    }
                    composable(Destinations.REGISTER_ROUTE) {
                        RegisterScreen()
                    }
                    composable(Destinations.HOME_ROUTE) {
                        HomeScreen()
                    }
                    composable(Destinations.PLASMA_REQUEST_ROUTE) {

                    }
                    composable(Destinations.DETAIL_ROUTE) {

                    }
                    composable(Destinations.DONATION_CENTRE_ROUTE) {

                    }
                    composable(Destinations.PROFILE_ROUTE) {

                    }
                }
            }
        }
    }
}

object Destinations {
    const val SPLASH_ROUTE = "splash"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val HOME_ROUTE = "home"
    const val PLASMA_REQUEST_ROUTE = "plasma_request"
    const val DETAIL_ROUTE = "detail"
    const val DONATION_CENTRE_ROUTE = "donation_centre"
    const val PROFILE_ROUTE = "profile"
}