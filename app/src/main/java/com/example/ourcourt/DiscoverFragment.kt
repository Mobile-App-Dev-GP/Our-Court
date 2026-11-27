package com.example.ourcourt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DiscoverFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvCourtsSub: TextView
    private lateinit var layoutEmptyState: View

    // Data-driven, initially empty list (no hardcoded sample records)
    private val courtList = emptyList<CourtItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_discover, container, false)
        recyclerView = view.findViewById(R.id.recycler_courts)
        tvCourtsSub = view.findViewById(R.id.tv_courts_sub)
        layoutEmptyState = view.findViewById(R.id.layout_empty_state)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = CourtAdapter(courtList) { court ->
            val fragment = CourtDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("court_item", court)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("CourtDetails")
                .commit()
        }

        updateState()

        layoutEmptyState.setOnClickListener {
            val sampleCourt = CourtItem(
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
            val fragment = CourtDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("court_item", sampleCourt)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("CourtDetails")
                .commit()
        }

        val btnFilter = view.findViewById<View>(R.id.btn_filter)
        btnFilter.setOnClickListener {
            val sheet = FilterBottomSheetDialogFragment()
            sheet.show(parentFragmentManager, "FilterBottomSheet")
        }

        return view
    }

    private fun updateState() {
        val count = courtList.size
        tvCourtsSub.text = "$count courts · Sorted by distance"
        if (courtList.isEmpty()) {
            recyclerView.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
        }
    }
}

data class CourtItem(
    val id: String,
    val name: String,
    val rating: String,
    val distance: String,
    val details: String,
    val surface: String,
    val isOpen: Boolean,
    val address: String? = null,
    val hours: String? = null,
    val lighting: String? = null,
    val courtCount: String? = null,
    val photoUrl: String? = null,
    val officialWebsite: String? = null
) : java.io.Serializable

class CourtAdapter(
    private val courts: List<CourtItem>,
    private val onCourtClick: (CourtItem) -> Unit
) : RecyclerView.Adapter<CourtAdapter.CourtViewHolder>() {

    class CourtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourtName: TextView = itemView.findViewById(R.id.tv_court_name)
        val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        val tvCourtDetails: TextView = itemView.findViewById(R.id.tv_court_details)
        val tvSurfaceTag: TextView = itemView.findViewById(R.id.tv_surface_tag)
        val tvOpenStatus: TextView = itemView.findViewById(R.id.tv_open_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_court_card, parent, false)
        return CourtViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourtViewHolder, position: Int) {
        val court = courts[position]
        holder.tvCourtName.text = court.name
        holder.tvRating.text = court.rating
        holder.tvCourtDetails.text = "${court.distance} · ${court.address ?: court.details}"
        holder.tvSurfaceTag.text = court.surface
        holder.tvOpenStatus.text = if (court.isOpen) "Open now" else "Closed"

        holder.itemView.setOnClickListener {
            onCourtClick(court)
        }
    }

    override fun getItemCount(): Int = courts.size
}
