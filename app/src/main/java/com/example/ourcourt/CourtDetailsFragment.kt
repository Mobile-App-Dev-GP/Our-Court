package com.example.ourcourt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class CourtDetailsFragment : Fragment() {

    private var courtItem: CourtItem? = null

    private lateinit var layoutSignupPlayersContainer: LinearLayout
    private lateinit var tvSignupSubtitle: TextView
    private lateinit var tvSignupEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courtItem = arguments?.getSerializable("court_item") as? CourtItem ?: CourtItem(
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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_court_details, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back)
        val tvCourtName = view.findViewById<TextView>(R.id.tv_court_name)
        val tvAddressDistance = view.findViewById<TextView>(R.id.tv_address_distance)
        val tvRating = view.findViewById<TextView>(R.id.tv_rating)
        val tvHours = view.findViewById<TextView>(R.id.tv_hours)
        val tvSurfaceTag = view.findViewById<TextView>(R.id.tv_surface_tag)
        val tvLightingTag = view.findViewById<TextView>(R.id.tv_lighting_tag)
        val tvCourtCountTag = view.findViewById<TextView>(R.id.tv_court_count_tag)
        val btnLeaveReview = view.findViewById<MaterialButton>(R.id.btn_leave_review)
        val btnSaveCourt = view.findViewById<MaterialButton>(R.id.btn_save_court)
        val tvOfficialWebsite = view.findViewById<TextView>(R.id.tv_official_website)
        val btnCheckIn = view.findViewById<MaterialButton>(R.id.btn_check_in)
        val ivCourtPhoto = view.findViewById<ImageView>(R.id.iv_court_photo)
        val tvPhotoText = view.findViewById<TextView>(R.id.tv_photo_text)

        layoutSignupPlayersContainer = view.findViewById(R.id.layout_signup_players_container)
        tvSignupSubtitle = view.findViewById(R.id.tv_signup_subtitle)
        tvSignupEmptyState = view.findViewById(R.id.tv_signup_empty_state)

        btnBack.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DiscoverFragment())
                    .commit()
            }
        }

        courtItem?.let { court ->
            tvCourtName.text = court.name
            val addr = court.address ?: court.details
            tvAddressDistance.text = "$addr · ${court.distance}"
            tvRating.text = court.rating

            if (!court.hours.isNullOrBlank()) {
                tvHours.visibility = View.VISIBLE
                tvHours.text = court.hours
            } else if (court.isOpen) {
                tvHours.visibility = View.VISIBLE
                tvHours.text = "Open now"
            } else {
                tvHours.visibility = View.GONE
            }

            if (!court.surface.isNullOrBlank()) {
                tvSurfaceTag.visibility = View.VISIBLE
                tvSurfaceTag.text = court.surface
            } else {
                tvSurfaceTag.visibility = View.GONE
            }

            if (!court.lighting.isNullOrBlank()) {
                tvLightingTag.visibility = View.VISIBLE
                tvLightingTag.text = court.lighting
            } else {
                tvLightingTag.visibility = View.GONE
            }

            if (!court.courtCount.isNullOrBlank()) {
                tvCourtCountTag.visibility = View.VISIBLE
                tvCourtCountTag.text = court.courtCount
            } else {
                tvCourtCountTag.visibility = View.GONE
            }

            if (!court.officialWebsite.isNullOrBlank()) {
                tvOfficialWebsite.visibility = View.VISIBLE
                tvOfficialWebsite.setOnClickListener {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(court.officialWebsite))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Unable to open website", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                tvOfficialWebsite.visibility = View.GONE
            }

            // Photo handling placeholder
            if (!court.photoUrl.isNullOrBlank()) {
                ivCourtPhoto.visibility = View.VISIBLE
                tvPhotoText.text = "Court photo"
            } else {
                ivCourtPhoto.visibility = View.GONE
                tvPhotoText.text = "Court photo gallery · 1 of 4"
            }
        }

        btnLeaveReview.setOnClickListener {
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
            val fragment = LeaveReviewFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("court_item", court)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("LeaveReview")
                .commit()
        }

        btnCheckIn.setOnClickListener {
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
            val fragment = CheckInFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("court_item", court)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("CheckIn")
                .commit()
        }

        btnSaveCourt.setOnClickListener {
            Toast.makeText(requireContext(), "Saved court toggled", Toast.LENGTH_SHORT).show()
        }

        loadCheckIns()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadCheckIns()
    }

    private fun loadCheckIns() {
        val courtId = courtItem?.id ?: "default_dolores"
        val checkIns = CheckInDataStore.getCheckIns(requireContext(), courtId)

        tvSignupSubtitle.text = "Today · ${checkIns.size} player${if (checkIns.size == 1) "" else "s"} signed up"

        layoutSignupPlayersContainer.removeAllViews()
        if (checkIns.isEmpty()) {
            tvSignupEmptyState.visibility = View.VISIBLE
            layoutSignupPlayersContainer.visibility = View.GONE
        } else {
            tvSignupEmptyState.visibility = View.GONE
            layoutSignupPlayersContainer.visibility = View.VISIBLE

            val inflater = LayoutInflater.from(requireContext())
            for (item in checkIns) {
                val rowView = inflater.inflate(R.layout.item_check_in_player, layoutSignupPlayersContainer, false)
                val tvInitials = rowView.findViewById<TextView>(R.id.tv_avatar_initials)
                val tvName = rowView.findViewById<TextView>(R.id.tv_player_name)
                val tvDetails = rowView.findViewById<TextView>(R.id.tv_player_details)
                val tvTime = rowView.findViewById<TextView>(R.id.tv_player_time)

                tvInitials.text = item.avatarInitials
                tvName.text = item.userName
                tvDetails.text = "${item.userSkill} · ${item.playReason}"
                tvTime.text = item.timeOfDay

                layoutSignupPlayersContainer.addView(rowView)
            }
        }
    }
}
