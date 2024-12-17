package uk.ac.tees.mad.plasmalink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import uk.ac.tees.mad.plasmalink.BottomNavigationBar
import uk.ac.tees.mad.plasmalink.Destinations
import uk.ac.tees.mad.plasmalink.domain.UserProfile
import uk.ac.tees.mad.plasmalink.ui.theme.Purple

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
                .padding(innerPad)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                ) {
                    ProfilePicture(userProfile.email)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = userProfile.email, style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    TextField(
                        value = userProfile.name,
                        onValueChange = { newName ->
                            userProfile = userProfile.copy(name = newName)
                        },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Purple,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Purple
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = userProfile.phoneNumber,
                        onValueChange = { newPhone ->
                            userProfile = userProfile.copy(phoneNumber = newPhone)
                        },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Purple,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Purple
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = userProfile.bloodGroup,
                        onValueChange = { newBloodGroup ->
                            userProfile = userProfile.copy(bloodGroup = newBloodGroup)
                        },
                        label = { Text("Blood Group") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Purple,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Purple
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            isLoading = true
                            firestore.collection("users")
                                .document(auth.currentUser?.uid!!)
                                .set(userProfile)
                                .addOnSuccessListener {
                                    errorMessage = "Profile updated successfully"
                                    isLoading = false
                                }
                                .addOnFailureListener { exception ->
                                    errorMessage = "Error saving profile: ${exception.message}"
                                    isLoading = false
                                }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Save Changes")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfilePicture(email: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://robohash.org/${email}"),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 2.dp,
                    color = Purple,
                    shape = RoundedCornerShape(20.dp)
                ),
            contentScale = ContentScale.Crop
        )
    }
}
