package com.example.ourcourt

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class LeaveReviewFragment : Fragment() {

    private var courtItem: CourtItem? = null
    private var selectedRating = 0

    private lateinit var starButtons: List<TextView>
    private lateinit var tvRatingHint: TextView
    private lateinit var etReviewText: EditText
    private lateinit var tvCharCount: TextView
    private lateinit var btnSubmitReview: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courtItem = arguments?.getSerializable("court_item") as? CourtItem
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leave_review, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back)
        val tvCourtName = view.findViewById<TextView>(R.id.tv_court_name)
        val tvCourtAddress = view.findViewById<TextView>(R.id.tv_court_address)
        val tvCourtRating = view.findViewById<TextView>(R.id.tv_court_rating)

        tvRatingHint = view.findViewById(R.id.tv_rating_hint)
        etReviewText = view.findViewById(R.id.et_review_text)
        tvCharCount = view.findViewById<TextView>(R.id.tv_char_count)
        btnSubmitReview = view.findViewById<MaterialButton>(R.id.btn_submit_review)

        starButtons = listOf(
            view.findViewById(R.id.tv_star_1),
            view.findViewById(R.id.tv_star_2),
            view.findViewById(R.id.tv_star_3),
            view.findViewById(R.id.tv_star_4),
            view.findViewById(R.id.tv_star_5)
        )

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        courtItem?.let { court ->
            tvCourtName.text = court.name
            tvCourtAddress.text = court.address ?: court.details
            tvCourtRating.text = "★ ${court.rating}"
        }

        starButtons.forEachIndexed { index, starView ->
            val rating = index + 1
            starView.setOnClickListener {
                selectedRating = rating
                updateStars(selectedRating)
            }
        }

        etReviewText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                tvCharCount.text = "$length/500"
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnSubmitReview.setOnClickListener {
            if (selectedRating == 0) {
                Toast.makeText(requireContext(), "Please select a star rating before submitting.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reviewText = etReviewText.text.toString().trim()
            val courtId = courtItem?.id ?: "default_court"

            // Save review using team's shared review storage (ReviewDataStore)
            ReviewDataStore.addReview(courtId, selectedRating, reviewText)

            Toast.makeText(requireContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show()

            // Explanation regarding backend connection:
            // "Reviews are stored in ReviewDataStore (shared in-memory review storage). A cloud backend connection (such as Firebase Firestore) is required for remote cross-device persistence."

            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun updateStars(rating: Int) {
        starButtons.forEachIndexed { index, starView ->
            if (index < rating) {
                starView.text = "★"
                starView.setTextColor(android.graphics.Color.parseColor("#272727"))
            } else {
                starView.text = "☆"
                starView.setTextColor(android.graphics.Color.parseColor("#8E8E93"))
            }
        }
        tvRatingHint.text = "Rated $rating out of 5 stars"
    }
}
