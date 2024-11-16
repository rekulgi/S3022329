package uk.ac.tees.mad.plasmalink.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    Scaffold { innerPad ->
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