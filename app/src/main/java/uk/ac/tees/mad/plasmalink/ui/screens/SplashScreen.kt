package uk.ac.tees.mad.plasmalink.ui.screens

import android.os.Build
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import uk.ac.tees.mad.plasmalink.R
import uk.ac.tees.mad.plasmalink.ui.theme.Purple

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    LaunchedEffect(Unit) {
        val 
        SystemBarStyle.light(
            Color.Black.toArgb(),
            Color.White.toArgb()
        )
        delay(3000)
        onTimeout()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PlasmaLink",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Connecting Heroes",
                fontSize = 18.sp,
                color = Color(0xFFBB86FC)
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(color = Color.White)
        }
    }
}