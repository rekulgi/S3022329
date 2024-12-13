package uk.ac.tees.mad.plasmalink.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import uk.ac.tees.mad.plasmalink.BottomNavigationBar
import uk.ac.tees.mad.plasmalink.Destinations
import uk.ac.tees.mad.plasmalink.domain.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigate: (String) -> Unit) {
    val firestore = Firebase.firestore
    val auth = Firebase.auth
    var userProfile by remember { mutableStateOf(UserProfile()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userProfile = document.toObject(UserProfile::class.java)!!
                    }
                    isLoading = false
                }
                .addOnFailureListener { exception ->
                    errorMessage = "Error fetching user profile: ${exception.message}"
                    isLoading = false
                }
        } else {
            errorMessage = "User not authenticated"
            isLoading = false
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                errorMessage
            )
            errorMessage = ""
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(title = { Text(text = "Profile") })
        },
        bottomBar = {
            BottomNavigationBar(currentScreen = Destinations.PROFILE_ROUTE) { route ->
                onNavigate(route)
            }
        }
    ) { innerPad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPad),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Profile", fontSize = 24.sp)
        }
    }
}