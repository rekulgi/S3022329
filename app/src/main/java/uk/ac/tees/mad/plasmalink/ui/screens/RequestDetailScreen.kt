package uk.ac.tees.mad.plasmalink.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberAsyncImagePainter
import uk.ac.tees.mad.plasmalink.domain.PlasmaDonationRequest
import uk.ac.tees.mad.plasmalink.ui.theme.Purple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(id: String?, navigateBack: () -> Unit) {
    val request = PlasmaDonationRequest()
    val context = LocalContext.current

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 16.dp,
                    vertical = pad.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState()),
        ) {


            Spacer(modifier = Modifier.height(8.dp))

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
                        text = "Contact: ${request.contactInfo}",
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Blood Group: ${request.bloodGroup}",
                        fontSize = 18.sp

                    )
                    Text(
                        text = "Location: ${request.location}",
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
                        fontSize = 18.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    HorizontalDivider(color = Purple)
                    Text(
                        text = "Plasma Type",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    HorizontalDivider(color = Purple)

                    Text(
                        text = "Special Instructions",
                        fontSize = 18.sp,
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
                        text = "hi ${request.patientCondition}", fontSize = 18.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    HorizontalDivider(
                        color = Purple
                    )

                    Text(
                        text = request.plasmaType, fontSize = 18.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    HorizontalDivider(color = Purple)

                    Text(
                        text = request.specialInstructions, fontSize = 18.sp,
                        modifier = Modifier.padding(8.dp)
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
                Image(
                    modifier = Modifier
                        .fillMaxWidth(),
                    painter = rememberAsyncImagePainter(model = request.covidReportUri),
                    contentDescription = "Covid report"
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_CALL);
                        intent.data = Uri.parse("tel:${request.contactInfo}")
                        context.startActivity(intent)
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