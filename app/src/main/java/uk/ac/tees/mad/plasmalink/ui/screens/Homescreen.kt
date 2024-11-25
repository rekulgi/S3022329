package uk.ac.tees.mad.plasmalink.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.plasmalink.BottomNavigationBar
import uk.ac.tees.mad.plasmalink.Destinations
import uk.ac.tees.mad.plasmalink.ShakeDetector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {

    val context = LocalContext.current

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val shakeDetector = remember {
        ShakeDetector(context) {
            onNavigate(Destinations.PLASMA_REQUEST_ROUTE)
        }
    }

    LaunchedEffect(Unit) {
        snackbarHostState.showSnackbar(
            "Shake to request plasma",
            withDismissAction = true,
            duration = SnackbarDuration.Long
        )
    }

    DisposableEffect(Unit) {
        shakeDetector.start()
        onDispose {
            shakeDetector.stop()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(title = { Text(text = "PlasmaLink") })
        },
        bottomBar = {
            BottomNavigationBar(currentScreen = Destinations.HOME_ROUTE) { route ->
                onNavigate(route)
            }
        },

        ) { innerPad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPad),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Home Screen", fontSize = 24.sp)
        }
    }
}