package com.example.ourcourt

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object CheckInDataStore {
    private const val PREF_NAME = "check_ins_prefs"
    private const val KEY_CHECK_INS = "key_check_ins_json"

    private fun getAllCheckIns(context: Context): MutableList<CheckInItem> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_CHECK_INS, null)
        val list = mutableListOf<CheckInItem>()
        if (jsonStr != null) {
            try {
                val jsonArray = JSONArray(jsonStr)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        CheckInItem(
                            checkInId = obj.optString("checkInId", System.currentTimeMillis().toString()),
                            courtId = obj.getString("courtId"),
                            courtName = obj.getString("courtName"),
                            userName = obj.getString("userName"),
                            userSkill = obj.getString("userSkill"),
                            avatarInitials = obj.getString("avatarInitials"),
                            dateSelection = obj.getString("dateSelection"),
                            timeOfDay = obj.getString("timeOfDay"),
                            playReason = obj.getString("playReason"),
                            matchSimilar = obj.optBoolean("matchSimilar", true),
                            timestamp = obj.getLong("timestamp")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Ensure sample players exist for debug sample court ("default_dolores" or "dolores_park") if not already present
        val debugCourtIds = listOf("default_dolores", "dolores_park")
        val hasSample1 = list.any { debugCourtIds.contains(it.courtId) && it.userName == "Maya Chen" }
        if (!hasSample1) {
            val sample1 = CheckInItem(
                checkInId = "sample_1",
                courtId = "default_dolores",
                courtName = "Dolores Park Courts",
                userName = "Maya Chen",
                userSkill = "UTR 6.5",
                avatarInitials = "MC",
                dateSelection = "Today",
                timeOfDay = "Afternoon",
                playReason = "Looking for Partner",
                matchSimilar = true,
                timestamp = System.currentTimeMillis() - 3600000L
            )
            list.add(sample1)
        }
        val hasSample2 = list.any { debugCourtIds.contains(it.courtId) && it.userName == "Alex Rivera" }
        if (!hasSample2) {
            val sample2 = CheckInItem(
                checkInId = "sample_2",
                courtId = "default_dolores",
                courtName = "Dolores Park Courts",
                userName = "Alex Rivera",
                userSkill = "UTR 5.2",
                avatarInitials = "AR",
                dateSelection = "Today",
                timeOfDay = "Evening",
                playReason = "Looking for Partner",
                matchSimilar = true,
                timestamp = System.currentTimeMillis() - 1800000L
            )
            list.add(sample2)
        }

        return list
    }

    private fun saveAllCheckIns(context: Context, list: List<CheckInItem>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("checkInId", item.checkInId)
                put("courtId", item.courtId)
                put("courtName", item.courtName)
                put("userName", item.userName)
                put("userSkill", item.userSkill)
                put("avatarInitials", item.avatarInitials)
                put("dateSelection", item.dateSelection)
                put("timeOfDay", item.timeOfDay)
                put("playReason", item.playReason)
                put("matchSimilar", item.matchSimilar)
                put("timestamp", item.timestamp)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_CHECK_INS, jsonArray.toString()).apply()
    }

    fun getCheckIns(context: Context, courtId: String, dateSelection: String? = null): List<CheckInItem> {
        val all = getAllCheckIns(context)
        val filtered = all.filter { it.courtId == courtId || (isDebugSampleCourt(it.courtId) && isDebugSampleCourt(courtId)) }
        return if (dateSelection != null) {
            filtered.filter { it.dateSelection.equals(dateSelection, ignoreCase = true) }
        } else {
            filtered
        }.sortedByDescending { it.timestamp }
    }

    private fun isDebugSampleCourt(courtId: String): Boolean {
        return courtId == "default_dolores" || courtId == "dolores_park"
    }

    fun addCheckIn(
        context: Context,
        courtId: String,
        courtName: String,
        userName: String,
        userSkill: String,
        avatarInitials: String,
        dateSelection: String,
        timeOfDay: String,
        playReason: String,
        matchSimilar: Boolean
    ): CheckInItem {
        val list = getAllCheckIns(context).toMutableList()
        val checkIn = CheckInItem(
            checkInId = System.currentTimeMillis().toString(),
            courtId = courtId,
            courtName = courtName,
            userName = userName,
            userSkill = userSkill,
            avatarInitials = avatarInitials,
            dateSelection = dateSelection,
            timeOfDay = timeOfDay,
            playReason = playReason,
            matchSimilar = matchSimilar,
            timestamp = System.currentTimeMillis()
        )
        list.add(0, checkIn)
        saveAllCheckIns(context, list)
        return checkIn
    }

    fun hasCheckedInForDate(context: Context, courtId: String, dateSelection: String, userName: String): Boolean {
        val list = getCheckIns(context, courtId, dateSelection)
        return list.any { it.userName.equals(userName, ignoreCase = true) }
    }

    fun clearAll(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
