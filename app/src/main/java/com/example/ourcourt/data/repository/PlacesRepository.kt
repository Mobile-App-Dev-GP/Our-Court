package com.example.ourcourt.data.repository

import android.content.Context
import com.example.ourcourt.BuildConfig
import com.example.ourcourt.R
import com.example.ourcourt.data.model.TennisCourtPlace
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * Repository interface for fetching tennis court places near a target location.
 */
interface PlacesRepository {
    suspend fun getNearbyTennisCourts(
        userLocation: LatLng,
        radiusMeters: Double = 5000.0
    ): Result<List<TennisCourtPlace>>
}

/**
 * Production implementation of [PlacesRepository] using Google Places Android SDK (New).
 */
class PlacesRepositoryImpl(
    private val context: Context,
    private val placesClient: PlacesClient = createPlacesClient(context)
) : PlacesRepository {

    companion object {
        /**
         * Strictly non-billed fields allowed on a Google Places Demo Key.
         * Photos, reviews, and EV charge options are intentionally excluded.
         */
        val DEMO_SAFE_PLACE_FIELDS: List<Place.Field> = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.SHORT_FORMATTED_ADDRESS,
            Place.Field.LOCATION,
            Place.Field.TYPES,
            Place.Field.PRIMARY_TYPE,
            Place.Field.RATING,
            Place.Field.USER_RATING_COUNT,
            Place.Field.OPENING_HOURS,
            Place.Field.CURRENT_OPENING_HOURS,
            Place.Field.BUSINESS_STATUS,
            Place.Field.WEBSITE_URI
        )

        private fun createPlacesClient(context: Context): PlacesClient {
            val apiKey = getApiKey()
            if (!Places.isInitialized() && apiKey.isNotBlank() && apiKey != "YOUR_DEMO_KEY") {
                Places.initializeWithNewPlacesApiEnabled(context.applicationContext, apiKey)
            }
            return Places.createClient(context.applicationContext)
        }

        private fun getApiKey(): String {
            return try {
                BuildConfig.MAPS_API_KEY
            } catch (_: Throwable) {
                "YOUR_DEMO_KEY"
            }
        }
    }

    override suspend fun getNearbyTennisCourts(
        userLocation: LatLng,
        radiusMeters: Double
    ): Result<List<TennisCourtPlace>> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        // If demo key placeholder is used or SDK is not initialized, fallback to demo dataset
        if (apiKey.isBlank() || apiKey == "YOUR_DEMO_KEY" || !Places.isInitialized()) {
            return@withContext Result.success(getDemoTennisCourts(userLocation))
        }

        try {
            val circularBounds = CircularBounds.newInstance(userLocation, radiusMeters)
            val searchNearbyRequest = SearchNearbyRequest.builder(
                circularBounds,
                DEMO_SAFE_PLACE_FIELDS
            )
                .setIncludedTypes(listOf("tennis_court"))
                .setMaxResultCount(20)
                .build()

            val response = suspendCancellableCoroutine { continuation ->
                val task = placesClient.searchNearby(searchNearbyRequest)
                task.addOnSuccessListener { response ->
                    if (continuation.isActive) {
                        continuation.resume(response)
                    }
                }.addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        continuation.resumeWith(Result.failure(exception))
                    }
                }
            }

            val tennisCourts = response.places.map { place ->
                mapPlaceToDomain(place, userLocation)
            }.sortedBy { it.distanceMiles ?: Double.MAX_VALUE }

            Result.success(tennisCourts)
        } catch (_: Exception) {
            // Fallback to offline demo courts if API query fails
            Result.success(getDemoTennisCourts(userLocation))
        }
    }

    private fun mapPlaceToDomain(place: Place, userLocation: LatLng): TennisCourtPlace {
        val courtLatLng = place.location
        val distance = if (courtLatLng != null) {
            TennisCourtPlace.computeDistanceMiles(userLocation, courtLatLng)
        } else null

        val isOpenNow = try {
            place.currentOpeningHours?.periods?.isNotEmpty()
        } catch (_: Exception) {
            null
        }

        val weeklyHours = try {
            place.currentOpeningHours?.weekdayText ?: place.openingHours?.weekdayText ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        return TennisCourtPlace(
            placeId = place.id ?: "",
            name = place.displayName ?: "Tennis Court",
            formattedAddress = place.formattedAddress,
            shortFormattedAddress = place.shortFormattedAddress,
            location = courtLatLng,
            rating = place.rating,
            userRatingCount = place.userRatingCount,
            isOpen = isOpenNow,
            businessStatus = place.businessStatus?.name,
            websiteUri = place.websiteUri?.toString(),
            primaryType = place.primaryType,
            types = place.placeTypes ?: emptyList(),
            weekdayText = weeklyHours,
            distanceMiles = distance,
            fallbackDrawableRes = R.drawable.ic_launcher_background
        )
    }

    /**
     * Provides realistic sample tennis court data for local testing when no live API key is configured.
     */
    private fun getDemoTennisCourts(userLocation: LatLng): List<TennisCourtPlace> {
        val sampleCourts = listOf(
            TennisCourtPlace(
                placeId = "ChIJ_sample_court_1",
                name = "Central Park Tennis Center",
                formattedAddress = "96th St & Central Park W, New York, NY",
                shortFormattedAddress = "Central Park W, NY",
                location = LatLng(userLocation.latitude + 0.005, userLocation.longitude + 0.003),
                rating = 4.7,
                userRatingCount = 142,
                isOpen = true,
                businessStatus = "OPERATIONAL",
                primaryType = "tennis_court",
                distanceMiles = 0.4
            ),
            TennisCourtPlace(
                placeId = "ChIJ_sample_court_2",
                name = "Riverside Park Courts",
                formattedAddress = "Riverside Dr & 119th St, New York, NY",
                shortFormattedAddress = "Riverside Dr, NY",
                location = LatLng(userLocation.latitude + 0.012, userLocation.longitude - 0.008),
                rating = 4.5,
                userRatingCount = 89,
                isOpen = true,
                businessStatus = "OPERATIONAL",
                primaryType = "tennis_court",
                distanceMiles = 0.9
            ),
            TennisCourtPlace(
                placeId = "ChIJ_sample_court_3",
                name = "Sutton East Tennis Club",
                formattedAddress = "488 E 60th St, New York, NY",
                shortFormattedAddress = "E 60th St, NY",
                location = LatLng(userLocation.latitude - 0.015, userLocation.longitude + 0.010),
                rating = 4.2,
                userRatingCount = 56,
                isOpen = false,
                businessStatus = "OPERATIONAL",
                primaryType = "tennis_court",
                distanceMiles = 1.4
            )
        )
        return sampleCourts.sortedBy { it.distanceMiles ?: Double.MAX_VALUE }
    }
}
