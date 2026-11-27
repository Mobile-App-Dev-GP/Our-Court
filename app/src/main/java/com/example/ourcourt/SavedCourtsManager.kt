package com.example.ourcourt

import android.content.Context
import android.content.SharedPreferences
import com.example.ourcourt.data.model.TennisCourtPlace
import org.json.JSONArray
import org.json.JSONObject

data class SavedCourtItem(
    val placeId: String,
    val courtName: String,
    val formattedAddress: String,
    val rating: String,
    val distance: String,
    val isOpen: Boolean?,
    val websiteUri: String?,
    val surface: String?
)

object SavedCourtsManager {
    private const val PREF_NAME = "saved_courts_prefs"
    private const val KEY_SAVED_LIST = "key_saved_courts_list"

    fun saveCourt(context: Context, court: TennisCourtPlace) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("saved_${court.placeId}", true).apply()

        val currentList = getSavedCourts(context).toMutableList()
        currentList.removeAll { it.placeId == court.placeId }

        val newItem = SavedCourtItem(
            placeId = court.placeId,
            courtName = court.name,
            formattedAddress = court.formattedAddress ?: court.shortFormattedAddress ?: "",
            rating = court.formattedRating,
            distance = court.formattedDistance,
            isOpen = court.isOpen,
            websiteUri = court.websiteUri,
            surface = court.primaryType
        )
        currentList.add(0, newItem)
        saveListToPrefs(prefs, currentList)
    }

    fun removeCourt(context: Context, placeId: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("saved_$placeId", false).apply()

        val currentList = getSavedCourts(context).toMutableList()
        currentList.removeAll { it.placeId == placeId }
        saveListToPrefs(prefs, currentList)
    }

    fun isSaved(context: Context, placeId: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("saved_$placeId", false)
    }

    fun getSavedCourts(context: Context): List<SavedCourtItem> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_SAVED_LIST, null) ?: return emptyList()
        val list = mutableListOf<SavedCourtItem>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val pId = obj.getString("placeId")
                if (prefs.getBoolean("saved_$pId", true)) {
                    list.add(
                        SavedCourtItem(
                            placeId = pId,
                            courtName = obj.optString("courtName", "Tennis Court"),
                            formattedAddress = obj.optString("formattedAddress", ""),
                            rating = obj.optString("rating", "Unrated"),
                            distance = obj.optString("distance", "N/A"),
                            isOpen = if (obj.has("isOpen")) obj.getBoolean("isOpen") else null,
                            websiteUri = obj.optString("websiteUri", ""),
                            surface = obj.optString("surface", "Tennis court")
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun saveListToPrefs(prefs: SharedPreferences, list: List<SavedCourtItem>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("placeId", item.placeId)
                put("courtName", item.courtName)
                put("formattedAddress", item.formattedAddress)
                put("rating", item.rating)
                put("distance", item.distance)
                if (item.isOpen != null) put("isOpen", item.isOpen)
                put("websiteUri", item.websiteUri ?: "")
                put("surface", item.surface ?: "")
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_SAVED_LIST, jsonArray.toString()).apply()
    }
}
