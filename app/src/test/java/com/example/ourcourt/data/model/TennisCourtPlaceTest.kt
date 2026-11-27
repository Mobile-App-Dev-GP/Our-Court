package com.example.ourcourt.data.model

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TennisCourtPlaceTest {

    @Test
    fun testDistanceFormatting() {
        val court = TennisCourtPlace(
            placeId = "test_1",
            name = "Test Court",
            distanceMiles = 0.64
        )
        assertEquals("0.6 mi", court.formattedDistance)
    }

    @Test
    fun testRatingFormatting() {
        val court = TennisCourtPlace(
            placeId = "test_2",
            name = "Test Court 2",
            rating = 4.8,
            userRatingCount = 120
        )
        assertEquals("4.8 ★ (120)", court.formattedRating)
    }

    @Test
    fun testUnratedFormatting() {
        val court = TennisCourtPlace(
            placeId = "test_3",
            name = "New Court"
        )
        assertEquals("Unrated", court.formattedRating)
        assertEquals("N/A", court.formattedDistance)
    }
}
