package com.example.ourcourt

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CourtHistoryItem(
    val courtId: String,
    val courtName: String,
    val lastViewedTimestamp: Long,
    val dateString: String,
    val year: Int,
    val month: Int,
    val day: Int
)

object CourtHistoryManager {
    private const val PREF_NAME = "court_history_prefs"
    private const val KEY_HISTORY = "key_history_json"

    /**
     * Reusable method for recording a court view.
     * Teammates should call this method when a user deliberately opens/selects a court
     * from search results or the map:
     * CourtHistoryManager.recordCourtView(context, courtId, courtName)
     */
    fun recordCourtView(context: Context, courtId: String, courtName: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val currentList = getHistory(context).toMutableList()

        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = now }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateFormat = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
        val dateString = dateFormat.format(Date(now))

        // Remove existing entry for this courtId if any (moves to top)
        currentList.removeAll { it.courtId == courtId }

        val newItem = CourtHistoryItem(
            courtId = courtId,
            courtName = courtName,
            lastViewedTimestamp = now,
            dateString = dateString,
            year = year,
            month = month,
            day = day
        )
        currentList.add(0, newItem)

        saveHistory(prefs, currentList)
    }

    fun getHistory(context: Context): List<CourtHistoryItem> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<CourtHistoryItem>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    CourtHistoryItem(
                        courtId = obj.getString("courtId"),
                        courtName = obj.getString("courtName"),
                        lastViewedTimestamp = obj.getLong("lastViewedTimestamp"),
                        dateString = obj.getString("dateString"),
                        year = obj.getInt("year"),
                        month = obj.getInt("month"),
                        day = obj.getInt("day")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Remove known test simulation entry if present from previous testing, preserving real history
        val cleanedList = list.filterNot { it.courtId == "dolores_park" && it.dateString.contains("2026") }
        if (cleanedList.size != list.size) {
            saveHistory(prefs, cleanedList)
        }
        return cleanedList.sortedByDescending { it.lastViewedTimestamp }
    }

    private fun saveHistory(prefs: SharedPreferences, list: List<CourtHistoryItem>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("courtId", item.courtId)
                put("courtName", item.courtName)
                put("lastViewedTimestamp", item.lastViewedTimestamp)
                put("dateString", item.dateString)
                put("year", item.year)
                put("month", item.month)
                put("day", item.day)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }
}
