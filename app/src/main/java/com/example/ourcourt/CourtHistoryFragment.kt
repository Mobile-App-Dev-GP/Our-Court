package com.example.ourcourt

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class CourtHistoryFragment : Fragment() {

    private enum class FilterType {
        ALL_TIME, THIS_MONTH, RECENT
    }

    private var currentFilter = FilterType.ALL_TIME

    private lateinit var btnFilterAll: Button
    private lateinit var btnFilterMonth: Button
    private lateinit var btnFilterRecent: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutEmptyState: View
    private lateinit var tvEmptyTitle: TextView
    private lateinit var tvEmptySubtitle: TextView
    private lateinit var tvSummary: TextView
    private lateinit var tvFooter: TextView

    private lateinit var visitAdapter: CourtHistoryAdapter

    companion object {
        private const val KEY_FILTER = "key_filter"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_court_history, container, false)

        if (savedInstanceState != null) {
            val filterOrdinal = savedInstanceState.getInt(KEY_FILTER, FilterType.ALL_TIME.ordinal)
            currentFilter = FilterType.values()[filterOrdinal]
        }

        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)
        btnFilterAll = view.findViewById(R.id.btn_filter_all)
        btnFilterMonth = view.findViewById(R.id.btn_filter_month)
        btnFilterRecent = view.findViewById(R.id.btn_filter_recent)
        recyclerView = view.findViewById(R.id.recycler_visits)
        layoutEmptyState = view.findViewById(R.id.layout_empty_state)
        tvEmptyTitle = view.findViewById(R.id.tv_empty_title)
        tvEmptySubtitle = view.findViewById(R.id.tv_empty_subtitle)
        tvSummary = view.findViewById(R.id.tv_summary)
        tvFooter = view.findViewById(R.id.tv_footer)

        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        btnFilterAll.setOnClickListener {
            currentFilter = FilterType.ALL_TIME
            updateFilterUI()
        }

        btnFilterMonth.setOnClickListener {
            currentFilter = FilterType.THIS_MONTH
            updateFilterUI()
        }

        btnFilterRecent.setOnClickListener {
            currentFilter = FilterType.RECENT
            updateFilterUI()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        visitAdapter = CourtHistoryAdapter(emptyList()) { item ->
            showRevisitDialog(item)
        }
        recyclerView.adapter = visitAdapter

        refreshData()

        return view
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_FILTER, currentFilter.ordinal)
    }

    private fun refreshData() {
        val allHistory = CourtHistoryManager.getHistory(requireContext())
        val totalCount = allHistory.size
        val courtWord = if (totalCount == 1) "court viewed" else "courts viewed"
        tvSummary.text = "$totalCount $courtWord · Every session counts."

        updateFilterUI(allHistory)
    }

    private fun updateFilterUI(allHistory: List<CourtHistoryItem> = CourtHistoryManager.getHistory(requireContext())) {
        val darkColor = Color.parseColor("#272727")
        val whiteColor = Color.parseColor("#FFFFFF")

        fun setButtonStyle(button: Button, isSelected: Boolean) {
            if (isSelected) {
                button.backgroundTintList = ColorStateList.valueOf(darkColor)
                button.setTextColor(whiteColor)
            } else {
                button.backgroundTintList = ColorStateList.valueOf(whiteColor)
                button.setTextColor(darkColor)
            }
        }

        setButtonStyle(btnFilterAll, currentFilter == FilterType.ALL_TIME)
        setButtonStyle(btnFilterMonth, currentFilter == FilterType.THIS_MONTH)
        setButtonStyle(btnFilterRecent, currentFilter == FilterType.RECENT)

        val filteredList = when (currentFilter) {
            FilterType.ALL_TIME -> allHistory
            FilterType.THIS_MONTH -> {
                val currentCal = Calendar.getInstance()
                val curYear = currentCal.get(Calendar.YEAR)
                val curMonth = currentCal.get(Calendar.MONTH)
                allHistory.filter { it.year == curYear && it.month == curMonth }
            }
            FilterType.RECENT -> allHistory.take(3)
        }

        visitAdapter.updateData(filteredList)

        if (filteredList.isEmpty()) {
            recyclerView.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
            tvEmptyTitle.text = "No court history yet"
            if (currentFilter == FilterType.THIS_MONTH) {
                tvEmptySubtitle.text = "No courts viewed this month."
            } else {
                tvEmptySubtitle.text = "Courts you view will appear here."
            }
            tvFooter.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
            tvFooter.visibility = View.VISIBLE

            val count = filteredList.size
            tvFooter.text = when (currentFilter) {
                FilterType.ALL_TIME -> {
                    val w = if (count == 1) "court" else "courts"
                    "Showing all $count $w."
                }
                FilterType.THIS_MONTH -> {
                    val w = if (count == 1) "court" else "courts"
                    "Showing $count $w this month."
                }
                FilterType.RECENT -> {
                    val w = if (count == 1) "court" else "courts"
                    "Showing your $count most recent $w."
                }
            }
        }
    }

    private fun showRevisitDialog(item: CourtHistoryItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Court Details")
            .setMessage("Court: ${item.courtName}\n\nCourt details and booking are not available yet.")
            .setPositiveButton("OK", null)
            .show()
    }
}

class CourtHistoryAdapter(
    private var items: List<CourtHistoryItem>,
    private val onRevisitClick: (CourtHistoryItem) -> Unit
) : RecyclerView.Adapter<CourtHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourtName: TextView = itemView.findViewById(R.id.tv_visit_court_name)
        val tvDate: TextView = itemView.findViewById(R.id.tv_visit_date)
        val btnRevisit: View = itemView.findViewById(R.id.btn_revisit)
    }

    fun updateData(newItems: List<CourtHistoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_court_visit, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]
        holder.tvCourtName.text = item.courtName
        holder.tvDate.text = item.dateString

        holder.btnRevisit.setOnClickListener {
            onRevisitClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
