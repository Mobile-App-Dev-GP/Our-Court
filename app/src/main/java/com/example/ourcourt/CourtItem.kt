package com.example.ourcourt

import java.io.Serializable

data class CourtItem(
    val id: String,
    val name: String,
    val rating: String,
    val distance: String,
    val details: String,
    val surface: String,
    val isOpen: Boolean,
    val address: String? = null,
    val hours: String? = null,
    val lighting: String? = null,
    val courtCount: String? = null,
    val photoUrl: String? = null,
    val officialWebsite: String? = null
) : Serializable
