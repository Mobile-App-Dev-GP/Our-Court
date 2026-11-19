package com.example.ourcourt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView

class ProfileFragment : Fragment() {

    private lateinit var tvHitCount: TextView
    private lateinit var cardHitList: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        tvHitCount = view.findViewById(R.id.tv_hit_count)
        cardHitList = view.findViewById(R.id.card_hit_list)

        cardHitList.setOnClickListener {
            (activity as? MainActivity)?.openHitList()
        }

        updateHitListCount()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateHitListCount()
    }

    private fun updateHitListCount() {
        val count = HitListDataStore.getUniqueCount()
        tvHitCount.text = if (count == 1) "1 court" else "$count courts"
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

        view.findViewById<View>(R.id.btn_edit_profile)?.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
