package com.example.ourcourt

import java.io.Serializable

data class CheckInItem(
    val checkInId: String,
    val courtId: String,
    val courtName: String,
    val userName: String,
    val userSkill: String,
    val avatarInitials: String,
    val dateSelection: String, // "Today", "Tomorrow", or date string
    val timeOfDay: String,     // "Morning", "Afternoon", "Evening"
    val playReason: String,    // "Casual Play", "Looking for Partner", "Drills"
    val matchSimilar: Boolean,
    val timestamp: Long
) : Serializable
