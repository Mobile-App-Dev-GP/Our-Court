package com.example.ourcourt

data class CourtReview(
    val reviewId: String,
    val courtId: String,
    val rating: Int,
    val reviewText: String,
    val timestamp: Long,
    val authorName: String = "Tennis Player"
)

object ReviewDataStore {
    private val reviewsMap = mutableMapOf<String, MutableList<CourtReview>>()

    fun getReviews(courtId: String): List<CourtReview> {
        return reviewsMap[courtId] ?: emptyList()
    }

    fun addReview(courtId: String, rating: Int, reviewText: String): CourtReview {
        val review = CourtReview(
            reviewId = System.currentTimeMillis().toString(),
            courtId = courtId,
            rating = rating,
            reviewText = reviewText,
            timestamp = System.currentTimeMillis()
        )
        val list = reviewsMap.getOrPut(courtId) { mutableListOf() }
        list.add(0, review)
        return review
    }

    fun clearAll() {
        reviewsMap.clear()
    }
}
