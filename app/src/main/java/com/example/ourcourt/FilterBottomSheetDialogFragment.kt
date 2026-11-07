package com.example.ourcourt

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var distanceMiles = 10
    private var selectedRating = 4

    private lateinit var starButtons: List<Button>
    private lateinit var tvRatingValue: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false)

        val btnClose = view.findViewById<ImageButton>(R.id.btn_close)
        val btnApply = view.findViewById<Button>(R.id.btn_apply_filters)
        val tvDistanceValue = view.findViewById<TextView>(R.id.tv_distance_value)
        val seekBarDistance = view.findViewById<SeekBar>(R.id.seekbar_distance)
        tvRatingValue = view.findViewById<TextView>(R.id.tv_rating_value)

        starButtons = listOf(
            view.findViewById(R.id.btn_star_1),
            view.findViewById(R.id.btn_star_2),
            view.findViewById(R.id.btn_star_3),
            view.findViewById(R.id.btn_star_4),
            view.findViewById(R.id.btn_star_5)
        )

        seekBarDistance.progress = distanceMiles - 1
        tvDistanceValue.text = "$distanceMiles miles"

        seekBarDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                distanceMiles = progress + 1
                tvDistanceValue.text = "$distanceMiles miles"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        starButtons.forEachIndexed { index, button ->
            val rating = index + 1
            button.setOnClickListener {
                selectedRating = rating
                updateStarSelection(selectedRating)
            }
        }

        updateStarSelection(selectedRating)

        btnClose.setOnClickListener { dismiss() }
        btnApply.setOnClickListener { dismiss() }

        return view
    }

    private fun updateStarSelection(rating: Int) {
        tvRatingValue.text = "$rating stars & up"
        starButtons.forEachIndexed { index, button ->
            val starLevel = index + 1
            if (starLevel == rating) {
                button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#272727"))
                button.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                button.setTextColor(Color.parseColor("#272727"))
            }
        }
    }
}
