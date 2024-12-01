package uk.ac.tees.mad.plasmalink.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import uk.ac.tees.mad.plasmalink.LocationManager
import java.io.ByteArrayOutputStream


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RequestPlasmaScreen(onBackClick: () -> Unit, onSuccessfulRequest: () -> Unit) {
    var bloodGroup by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var patientCondition by remember { mutableStateOf("") }
    var patientName by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var plasmaType by remember { mutableStateOf("") }
    var specialInstructions by remember { mutableStateOf("") }
    var covidReportUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var loading by remember {
        mutableStateOf(false)
    }
    var expanded by remember { mutableStateOf(false) }
    val plasmaTypes = listOf("Convalescent Plasma", "Standard Plasma", "Other")

    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) {
        if (it.containsValue(true)) {
            locationManager.getCurrentLocation(
                onSuccess = { loc ->
                    if (loc != null) {
                        locationManager.getAddressFromCoordinates(
                            loc.latitude,
                            loc.longitude,
                            onSuccess = {
                                location = it ?: ""
                            },
                            onError = { error ->
                                errorMessage = error
                            }
                        )

                    } else {
                        errorMessage = "Unable to retrieve location."
                    }
                },
                onError = {
                    errorMessage = it
                }
            )
        }

    }


    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            covidReportUri = uri
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->

            bitmap?.let { bm ->
                val bytes = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val contentResolver = context.contentResolver
                val path =
                    MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
                covidReportUri = Uri.parse(path)

            }

        }

    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA) {
            if (it) {
                cameraLauncher.launch(null)
            }
        }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = { Text(text = "Request Plasma", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { ip ->
        Box(modifier = Modifier.padding(ip)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Patient Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = contactInfo,
                    onValueChange = { contactInfo = it },
                    label = { Text("Contact Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = bloodGroup,
                    onValueChange = { bloodGroup = it },
                    label = { Text("Blood Group") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    trailingIcon = {
                        IconButton(onClick = {
                            if (locationPermissionsState.allPermissionsGranted) {
                                locationManager.checkGpsSettings()
                                locationManager.getCurrentLocation(
                                    onSuccess = { loc ->
                                        locationManager.getAddressFromCoordinates(
                                            loc.latitude,
                                            loc.longitude,
                                            onSuccess = {
                                                location = it ?: ""
                                            },
                                            onError = { error ->
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        error,
                                                        withDismissAction = true,
                                                        actionLabel = "Error",
                                                        duration = SnackbarDuration.Short
                                                    )
                                                }
                                            }
                                        )
                                        latitude = loc.latitude
                                        longitude = loc.longitude
                                        errorMessage = ""

                                    },
                                    onError = { error ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                error,
                                                withDismissAction = true,
                                                actionLabel = "Error",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                )
                            } else {
                                locationPermissionsState.launchMultiplePermissionRequest()
                            }
                        }) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Location")
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction =
                        ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = patientCondition,
                    onValueChange = { patientCondition = it },
                    label = { Text("Patient Condition") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = plasmaType,
                        onValueChange = { plasmaType = it },
                        label = { Text("Type of Plasma") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable { expanded = true },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        plasmaTypes.forEach { type ->
                            DropdownMenuItem(
                                onClick = {
                                    plasmaType = type
                                    expanded = false
                                },
                                text = {
                                    Text(text = type)

                                }
                            )
                        }
                    }

                }


                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = specialInstructions,
                    onValueChange = { specialInstructions = it },
                    label = { Text("Special Instructions") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    onClick = {
                        showImagePickerOptions(
                            context,
                            cameraPermissionState,
                            galleryLauncher,
                            cameraLauncher
                        )
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (covidReportUri == null) {

                            Text(text = "Upload Covid Report")
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(covidReportUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Covid Report",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (bloodGroup.isEmpty() || location.isEmpty() || patientCondition.isEmpty() ||
                            patientName.isEmpty() || contactInfo.isEmpty() || plasmaType.isEmpty() || covidReportUri == null
                        ) {
                            errorMessage = "Please fill in all fields."
                        } else {
                            loading = true
                            val storageRef = Firebase.storage.reference
                            val imageRef = storageRef.child("covidReports/${covidReportUri?.lastPathSegment}")

                            val uploadTask = imageRef.putFile(covidReportUri!!)

                            uploadTask.addOnSuccessListener {
                                imageRef.downloadUrl.addOnSuccessListener { uri ->
                                    val requestData = hashMapOf(
                                        "patientName" to patientName,
                                        "contactInfo" to contactInfo,
                                        "bloodGroup" to bloodGroup,
                                        "location" to location,
                                        "latitude" to latitude,
                                        "longitude" to longitude,
                                        "patientCondition" to patientCondition,
                                        "plasmaType" to plasmaType,
                                        "specialInstructions" to specialInstructions,
                                        "covidReportUri" to uri.toString()
                                    )

                                    Firebase.firestore.collection("plasmaRequests")
                                        .add(requestData)
                                        .addOnSuccessListener {
                                            onSuccessfulRequest()
                                        }
                                        .addOnFailureListener { e ->
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = e.message ?: "Firestore error",
                                                    actionLabel = "Error",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                            loading = false
                                        }
                                }
                            }.addOnFailureListener { exception ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = exception.message ?: "Storage error",
                                        actionLabel = "Error",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                loading = false
                            }

                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(text = "Submit Request")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
private fun showImagePickerOptions(
    context: Context,
    cameraPermissionState: PermissionState,
    galleryLauncher: ActivityResultLauncher<String>,
    requestCameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>
) {
    val options = arrayOf("Camera", "Gallery")

    android.app.AlertDialog.Builder(context).setTitle("Select Image Source")
        .setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    if (cameraPermissionState.status.isGranted) {
                        requestCameraLauncher.launch(null)
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }

                1 -> {
                    galleryLauncher.launch("image/*")
                }
            }
        }.show()
}
