package com.example.ourcourt

data class VisitedCourt(
    val courtId: String,
    val courtName: String,
    val location: String,
    val lastVisitedDate: String,
    val timestamp: Long,
    var isPublic: Boolean = true
)

object HitListDataStore {
    var isPublicDefault: Boolean = true

    private val visitedMap = LinkedHashMap<String, VisitedCourt>()

    fun getVisitedCourts(): List<VisitedCourt> {
        return visitedMap.values.sortedByDescending { it.timestamp }
    }

    fun getUniqueCount(): Int {
        return visitedMap.size
    }

    fun recordVisit(
        courtId: String,
        courtName: String,
        location: String,
        dateStr: String,
        timestamp: Long,
        isPublic: Boolean = isPublicDefault
    ) {
        visitedMap[courtId] = VisitedCourt(
            courtId = courtId,
            courtName = courtName,
            location = location,
            lastVisitedDate = dateStr,
            timestamp = timestamp,
            isPublic = isPublic
        )
    }

    fun updateDefaultVisibility(isPublic: Boolean) {
        isPublicDefault = isPublic
        visitedMap.values.forEach { it.isPublic = isPublic }
    }

    fun clearAll() {
        visitedMap.clear()
    }
}
