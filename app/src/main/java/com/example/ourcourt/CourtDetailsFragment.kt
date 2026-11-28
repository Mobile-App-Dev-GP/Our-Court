package com.example.ourcourt

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.example.ourcourt.data.model.TennisCourtPlace
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CourtDetailsFragment : Fragment() {

    private var placeId: String = ""
    private var courtName: String = ""
    private var formattedAddress: String = ""
    private var shortAddress: String = ""
    private var distanceStr: String = ""
    private var ratingStr: String = ""
    private var openStatusText: String = ""
    private var websiteUrl: String = ""
    private var surfaceType: String = ""

    private lateinit var tvSavedButton: MaterialButton
    private lateinit var containerSignups: LinearLayout
    private lateinit var tvSignupSubtitle: TextView
    private lateinit var tvEmptySignups: TextView

    companion object {
        private const val ARG_PLACE_ID = "arg_place_id"
        private const val ARG_COURT_NAME = "arg_court_name"
        private const val ARG_ADDRESS = "arg_address"
        private const val ARG_SHORT_ADDRESS = "arg_short_address"
        private const val ARG_DISTANCE = "arg_distance"
        private const val ARG_RATING = "arg_rating"
        private const val ARG_OPEN_STATUS = "arg_open_status"
        private const val ARG_WEBSITE = "arg_website"
        private const val ARG_SURFACE = "arg_surface"

        fun newInstance(court: TennisCourtPlace): CourtDetailsFragment {
            val fragment = CourtDetailsFragment()
            val args = Bundle().apply {
                putString(ARG_PLACE_ID, court.placeId)
                putString(ARG_COURT_NAME, court.name)
                putString(ARG_ADDRESS, court.formattedAddress ?: "")
                putString(ARG_SHORT_ADDRESS, court.shortFormattedAddress ?: "")
                putString(ARG_DISTANCE, court.formattedDistance)
                putString(ARG_RATING, court.formattedRating)
                putString(ARG_OPEN_STATUS, if (court.isOpen == true) "Open · 6:00 AM - 10:00 PM" else if (court.isOpen == false) "Closed" else court.businessStatus ?: "Available")
                putString(ARG_WEBSITE, court.websiteUri ?: "")
                putString(ARG_SURFACE, court.primaryType?.replace("_", " ")?.replaceFirstChar { it.uppercase() } ?: "Tennis court")
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            placeId = it.getString(ARG_PLACE_ID, "")
            courtName = it.getString(ARG_COURT_NAME, "Tennis Court")
            formattedAddress = it.getString(ARG_ADDRESS, "")
            shortAddress = it.getString(ARG_SHORT_ADDRESS, "")
            distanceStr = it.getString(ARG_DISTANCE, "N/A")
            ratingStr = it.getString(ARG_RATING, "Unrated")
            openStatusText = it.getString(ARG_OPEN_STATUS, "Available")
            websiteUrl = it.getString(ARG_WEBSITE, "")
            surfaceType = it.getString(ARG_SURFACE, "Tennis court")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_court_details, container, false)

        // Bind Views
        val btnBack = view.findViewById<View>(R.id.btn_back)
        val btnShare = view.findViewById<View>(R.id.btn_share)
        val tvCourtTitle = view.findViewById<TextView>(R.id.tv_court_title)
        val tvRating = view.findViewById<TextView>(R.id.tv_court_rating)
        val tvAddressDistance = view.findViewById<TextView>(R.id.tv_court_address_distance)
        val tvHoursStatus = view.findViewById<TextView>(R.id.tv_court_hours_status)
        val tvBadgeSurface = view.findViewById<TextView>(R.id.tv_badge_surface)
        val btnLeaveReview = view.findViewById<MaterialButton>(R.id.btn_leave_review)
        tvSavedButton = view.findViewById(R.id.btn_save_court)
        val layoutWebsite = view.findViewById<View>(R.id.layout_website)
        val btnCheckInPlay = view.findViewById<View>(R.id.btn_check_in_play)

        containerSignups = view.findViewById(R.id.container_player_signups)
        tvSignupSubtitle = view.findViewById(R.id.tv_signup_subtitle)
        tvEmptySignups = view.findViewById(R.id.tv_empty_signups)

        // Set Text Data
        tvCourtTitle.text = courtName
        tvRating.text = ratingStr
        
        val displayLoc = if (shortAddress.isNotBlank()) shortAddress else formattedAddress
        tvAddressDistance.text = "$distanceStr · $displayLoc"
        tvHoursStatus.text = openStatusText
        tvBadgeSurface.text = surfaceType

        // Top Bar Actions
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnShare.setOnClickListener {
            shareCourtDetails()
        }

        // Save Court State Setup
        updateSaveButtonUi(isCourtSaved())
        tvSavedButton.setOnClickListener {
            toggleSaveCourt()
        }

        // Leave a Review Flow
        btnLeaveReview.setOnClickListener {
            val courtItem = CourtItem(
                id = placeId.ifEmpty { "default_court" },
                name = courtName,
                rating = ratingStr.replace("★", "").trim(),
                distance = distanceStr,
                details = displayLoc,
                surface = surfaceType,
                isOpen = openStatusText.contains("Open"),
                address = formattedAddress
            )
            val fragment = LeaveReviewFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("court_item", courtItem)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("LeaveReview")
                .commit()
        }

        // Website Link
        if (websiteUrl.isNotBlank()) {
            layoutWebsite.visibility = View.VISIBLE
            layoutWebsite.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                    startActivity(intent)
                } catch (_: Exception) {
                    Toast.makeText(requireContext(), "Unable to open website", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            layoutWebsite.visibility = View.GONE
        }

        // Check In to Play Button -> Launches Kamran's CheckInFragment with dynamic API court info
        btnCheckInPlay.setOnClickListener {
            val courtItem = CourtItem(
                id = placeId.ifEmpty { "default_court" },
                name = courtName,
                rating = ratingStr.replace("★", "").trim(),
                distance = distanceStr,
                details = displayLoc,
                surface = surfaceType,
                isOpen = openStatusText.contains("Open"),
                address = formattedAddress
            )
            val fragment = CheckInFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("court_item", courtItem)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("CheckIn")
                .commit()
        }

        // Load Sign-Up Sheet
        loadCommunitySignups()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadCommunitySignups()
    }

    private fun isCourtSaved(): Boolean {
        return SavedCourtsManager.isSaved(requireContext(), placeId)
    }

    private fun toggleSaveCourt() {
        val context = requireContext()
        val currentState = isCourtSaved()
        if (currentState) {
            SavedCourtsManager.removeCourt(context, placeId)
        } else {
            val dummyCourt = TennisCourtPlace(
                placeId = placeId,
                name = courtName,
                formattedAddress = formattedAddress,
                shortFormattedAddress = shortAddress,
                rating = null,
                isOpen = if (openStatusText.contains("Open")) true else if (openStatusText.contains("Closed")) false else null,
                websiteUri = websiteUrl,
                primaryType = surfaceType,
                distanceMiles = null
            )
            SavedCourtsManager.saveCourt(context, dummyCourt)
        }
        val newState = !currentState
        updateSaveButtonUi(newState)

        val msg = if (newState) "$courtName saved to your list" else "Removed from saved courts"
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun updateSaveButtonUi(saved: Boolean) {
        if (saved) {
            tvSavedButton.text = "Saved"
            tvSavedButton.setIconResource(R.drawable.ic_bookmark)
            tvSavedButton.setBackgroundColor(Color.parseColor("#272727"))
            tvSavedButton.setTextColor(Color.parseColor("#FFFFFF"))
            tvSavedButton.iconTint = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
            tvSavedButton.strokeWidth = 0
        } else {
            tvSavedButton.text = "Save Court"
            tvSavedButton.setIconResource(R.drawable.ic_bookmark)
            tvSavedButton.setBackgroundColor(Color.parseColor("#FFFFFF"))
            tvSavedButton.setTextColor(Color.parseColor("#272727"))
            tvSavedButton.iconTint = ColorStateList.valueOf(Color.parseColor("#272727"))
            tvSavedButton.strokeColor = ColorStateList.valueOf(Color.parseColor("#D4D4D4"))
            tvSavedButton.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
    }

    private fun shareCourtDetails() {
        val shareBody = "Check out $courtName on Our Court!\n$formattedAddress\nhttps://maps.google.com/?q=${Uri.encode(courtName)}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, courtName)
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }
        startActivity(Intent.createChooser(intent, "Share Court"))
    }

    private fun loadCommunitySignups() {
        if (!isAdded) return
        containerSignups.removeAllViews()

        val todayFormatted = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())
        
        // Retrieve check-ins from CheckInDataStore for this court
        val checkIns = CheckInDataStore.getCheckIns(requireContext(), placeId)
            .ifEmpty { CheckInDataStore.getCheckIns(requireContext(), courtName) }

        val count = checkIns.size
        tvSignupSubtitle.text = "Today, $todayFormatted · $count players signed up"

        if (checkIns.isEmpty()) {
            tvEmptySignups.visibility = View.VISIBLE
            containerSignups.visibility = View.GONE
        } else {
            tvEmptySignups.visibility = View.GONE
            containerSignups.visibility = View.VISIBLE

            val inflater = LayoutInflater.from(requireContext())
            for (req in checkIns) {
                val row = inflater.inflate(R.layout.item_player_signup, containerSignups, false)
                val tvAvatar = row.findViewById<TextView>(R.id.tv_player_avatar)
                val tvName = row.findViewById<TextView>(R.id.tv_player_name)
                val tvSkill = row.findViewById<TextView>(R.id.tv_player_skill)
                val tvTime = row.findViewById<TextView>(R.id.tv_player_time)

                tvAvatar.text = req.avatarInitials
                tvName.text = req.userName
                tvSkill.text = "${req.userSkill} · ${req.playReason}"
                tvTime.text = "${req.timeOfDay} (${req.dateSelection})"

                containerSignups.addView(row)
            }
        }
    }
}
