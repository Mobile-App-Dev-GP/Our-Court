package com.example.ourcourt

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    private lateinit var prefs: android.content.SharedPreferences

    private lateinit var btnMiles: TextView
    private lateinit var btnKm: TextView
    private lateinit var btnPublic: TextView
    private lateinit var btnPrivate: TextView
    private lateinit var switchDailyReminders: SwitchCompat
    private lateinit var switchCommunityReplies: SwitchCompat

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            prefs.edit().putBoolean("daily_reminders", true).apply()
            switchDailyReminders.isChecked = true
            Toast.makeText(requireContext(), "Daily check-in reminders enabled", Toast.LENGTH_SHORT).show()
        } else {
            switchDailyReminders.isChecked = false
            prefs.edit().putBoolean("daily_reminders", false).apply()
            Toast.makeText(requireContext(), "Notification permission denied. Reminders cannot be enabled.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("our_court_prefs", Context.MODE_PRIVATE)

        // Back button
        view.findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Initialize views
        btnMiles = view.findViewById(R.id.btn_distance_miles)
        btnKm = view.findViewById(R.id.btn_distance_km)
        btnPublic = view.findViewById(R.id.btn_visibility_public)
        btnPrivate = view.findViewById(R.id.btn_visibility_private)
        switchDailyReminders = view.findViewById(R.id.switch_daily_reminders)
        switchCommunityReplies = view.findViewById(R.id.switch_community_replies)

        // Load saved preferences
        loadPreferences()

        // Distance unit selectors
        btnMiles.setOnClickListener {
            saveDistanceUnit("miles")
        }
        btnKm.setOnClickListener {
            saveDistanceUnit("kilometers")
        }

        // Visibility selectors
        btnPublic.setOnClickListener {
            saveVisibility("public")
        }
        btnPrivate.setOnClickListener {
            saveVisibility("private")
        }

        // Switches
        switchDailyReminders.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (requireContext().checkSelfPermission(
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        switchDailyReminders.isChecked = false
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        return@setOnCheckedChangeListener
                    }
                }
            }
            prefs.edit().putBoolean("daily_reminders", isChecked).apply()
        }

        switchCommunityReplies.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("community_replies", isChecked).apply()
        }

        // Account actions
        view.findViewById<LinearLayout>(R.id.row_edit_credential).setOnClickListener {
            val intent = android.content.Intent(requireContext(), EditEmailPasswordActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.row_logout).setOnClickListener {
            showLogoutConfirmationDialog()
        }

        view.findViewById<LinearLayout>(R.id.row_delete_account).setOnClickListener {
            showDeleteAccountConfirmationDialog()
        }
    }

    private fun loadPreferences() {
        val distance = prefs.getString("distance_unit", "miles")
        updateDistanceUI(distance)

        val visibility = prefs.getString("hit_list_visibility", "public")
        updateVisibilityUI(visibility)

        val dailyReminders = prefs.getBoolean("daily_reminders", true)
        switchDailyReminders.isChecked = dailyReminders

        val communityReplies = prefs.getBoolean("community_replies", true)
        switchCommunityReplies.isChecked = communityReplies
    }

    private fun saveDistanceUnit(unit: String) {
        prefs.edit().putString("distance_unit", unit).apply()
        updateDistanceUI(unit)
    }

    private fun updateDistanceUI(unit: String?) {
        if (unit == "kilometers") {
            btnKm.setBackgroundResource(R.drawable.bg_segment_selected)
            btnKm.setTextColor(Color.parseColor("#272727"))
            btnKm.setTypeface(null, android.graphics.Typeface.BOLD)

            btnMiles.setBackgroundResource(android.R.color.transparent)
            btnMiles.setTextColor(Color.parseColor("#686868"))
            btnMiles.setTypeface(null, android.graphics.Typeface.NORMAL)
        } else {
            btnMiles.setBackgroundResource(R.drawable.bg_segment_selected)
            btnMiles.setTextColor(Color.parseColor("#272727"))
            btnMiles.setTypeface(null, android.graphics.Typeface.BOLD)

            btnKm.setBackgroundResource(android.R.color.transparent)
            btnKm.setTextColor(Color.parseColor("#686868"))
            btnKm.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun saveVisibility(vis: String) {
        prefs.edit().putString("hit_list_visibility", vis).apply()
        updateVisibilityUI(vis)
    }

    private fun updateVisibilityUI(vis: String?) {
        if (vis == "private") {
            btnPrivate.setBackgroundResource(R.drawable.bg_segment_selected)
            btnPrivate.setTextColor(Color.parseColor("#272727"))
            btnPrivate.setTypeface(null, android.graphics.Typeface.BOLD)

            btnPublic.setBackgroundResource(android.R.color.transparent)
            btnPublic.setTextColor(Color.parseColor("#686868"))
            btnPublic.setTypeface(null, android.graphics.Typeface.NORMAL)
        } else {
            btnPublic.setBackgroundResource(R.drawable.bg_segment_selected)
            btnPublic.setTextColor(Color.parseColor("#272727"))
            btnPublic.setTypeface(null, android.graphics.Typeface.BOLD)

            btnPrivate.setBackgroundResource(android.R.color.transparent)
            btnPrivate.setTextColor(Color.parseColor("#686868"))
            btnPrivate.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { _, _ ->
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DiscoverFragment())
                    .commit()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteAccountConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage("This action is permanent and will delete all your account data, posts, and history. Are you sure you want to delete your account?")
            .setPositiveButton("Delete") { _, _ ->
                Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DiscoverFragment())
                    .commit()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
