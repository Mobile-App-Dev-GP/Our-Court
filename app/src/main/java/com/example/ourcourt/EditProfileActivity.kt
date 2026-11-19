package com.example.ourcourt

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etDisplayName: EditText
    private lateinit var etEmail: EditText
    private lateinit var tvSelectedSkill: TextView
    private lateinit var tvSelectedCourt: TextView
    private lateinit var etBio: EditText
    private lateinit var tvAvatarInitials: TextView

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            Toast.makeText(this, "Photo selected successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Back button
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Initialize views
        etDisplayName = findViewById(R.id.et_display_name)
        etEmail = findViewById(R.id.et_email)
        tvSelectedSkill = findViewById(R.id.tv_selected_skill)
        tvSelectedCourt = findViewById(R.id.tv_selected_court)
        etBio = findViewById(R.id.et_bio)
        tvAvatarInitials = findViewById(R.id.tv_avatar_initials)

        // Load saved profile data
        loadProfileData()

        // Change Photo action
        findViewById<TextView>(R.id.tv_change_photo).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Email read-only click
        etEmail.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Email Management")
                .setMessage("Email and password changes are handled in Settings. Would you like to go to Settings now?")
                .setPositiveButton("Go to Settings") { _, _ ->
                    val intent = Intent(this, EditEmailPasswordActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Skill level selector
        findViewById<FrameLayout>(R.id.layout_skill_selector).setOnClickListener {
            val skills = arrayOf("NTRP 3.5 / UTR 5.2", "NTRP 4.0 / UTR 6.0", "NTRP 4.5 / UTR 7.0", "NTRP 5.0 / UTR 8.0")
            AlertDialog.Builder(this)
                .setTitle("Select Skill Level")
                .setItems(skills) { _, which ->
                    tvSelectedSkill.text = skills[which]
                }
                .show()
        }

        // Home court selector
        findViewById<FrameLayout>(R.id.layout_court_selector).setOnClickListener {
            val courts = arrayOf("Dolores Park Courts", "Golden Gate Park Tennis Complex", "Stanyan Courts", "Mission Dolores Courts")
            AlertDialog.Builder(this)
                .setTitle("Select Home Court")
                .setItems(courts) { _, which ->
                    tvSelectedCourt.text = courts[which]
                }
                .show()
        }

        // Save Changes
        findViewById<Button>(R.id.btn_save_changes).setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun loadProfileData() {
        val prefs = getSharedPreferences("our_court_prefs", Context.MODE_PRIVATE)
        val name = prefs.getString("profile_display_name", "Alex Rivera") ?: "Alex Rivera"
        val email = prefs.getString("account_email", "alex.rivera@example.com") ?: "alex.rivera@example.com"
        val skill = prefs.getString("profile_skill", "NTRP 3.5 / UTR 5.2") ?: "NTRP 3.5 / UTR 5.2"
        val court = prefs.getString("profile_court", "Dolores Park Courts") ?: "Dolores Park Courts"
        val bio = prefs.getString("profile_bio", "") ?: ""

        etDisplayName.setText(name)
        etEmail.setText(email)
        tvSelectedSkill.text = skill
        tvSelectedCourt.text = court
        etBio.setText(bio)

        // Set initials
        if (name.isNotEmpty()) {
            val parts = name.trim().split(" ")
            val initials = if (parts.size >= 2) {
                "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
            } else {
                name.take(2).uppercase()
            }
            tvAvatarInitials.text = initials
        }
    }

    private fun saveProfileChanges() {
        val name = etDisplayName.text.toString().trim()
        if (name.isEmpty()) {
            etDisplayName.error = "Display name cannot be empty"
            etDisplayName.requestFocus()
            return
        }

        val skill = tvSelectedSkill.text.toString()
        val court = tvSelectedCourt.text.toString()
        val bio = etBio.text.toString()

        val prefs = getSharedPreferences("our_court_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("profile_display_name", name)
            .putString("profile_skill", skill)
            .putString("profile_court", court)
            .putString("profile_bio", bio)
            .apply()

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
}
