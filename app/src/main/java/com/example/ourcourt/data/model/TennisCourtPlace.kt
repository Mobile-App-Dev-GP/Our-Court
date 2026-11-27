package com.example.ourcourt.data.model

import com.example.ourcourt.R
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

/**
 * In-memory domain model representing a Tennis Court place retrieved from Google Places API (New).
 *
 * Designed to strictly conform to Google Maps Platform Terms of Service:
 * - Only `placeId` is stored permanently in backend (Firebase Firestore).
 * - Rich place details remain in-memory and are populated at runtime.
 */
data class TennisCourtPlace(
    val placeId: String,
    val name: String,
    val formattedAddress: String? = null,
    val shortFormattedAddress: String? = null,
    val location: LatLng? = null,
    val rating: Double? = null,
    val userRatingCount: Int? = null,
    val isOpen: Boolean? = null,
    val businessStatus: String? = null,
    val websiteUri: String? = null,
    val googleMapsUri: String? = null,
    val primaryType: String? = null,
    val types: List<String> = emptyList(),
    val weekdayText: List<String> = emptyList(),
    val distanceMiles: Double? = null,
    val fallbackDrawableRes: Int = R.drawable.ic_launcher_background
) {
    /**
     * Formatted distance string (e.g., "0.6 mi" or "N/A").
     */
    val formattedDistance: String
        get() = when {
            distanceMiles != null && distanceMiles >= 0 -> String.format(Locale.US, "%.1f mi", distanceMiles)
            else -> "N/A"
        }

    /**
     * Rating string representation (e.g., "4.8 (120)" or "No rating").
     */
    val formattedRating: String
        get() = when {
            rating != null -> {
                val count = userRatingCount ?: 0
                String.format(Locale.US, "%.1f ★ (%d)", rating, count)
            }
            else -> "Unrated"
        }

    companion object {
        /**
         * Computes straight-line distance in miles between user LatLng and court LatLng.
         */
        fun computeDistanceMiles(userLocation: LatLng, courtLocation: LatLng): Double {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                userLocation.latitude, userLocation.longitude,
                courtLocation.latitude, courtLocation.longitude,
                results
            )
            return results[0] * 0.000621371
        }
    }
}
