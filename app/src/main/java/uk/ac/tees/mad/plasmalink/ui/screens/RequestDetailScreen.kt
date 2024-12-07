package uk.ac.tees.mad.plasmalink.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import uk.ac.tees.mad.plasmalink.domain.PlasmaDonationRequest
import uk.ac.tees.mad.plasmalink.ui.theme.Purple

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RequestDetailScreen(id: String?, navigateBack: () -> Unit) {
    var request by remember {
        mutableStateOf(PlasmaDonationRequest())
    }
    val context = LocalContext.current
    val callPermission = rememberPermissionState(permission = Manifest.permission.CALL_PHONE)
    var loading by remember {
        mutableStateOf(true)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                errorMessage
            )
            errorMessage = ""
        }
    }
    LaunchedEffect(id) {
        id?.let {
            Firebase.firestore.collection("plasmaRequests").document(it).get()
                .addOnSuccessListener { snapshot ->
                    val data = snapshot.data
                    Log.d("URI", data?.get("covidReportUri") as String? ?: "")
                    request = PlasmaDonationRequest(
                        id = snapshot.id,
                        patientName = data?.get("patientName") as String? ?: "",
                        contactInfo = data?.get("contactInfo") as String? ?: "",
                        bloodGroup = data?.get("bloodGroup") as String? ?: "",
                        location = data?.get("location") as String? ?: "",
                        latitude = data?.get("latitude") as Double? ?: 0.0,
                        longitude = data?.get("longitude") as Double? ?: 0.0,
                        patientCondition = data?.get("patientCondition") as String? ?: "",
                        plasmaType = data?.get("plasmaType") as String? ?: "",
                        specialInstructions = data?.get("specialInstructions") as String? ?: "",
                        covidReportUri = data?.get("covidReportUri") as String? ?: ""
                    )
                    loading = false
                }.addOnFailureListener { ex ->
                    ex.printStackTrace()
                    errorMessage = ex.message ?: "Failed to fetch data"
                    loading = false
                    navigateBack()
                }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Plasma Donation Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = Purple,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = "Patient Name: ${request.patientName}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = Purple
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Patient Details:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                            Icon(
                                imageVector = Icons.Filled.Contacts,
                                contentDescription = "contact",
                                tint = Purple
                            )
                            Icon(
                                imageVector = Icons.Filled.Bloodtype,
                                contentDescription = "bloodtype",
                                tint = Purple
                            )
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "bloodtype",
                                tint = Purple
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = request.contactInfo,
                                fontSize = 18.sp
                            )
                            Text(
                                text = request.bloodGroup,
                                fontSize = 18.sp

                            )
                            Text(
                                text = request.location,
                                fontSize = 18.sp

                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Medical Information:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                    ) {
                        Column(
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, color = Purple)
                        ) {

                            Text(
                                text = "Condition",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            HorizontalDivider(color = Purple)
                            Text(
                                text = "Plasma Type",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            HorizontalDivider(color = Purple)

                            Text(
                                text = "Special Instructions",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1.5f)
                                .border(1.dp, color = Purple)
                                .fillMaxHeight()
                        ) {

                            Text(
                                text = request.patientCondition, fontSize = 18.sp,
                                modifier = Modifier.padding(8.dp),
                                fontWeight = FontWeight.Medium
                            )
                            HorizontalDivider(
                                color = Purple
                            )

                            Text(
                                text = request.plasmaType, fontSize = 18.sp,
                                modifier = Modifier.padding(8.dp),
                                fontWeight = FontWeight.Medium
                            )
                            HorizontalDivider(color = Purple)

                            Text(
                                text = request.specialInstructions, fontSize = 18.sp,
                                modifier = Modifier.padding(8.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column {
                        Text(
                            text = "COVID Report: ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            painter = rememberAsyncImagePainter(model = request.covidReportUri),
                            contentDescription = "Covid report"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (callPermission.status.isGranted) {
                                    val intent = Intent(Intent.ACTION_CALL);
                                    intent.data = Uri.parse("tel:${request.contactInfo}")
                                    context.startActivity(intent)
                                } else {
                                    callPermission.launchPermissionRequest()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Purple,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Contact and donate", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}