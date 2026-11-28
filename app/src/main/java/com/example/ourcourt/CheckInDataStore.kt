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
        val filtered = all.filter { it.courtId == courtId || it.courtName.equals(courtId, ignoreCase = true) }
        return if (dateSelection != null) {
            filtered.filter { it.dateSelection.equals(dateSelection, ignoreCase = true) }
        } else {
            filtered
        }.sortedByDescending { it.timestamp }
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
