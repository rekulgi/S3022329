package uk.ac.tees.mad.plasmalink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import uk.ac.tees.mad.plasmalink.ui.screens.DonationCentreScreen
import uk.ac.tees.mad.plasmalink.ui.screens.HomeScreen
import uk.ac.tees.mad.plasmalink.ui.screens.LoginScreen
import uk.ac.tees.mad.plasmalink.ui.screens.ProfileScreen
import uk.ac.tees.mad.plasmalink.ui.screens.RegisterScreen
import uk.ac.tees.mad.plasmalink.ui.screens.RequestDetailScreen
import uk.ac.tees.mad.plasmalink.ui.screens.RequestPlasmaScreen
import uk.ac.tees.mad.plasmalink.ui.screens.SplashScreen
import uk.ac.tees.mad.plasmalink.ui.theme.PlasmaLinkTheme
import uk.ac.tees.mad.plasmalink.ui.theme.Purple

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlasmaLinkTheme {
                val navController = rememberNavController()
                val isLoggedIn = Firebase.auth.currentUser?.uid != null
                NavHost(
                    navController = navController,
                    startDestination = Destinations.SPLASH_ROUTE
                ) {
                    composable(Destinations.SPLASH_ROUTE) {
                        SplashScreen(
                            onTimeout = {
                                navController.navigate(if (isLoggedIn) Destinations.HOME_ROUTE else Destinations.LOGIN_ROUTE) {
                                    popUpTo(Destinations.SPLASH_ROUTE) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(Destinations.LOGIN_ROUTE) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Destinations.HOME_ROUTE) {
                                    popUpTo(Destinations.LOGIN_ROUTE) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate(Destinations.REGISTER_ROUTE)
                            }
                        )
                    }
                    composable(Destinations.REGISTER_ROUTE) {
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate(Destinations.HOME_ROUTE) {
                                    popUpTo(Destinations.REGISTER_ROUTE) {
                                        inclusive = true
                                    }
                                }
                            },
                            onNavigateToLogin = {
                                navController.navigate(Destinations.LOGIN_ROUTE)
                            }
                        )
                    }
                    composable(Destinations.HOME_ROUTE) {
                        HomeScreen(
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }

                    composable(Destinations.PLASMA_REQUEST_ROUTE) {
                        RequestPlasmaScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onSuccessfulRequest = {
                                navController.navigate(Destinations.HOME_ROUTE)
                            }
                        )
                    }

                    composable(
                        route = Destinations.DETAIL_ROUTE + "/{id}",
                        arguments = listOf(
                            navArgument("id") {
                                type = androidx.navigation.NavType.StringType
                            }
                        )
                    ) { backstack ->
                        val id = backstack.arguments?.getString("id")
                        RequestDetailScreen(
                            id = id,
                            navigateBack = { navController.popBackStack() })
                    }

                    composable(Destinations.DONATION_CENTRE_ROUTE) {
                        DonationCentreScreen(onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }

                        })
                    }
                    composable(Destinations.PROFILE_ROUTE) {
                        ProfileScreen(
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
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

@Composable
fun BottomNavigationBar(
    currentScreen: String,
    onNavigateTo: (String) -> Unit
) {
    NavigationBar() {
        val items = listOf(
            NavigationItem(
                Destinations.HOME_ROUTE,
                "Home",
                Icons.Default.Home
            ),
            NavigationItem(
                Destinations.DONATION_CENTRE_ROUTE,
                "Donation Centers",
                Icons.Default.LocalHospital
            ),
            NavigationItem(
                Destinations.PROFILE_ROUTE,
                "Profile",
                Icons.Default.Person
            )
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentScreen == item.route,
                onClick = { onNavigateTo(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Purple,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Purple,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.White
                )
            )
        }
    }
}

data class NavigationItem(val route: String, val label: String, val icon: ImageVector)
