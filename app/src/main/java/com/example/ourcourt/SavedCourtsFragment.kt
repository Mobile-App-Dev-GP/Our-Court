package com.example.ourcourt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ourcourt.data.model.TennisCourtPlace

class SavedCourtsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvSummary: TextView
    private lateinit var layoutEmptyState: View
    private lateinit var adapter: SavedCourtsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved_courts, container, false)

        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)
        tvSummary = view.findViewById(R.id.tv_summary)
        recyclerView = view.findViewById(R.id.recycler_saved_courts)
        layoutEmptyState = view.findViewById(R.id.layout_empty_state)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        adapter = SavedCourtsAdapter(
            items = emptyList(),
            onItemClick = { item ->
                val place = TennisCourtPlace(
                    placeId = item.placeId,
                    name = item.courtName,
                    formattedAddress = item.formattedAddress,
                    rating = null,
                    isOpen = item.isOpen,
                    websiteUri = item.websiteUri,
                    primaryType = item.surface ?: "Tennis court",
                    distanceMiles = null
                )
                val fragment = CourtDetailsFragment.newInstance(place)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack("CourtDetails")
                    .commit()
            },
            onRemoveClick = { item ->
                SavedCourtsManager.removeCourt(requireContext(), item.placeId)
                Toast.makeText(requireContext(), "${item.courtName} removed from saved courts", Toast.LENGTH_SHORT).show()
                refreshData()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        refreshData()

        return view
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        val savedCourts = SavedCourtsManager.getSavedCourts(requireContext())
        val count = savedCourts.size

        tvSummary.text = "$count ${if (count == 1) "court" else "courts"} saved · Bookmarked for quick access."

        if (savedCourts.isEmpty()) {
            recyclerView.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
            adapter.updateData(savedCourts)
        }
    }
}

class SavedCourtsAdapter(
    private var items: List<SavedCourtItem>,
    private val onItemClick: (SavedCourtItem) -> Unit,
    private val onRemoveClick: (SavedCourtItem) -> Unit
) : RecyclerView.Adapter<SavedCourtsAdapter.SavedViewHolder>() {

    class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_court_name)
        val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        val tvDetails: TextView = itemView.findViewById(R.id.tv_court_details)
        val tvSurface: TextView = itemView.findViewById(R.id.tv_surface_tag)
        val tvOpenStatus: TextView = itemView.findViewById(R.id.tv_open_status)
        val ivThumb: ImageView = itemView.findViewById(R.id.iv_court_thumb)
    }

    fun updateData(newItems: List<SavedCourtItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_court_card, parent, false)
        return SavedViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.courtName
        holder.tvRating.text = item.rating
        holder.tvDetails.text = "${item.distance} · ${item.formattedAddress}"
        holder.tvSurface.text = item.surface ?: "Tennis court"

        val openText = when (item.isOpen) {
            true -> "Open"
            false -> "Closed"
            null -> "Available"
        }
        holder.tvOpenStatus.text = openText
        holder.ivThumb.setImageResource(R.drawable.ic_launcher_background)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
