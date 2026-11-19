package com.example.ourcourt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.btn_settings)?.setOnClickListener {
            if (parentFragmentManager.findFragmentByTag("SettingsFragment") == null) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SettingsFragment(), "SettingsFragment")
                    .addToBackStack("settings")
                    .commit()
            }
        }

        view.findViewById<View>(R.id.btn_edit_profile)?.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
