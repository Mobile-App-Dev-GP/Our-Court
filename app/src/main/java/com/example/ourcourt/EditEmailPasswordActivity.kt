package com.example.ourcourt

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditEmailPasswordActivity : AppCompatActivity() {

    private lateinit var etCurrentPassword: EditText
    private lateinit var etNewEmail: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText

    private lateinit var tvErrorCurrentPassword: TextView
    private lateinit var tvErrorEmail: TextView
    private lateinit var tvErrorNewPassword: TextView
    private lateinit var tvErrorConfirmPassword: TextView

    private var isCurrentPasswordVisible = false
    private var isNewPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_email_password)

        // Back button
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Initialize views
        etCurrentPassword = findViewById(R.id.et_current_password)
        etNewEmail = findViewById(R.id.et_new_email)
        etNewPassword = findViewById(R.id.et_new_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)

        tvErrorCurrentPassword = findViewById(R.id.tv_error_current_password)
        tvErrorEmail = findViewById(R.id.tv_error_email)
        tvErrorNewPassword = findViewById(R.id.tv_error_new_password)
        tvErrorConfirmPassword = findViewById(R.id.tv_error_confirm_password)

        // Password visibility toggles
        setupPasswordToggle(findViewById(R.id.btn_toggle_current_password), etCurrentPassword) { isCurrentPasswordVisible = it }
        setupPasswordToggle(findViewById(R.id.btn_toggle_new_password), etNewPassword) { isNewPasswordVisible = it }
        setupPasswordToggle(findViewById(R.id.btn_toggle_confirm_password), etConfirmPassword) { isConfirmPasswordVisible = it }

        // Save Changes click listener
        findViewById<Button>(R.id.btn_save_changes).setOnClickListener {
            validateAndSave()
        }
    }

    private fun setupPasswordToggle(toggleButton: ImageButton, editText: EditText, updateState: (Boolean) -> Unit) {
        var visible = false
        toggleButton.setOnClickListener {
            visible = !visible
            updateState(visible)
            val selection = editText.selectionStart
            if (visible) {
                editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                toggleButton.setImageResource(R.drawable.ic_eye)
            } else {
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
                toggleButton.setImageResource(R.drawable.ic_eye_off)
            }
            editText.setSelection(selection)
        }
    }

    private fun validateAndSave() {
        // Clear all errors
        tvErrorCurrentPassword.visibility = View.GONE
        tvErrorEmail.visibility = View.GONE
        tvErrorNewPassword.visibility = View.GONE
        tvErrorConfirmPassword.visibility = View.GONE

        val currentPass = etCurrentPassword.text.toString()
        val newEmail = etNewEmail.text.toString().trim()
        val newPass = etNewPassword.text.toString()
        val confirmPass = etConfirmPassword.text.toString()

        var isValid = true

        // 1. Current password required
        if (currentPass.isEmpty()) {
            tvErrorCurrentPassword.text = "Current password is required to verify identity"
            tvErrorCurrentPassword.visibility = View.VISIBLE
            isValid = false
        }

        // 2. Check if any change requested
        if (newEmail.isEmpty() && newPass.isEmpty()) {
            Toast.makeText(this, "Please enter a new email or new password to update", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // 3. Validate email if supplied
        if (newEmail.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            tvErrorEmail.text = "Please enter a valid email address"
            tvErrorEmail.visibility = View.VISIBLE
            isValid = false
        }

        // 4. Validate new passwords if either is entered
        if (newPass.isNotEmpty() || confirmPass.isNotEmpty()) {
            if (newPass.length < 6) {
                tvErrorNewPassword.text = "Password must be at least 6 characters"
                tvErrorNewPassword.visibility = View.VISIBLE
                isValid = false
            } else if (newPass != confirmPass) {
                tvErrorConfirmPassword.text = "Passwords do not match"
                tvErrorConfirmPassword.visibility = View.VISIBLE
                isValid = false
            }
        }

        if (!isValid) return

        // 5. Verify current password against stored preference
        val prefs = getSharedPreferences("our_court_prefs", Context.MODE_PRIVATE)
        val savedPassword = prefs.getString("account_password", "password123") ?: "password123"

        if (currentPass != savedPassword) {
            tvErrorCurrentPassword.text = "Incorrect current password"
            tvErrorCurrentPassword.visibility = View.VISIBLE
            return
        }

        // 6. Save changes
        val editor = prefs.edit()
        if (newEmail.isNotEmpty()) {
            editor.putString("account_email", newEmail)
        }
        if (newPass.isNotEmpty()) {
            editor.putString("account_password", newPass)
        }
        editor.apply()

        Toast.makeText(this, "Credentials updated successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
}
