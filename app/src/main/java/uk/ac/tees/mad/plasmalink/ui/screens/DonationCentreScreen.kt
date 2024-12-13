package uk.ac.tees.mad.plasmalink.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import uk.ac.tees.mad.plasmalink.BottomNavigationBar
import uk.ac.tees.mad.plasmalink.Destinations
import uk.ac.tees.mad.plasmalink.domain.PlasmaDonationCenter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationCentreScreen(onNavigate: (String) -> Unit) {
    val firestore = Firebase.firestore
    var donationCenters by remember { mutableStateOf<List<PlasmaDonationCenter>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val cameraPositionState = rememberCameraPositionState() {
        position = CameraPosition(LatLng(55.3781, -3.4360), 7f, 0f, 0f)
    }

    LaunchedEffect(Unit) {
        firestore.collection("donationCentres")
            .get()
            .addOnSuccessListener { documents ->
                donationCenters = documents.map { doc ->
                    doc.toObject(PlasmaDonationCenter::class.java)
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = "Error fetching data: ${e.message}"
                isLoading = false
            }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "PlasmaLink") })
        },
        bottomBar = {
            BottomNavigationBar(currentScreen = Destinations.DONATION_CENTRE_ROUTE) { route ->
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


            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {


                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    donationCenters.forEach { center ->
                        val markerState = rememberMarkerState(
                            position = LatLng(
                                center.latitude,
                                center.longitude
                            )
                        )
                        Marker(
                            state = markerState,
                            title = center.name,
                            snippet = center.address
                        )
                    }
                }
            }
        }

    }
}