package com.example.ourcourt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
                    .replace(
                        R.id.fragment_container,
                        SettingsFragment(),
                        "SettingsFragment"
                    )
                    .addToBackStack("settings")
                    .commit()
            }
        }

        val cardHistory = view.findViewById<View>(R.id.card_court_history)
        val tvHistoryCount = view.findViewById<TextView>(R.id.tv_history_count)

        val totalCount = CourtHistoryManager.getHistory(requireContext()).size
        val countWord = if (totalCount == 1) "court" else "courts"
        tvHistoryCount.text = "$totalCount $countWord"

        cardHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CourtHistoryFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}