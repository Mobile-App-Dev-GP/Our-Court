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
        recyclerView.adapter = CourtAdapter(courtList)

        updateState()

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
    val isOpen: Boolean
)

class CourtAdapter(private val courts: List<CourtItem>) : RecyclerView.Adapter<CourtAdapter.CourtViewHolder>() {

    class CourtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_court_card, parent, false)
        return CourtViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourtViewHolder, position: Int) {
        // Bind dynamic court data when available
    }

    override fun getItemCount(): Int = courts.size
}
