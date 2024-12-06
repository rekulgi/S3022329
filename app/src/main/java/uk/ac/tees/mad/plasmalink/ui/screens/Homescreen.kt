package uk.ac.tees.mad.plasmalink.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import uk.ac.tees.mad.plasmalink.ui.theme.Purple

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
                    PlasmaDonationRequest(
                        id = doc.id,
                        patientName = doc.getString("patientName") ?: "",
                        contactInfo = doc.getString("contactInfo") ?: "",
                        bloodGroup = doc.getString("bloodGroup") ?: "",
                        location = doc.getString("location") ?: "",
                        latitude = doc.getDouble("latitude") ?: 0.0,
                        longitude = doc.getDouble("longitude") ?: 0.0,
                        patientCondition = doc.getString("patientCondition") ?: "",
                        plasmaType = doc.getString("plasmaType") ?: "",
                        specialInstructions = doc.getString("specialInstructions") ?: "",
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
            errorMessage = ""
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
        bottomBar = {
            BottomNavigationBar(currentScreen = Destinations.HOME_ROUTE) { route ->
                onNavigate(route)
            }
        },

        ) { innerPad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPad)
        ) {
            Column {


                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Welcome to PlasmaLink",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your trusted partner in plasma donation",
                        fontSize = 16.sp
                    )
                }
                Column(
                    Modifier
                        .background(Purple)
                        .height(60.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "\"Plasma is the silent hero of modern medicine.\"",
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = "Plasma Donation Requests",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(donationRequests) { request ->
                            DonationRequestCard(
                                request = request,
                                onNavigate = {
                                    onNavigate(Destinations.DETAIL_ROUTE + "/" + request.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DonationRequestCard(request: PlasmaDonationRequest, onNavigate: () -> Unit) {
    Card(
        onClick = onNavigate,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.outlinedCardElevation(4.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Plasma type: ${request.plasmaType}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${request.patientName}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Bloodtype,
                    contentDescription = "Blood Group",
                    tint = Purple
                )
                Text(
                    text = "${request.bloodGroup}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Purple
                )
                Text(
                    text = "${request.location}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }
    }
}
