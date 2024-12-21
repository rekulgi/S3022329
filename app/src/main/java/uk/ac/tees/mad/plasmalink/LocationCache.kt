package uk.ac.tees.mad.plasmalink

import android.content.Context

class LocationCache(context: Context) {
    private val locationCachePrefs = context.getSharedPreferences("LocationCache", Context.MODE_PRIVATE)

    fun saveLocation(location: String) {
        locationCachePrefs.edit().putString("location", location).apply()
    }

    fun getLocation(): String? {
        return locationCachePrefs.getString("location", null)
    }
}