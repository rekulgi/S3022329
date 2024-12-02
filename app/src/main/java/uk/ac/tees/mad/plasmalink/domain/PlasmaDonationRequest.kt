package uk.ac.tees.mad.plasmalink.domain

import android.net.Uri

data class PlasmaDonationRequest(
    val id: String = "",
    val patientName: String = "",
    val contactInfo: String = "",
    val bloodGroup: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val patientCondition: String = "",
    val plasmaType: String = "",
    val specialInstructions: String = "",
    val covidReportUri: Uri? = null
)
