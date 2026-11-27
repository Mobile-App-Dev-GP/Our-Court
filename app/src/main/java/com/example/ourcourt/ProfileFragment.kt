package com.example.ourcourt

import android.content.Intent
import android.graphics.Color
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
    private lateinit var btnPublic: TextView
    private lateinit var btnPrivate: TextView
    private lateinit var layoutHitVisibility: View
    private lateinit var dividerHitPreview: View
    private lateinit var layoutHitPreview: View
    private lateinit var tvPreviewCourtName: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        tvHitCount = view.findViewById(R.id.tv_hit_count)
        cardHitList = view.findViewById(R.id.card_hit_list)
        btnPublic = view.findViewById(R.id.btn_visibility_public)
        btnPrivate = view.findViewById(R.id.btn_visibility_private)
        layoutHitVisibility = view.findViewById(R.id.layout_hit_visibility)
        dividerHitPreview = view.findViewById(R.id.divider_hit_preview)
        layoutHitPreview = view.findViewById(R.id.layout_hit_preview)
        tvPreviewCourtName = view.findViewById(R.id.tv_preview_court_name)

        cardHitList.setOnClickListener {
            (activity as? MainActivity)?.openHitList()
        }

        // Prevent visibility toggle clicks from opening Hit List
        layoutHitVisibility.setOnClickListener { }
        layoutHitVisibility.isClickable = true
        layoutHitVisibility.isFocusable = true

        btnPublic.setOnClickListener {
            HitListDataStore.updateDefaultVisibility(true)
            updateVisibilityUI(true)
        }

        btnPrivate.setOnClickListener {
            HitListDataStore.updateDefaultVisibility(false)
            updateVisibilityUI(false)
        }

        updateHitListCount()
        updateVisibilityUI(HitListDataStore.isPublicDefault)

        return view
    }

    override fun onResume() {
        super.onResume()
        updateHitListCount()
        updateVisibilityUI(HitListDataStore.isPublicDefault)
    }

    private fun updateHitListCount() {
        val visited = HitListDataStore.getVisitedCourts()
        val count = visited.size
        tvHitCount.text = if (count == 1) "1 court" else "$count courts"

        if (visited.isNotEmpty()) {
            dividerHitPreview.visibility = View.VISIBLE
            layoutHitPreview.visibility = View.VISIBLE
            tvPreviewCourtName.text = visited.first().courtName
        } else {
            dividerHitPreview.visibility = View.GONE
            layoutHitPreview.visibility = View.GONE
        }
    }

    private fun updateVisibilityUI(isPublic: Boolean) {
        if (isPublic) {
            btnPublic.setBackgroundResource(R.drawable.bg_segment_selected)
            btnPublic.setTextColor(Color.parseColor("#272727"))
            btnPublic.setTypeface(null, android.graphics.Typeface.BOLD)

            btnPrivate.setBackgroundResource(android.R.color.transparent)
            btnPrivate.setTextColor(Color.parseColor("#686868"))
            btnPrivate.setTypeface(null, android.graphics.Typeface.NORMAL)
        } else {
            btnPrivate.setBackgroundResource(R.drawable.bg_segment_selected)
            btnPrivate.setTextColor(Color.parseColor("#272727"))
            btnPrivate.setTypeface(null, android.graphics.Typeface.BOLD)

            btnPublic.setBackgroundResource(android.R.color.transparent)
            btnPublic.setTextColor(Color.parseColor("#686868"))
            btnPublic.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
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

        if (cardHistory != null && tvHistoryCount != null) {
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

        view.findViewById<View>(R.id.btn_edit_profile)?.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
