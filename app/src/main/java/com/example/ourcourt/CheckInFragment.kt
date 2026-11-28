package com.example.ourcourt

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class CheckInFragment : Fragment() {

    private var courtItem: CourtItem? = null

    private var selectedDateOption: String = "Today"
    private var selectedTimeOfDay: String = "Evening"

    private lateinit var btnDateToday: MaterialButton
    private lateinit var btnDateTomorrow: MaterialButton
    private lateinit var btnDateCustom: MaterialButton

    private lateinit var btnTimeMorning: MaterialButton
    private lateinit var btnTimeAfternoon: MaterialButton
    private lateinit var btnTimeEvening: MaterialButton

    private lateinit var rgPlayReason: RadioGroup
    private lateinit var switchMatchSimilar: SwitchCompat
    private lateinit var btnConfirmCheckIn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courtItem = arguments?.getSerializable("court_item") as? CourtItem
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_check_in, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back)
        val tvCourtName = view.findViewById<TextView>(R.id.tv_court_name)

        btnDateToday = view.findViewById(R.id.btn_date_today)
        btnDateTomorrow = view.findViewById(R.id.btn_date_tomorrow)
        btnDateCustom = view.findViewById(R.id.btn_date_custom)

        btnTimeMorning = view.findViewById(R.id.btn_time_morning)
        btnTimeAfternoon = view.findViewById(R.id.btn_time_afternoon)
        btnTimeEvening = view.findViewById(R.id.btn_time_evening)

        rgPlayReason = view.findViewById(R.id.rg_play_reason)
        switchMatchSimilar = view.findViewById(R.id.switch_match_similar)
        btnConfirmCheckIn = view.findViewById(R.id.btn_confirm_check_in)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        courtItem?.let { court ->
            tvCourtName.text = court.name
        }

        setupDateButtons()
        setupTimeButtons()

        btnConfirmCheckIn.setOnClickListener {
            val court = courtItem ?: CourtItem(
                id = "default_dolores",
                name = "Dolores Park Courts",
                rating = "4.8",
                distance = "0.5 mi",
                details = "Dolores St & 18th St · San Francisco",
                surface = "Hard",
                isOpen = true,
                address = "Dolores St & 18th St",
                hours = "Open now · Closes 10 PM",
                lighting = "Lit",
                courtCount = "6 courts"
            )

            val selectedRadioButtonId = rgPlayReason.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                Toast.makeText(requireContext(), "Please select what brings you to the court.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val rb = view.findViewById<RadioButton>(selectedRadioButtonId)
            val playReason = rb?.text?.toString() ?: "Casual Play"

            val userName = "You" // Current user
            val userSkill = "UTR 6.0"
            val initials = "YO"

            // Prevent duplicate entries for same date & court
            if (CheckInDataStore.hasCheckedInForDate(requireContext(), court.id, selectedDateOption, userName)) {
                Toast.makeText(requireContext(), "You have already checked in for $selectedDateOption at this court!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save check-in
            CheckInDataStore.addCheckIn(
                context = requireContext(),
                courtId = court.id,
                courtName = court.name,
                userName = userName,
                userSkill = userSkill,
                avatarInitials = initials,
                dateSelection = selectedDateOption,
                timeOfDay = selectedTimeOfDay,
                playReason = playReason,
                matchSimilar = switchMatchSimilar.isChecked
            )

            // Also record to Court History and Hit List
            val now = System.currentTimeMillis()
            val dateFormat = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
            val dateStr = dateFormat.format(java.util.Date(now))
            CourtHistoryManager.recordCourtView(requireContext(), court.id, court.name)
            HitListDataStore.recordVisit(
                courtId = court.id,
                courtName = court.name,
                location = court.address ?: court.details,
                dateStr = dateStr,
                timestamp = now
            )

            Toast.makeText(requireContext(), "Check-in confirmed successfully!", Toast.LENGTH_SHORT).show()

            // Report about shared storage / backend
            // "Check-ins are stored in CheckInDataStore (local in-memory/runtime storage). Cross-device sharing across different physical devices requires a cloud backend like Firebase Firestore."

            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun setupDateButtons() {
        val selectedBg = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#272727"))
        val selectedText = android.graphics.Color.parseColor("#FFFFFF")
        val unselectedBg = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFFFFF"))
        val unselectedText = android.graphics.Color.parseColor("#272727")
        val strokeColor = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D4D4D4"))
        val transparentStroke = android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)

        fun updateDateUi(selected: String) {
            selectedDateOption = selected
            val buttons = listOf(btnDateToday, btnDateTomorrow, btnDateCustom)
            buttons.forEachIndexed { index, btn ->
                val isSelected = when (index) {
                    0 -> selected == "Today"
                    1 -> selected == "Tomorrow"
                    2 -> selected != "Today" && selected != "Tomorrow"
                    else -> false
                }
                if (isSelected) {
                    btn.backgroundTintList = selectedBg
                    btn.setTextColor(selectedText)
                    btn.strokeColor = transparentStroke
                    btn.strokeWidth = 0
                } else {
                    btn.backgroundTintList = unselectedBg
                    btn.setTextColor(unselectedText)
                    btn.strokeColor = strokeColor
                    btn.strokeWidth = 2
                }
            }
        }

        btnDateToday.setOnClickListener { updateDateUi("Today") }
        btnDateTomorrow.setOnClickListener { updateDateUi("Tomorrow") }
        btnDateCustom.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val cal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
                    val sdf = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
                    val formatted = sdf.format(cal.time)
                    btnDateCustom.text = formatted
                    updateDateUi(formatted)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        updateDateUi("Today")
    }

    private fun setupTimeButtons() {
        val selectedBg = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#272727"))
        val selectedText = android.graphics.Color.parseColor("#FFFFFF")
        val unselectedBg = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFFFFF"))
        val unselectedText = android.graphics.Color.parseColor("#272727")
        val strokeColor = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D4D4D4"))
        val transparentStroke = android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)

        fun updateTimeUi(selected: String) {
            selectedTimeOfDay = selected
            val buttons = listOf(btnTimeMorning, btnTimeAfternoon, btnTimeEvening)
            val names = listOf("Morning", "Afternoon", "Evening")
            buttons.forEachIndexed { index, btn ->
                if (names[index] == selected) {
                    btn.backgroundTintList = selectedBg
                    btn.setTextColor(selectedText)
                    btn.strokeColor = transparentStroke
                    btn.strokeWidth = 0
                } else {
                    btn.backgroundTintList = unselectedBg
                    btn.setTextColor(unselectedText)
                    btn.strokeColor = strokeColor
                    btn.strokeWidth = 2
                }
            }
        }

        btnTimeMorning.setOnClickListener { updateTimeUi("Morning") }
        btnTimeAfternoon.setOnClickListener { updateTimeUi("Afternoon") }
        btnTimeEvening.setOnClickListener { updateTimeUi("Evening") }
        updateTimeUi("Evening")
    }
}
