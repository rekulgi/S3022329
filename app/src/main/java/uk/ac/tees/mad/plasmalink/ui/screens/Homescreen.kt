package uk.ac.tees.mad.plasmalink.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import uk.ac.tees.mad.plasmalink.BottomNavigationBar
import uk.ac.tees.mad.plasmalink.Destinations
import uk.ac.tees.mad.plasmalink.ShakeDetector
import uk.ac.tees.mad.plasmalink.domain.PlasmaDonationRequest

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
    val firestore = Firebase.firestore
    var donationRequests by remember { mutableStateOf<List<PlasmaDonationRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        snackbarHostState.showSnackbar(
            "Shake to request plasma",
            withDismissAction = true,
            duration = SnackbarDuration.Long
        )

        firestore.collection("plasmaRequests")
            .get()
            .addOnSuccessListener { documents ->
                donationRequests = documents.map { doc ->
                    val covidUri = doc.getString("covidReportUri")
                    PlasmaDonationRequest(
                        patientName = doc.getString("patientName") ?: "",
                        contactInfo = doc.getString("contactInfo") ?: "",
                        bloodGroup = doc.getString("bloodGroup") ?: "",
                        location = doc.getString("location") ?: "",
                        latitude = doc.getDouble("latitude") ?: 0.0,
                        longitude = doc.getDouble("longitude") ?: 0.0,
                        patientCondition = doc.getString("patientCondition") ?: "",
                        plasmaType = doc.getString("plasmaType") ?: "",
                        specialInstructions = doc.getString("specialInstructions") ?: "",
                        covidReportUri = Uri.parse(covidUri) ?: null
                    )
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = "Error fetching data: ${e.message}"
                isLoading = false
            }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                errorMessage
            )
        }
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
            Column(Modifier.padding(16.dp)) {
                Text(text = "Plasma Donation Requests", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    LazyColumn {
                        items(donationRequests) { request ->
                            DonationRequestCard(request)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DonationRequestCard(request: PlasmaDonationRequest) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.outlinedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Patient Name: ${request.patientName}",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = "Contact: ${request.contactInfo}")
            Text(text = "Blood Group: ${request.bloodGroup}")
            Text(text = "Location: ${request.location}")
            Text(text = "Condition: ${request.patientCondition}")
            Text(text = "Plasma Type: ${request.plasmaType}")
            Text(text = "Special Instructions: ${request.specialInstructions}")
            Image(
                painter = rememberAsyncImagePainter(request.covidReportUri),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}