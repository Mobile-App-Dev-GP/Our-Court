package com.example.ourcourt

import java.util.Calendar

data class CourtVisitItem(
    val id: String,
    val courtId: String,
    val courtName: String,
    val dateString: String,
    val timeString: String,
    val note: String?,
    val year: Int,
    val month: Int, // 0-based Calendar month
    val day: Int,
    val timestamp: Long
)

object CourtHistoryDataStore {
    val sampleVisits = listOf(
        CourtVisitItem(
            id = "v1",
            courtId = "dolores_park",
            courtName = "Dolores Park Courts",
            dateString = "Oct 29, 2026",
            timeString = "5:30 PM · 1 hour",
            note = "Rallied with Maya",
            year = 2026,
            month = Calendar.OCTOBER,
            day = 29,
            timestamp = 1793237400000L
        ),
        CourtVisitItem(
            id = "v2",
            courtId = "alice_marble",
            courtName = "Alice Marble Courts",
            dateString = "Oct 26, 2026",
            timeString = "9:00 AM · 1.5 hours",
            note = "Morning drills",
            year = 2026,
            month = Calendar.OCTOBER,
            day = 26,
            timestamp = 1792995600000L
        ),
        CourtVisitItem(
            id = "v3",
            courtId = "golden_gate",
            courtName = "Golden Gate Tennis Club",
            dateString = "Oct 22, 2026",
            timeString = "10:00 AM · 1 hour",
            note = "Casual singles",
            year = 2026,
            month = Calendar.OCTOBER,
            day = 22,
            timestamp = 1792653600000L
        )
    )
}
