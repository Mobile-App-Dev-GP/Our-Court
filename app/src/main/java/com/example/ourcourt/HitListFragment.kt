package com.example.ourcourt

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HitListFragment : Fragment() {

    private lateinit var recyclerHitList: RecyclerView
    private lateinit var layoutCountRow: View
    private lateinit var tvCourtCount: TextView
    private lateinit var layoutEmptyState: View
    private lateinit var btnBack: FrameLayout

    private lateinit var btnTogglePublic: LinearLayout
    private lateinit var btnTogglePrivate: LinearLayout
    private lateinit var ivPublicIcon: ImageView
    private lateinit var tvTogglePublic: TextView
    private lateinit var ivPrivateIcon: ImageView
    private lateinit var tvTogglePrivate: TextView

    private var adapter: HitListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hit_list, container, false)

        btnBack = view.findViewById(R.id.btn_back)
        recyclerHitList = view.findViewById(R.id.recycler_hit_list)
        layoutCountRow = view.findViewById(R.id.layout_count_row)
        tvCourtCount = view.findViewById(R.id.tv_court_count)
        layoutEmptyState = view.findViewById(R.id.layout_empty_state)

        btnTogglePublic = view.findViewById(R.id.btn_toggle_public)
        btnTogglePrivate = view.findViewById(R.id.btn_toggle_private)
        ivPublicIcon = view.findViewById(R.id.iv_public_icon)
        tvTogglePublic = view.findViewById(R.id.tv_toggle_public)
        ivPrivateIcon = view.findViewById(R.id.iv_private_icon)
        tvTogglePrivate = view.findViewById(R.id.tv_toggle_private)

        recyclerHitList.layoutManager = LinearLayoutManager(requireContext())

        btnBack.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                (activity as? MainActivity)?.selectTab(R.id.nav_profile)
            }
        }

        btnTogglePublic.setOnClickListener {
            HitListDataStore.updateDefaultVisibility(true)
            updateToggleState(isPublic = true)
            updateUI()
        }

        btnTogglePrivate.setOnClickListener {
            HitListDataStore.updateDefaultVisibility(false)
            updateToggleState(isPublic = false)
            updateUI()
        }

        updateToggleState(HitListDataStore.isPublicDefault)
        updateUI()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateToggleState(HitListDataStore.isPublicDefault)
        updateUI()
    }

    private fun updateToggleState(isPublic: Boolean) {
        if (isPublic) {
            btnTogglePublic.setBackgroundResource(R.drawable.bg_circle_avatar)
            tvTogglePublic.setTextColor(Color.parseColor("#242424"))
            ivPublicIcon.setColorFilter(Color.parseColor("#242424"))

            btnTogglePrivate.setBackgroundColor(Color.TRANSPARENT)
            tvTogglePrivate.setTextColor(Color.parseColor("#6B6B6B"))
            ivPrivateIcon.setColorFilter(Color.parseColor("#6B6B6B"))
        } else {
            btnTogglePrivate.setBackgroundResource(R.drawable.bg_circle_avatar)
            tvTogglePrivate.setTextColor(Color.parseColor("#242424"))
            ivPrivateIcon.setColorFilter(Color.parseColor("#242424"))

            btnTogglePublic.setBackgroundColor(Color.TRANSPARENT)
            tvTogglePublic.setTextColor(Color.parseColor("#6B6B6B"))
            ivPublicIcon.setColorFilter(Color.parseColor("#6B6B6B"))
        }
    }

    private fun updateUI() {
        val visits = HitListDataStore.getVisitedCourts()
        val count = visits.size

        if (count == 0) {
            layoutCountRow.visibility = View.GONE
            recyclerHitList.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        } else {
            layoutCountRow.visibility = View.VISIBLE
            recyclerHitList.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE

            tvCourtCount.text = if (count == 1) {
                getString(R.string.court_visited_single)
            } else {
                getString(R.string.courts_visited_plural, count)
            }

            if (adapter == null) {
                adapter = HitListAdapter(visits)
                recyclerHitList.adapter = adapter
            } else {
                adapter?.updateData(visits)
            }
        }
    }
}

class HitListAdapter(
    private var visits: List<VisitedCourt>
) : RecyclerView.Adapter<HitListAdapter.HitListViewHolder>() {

    fun updateData(newVisits: List<VisitedCourt>) {
        this.visits = newVisits
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HitListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hit_list_court, parent, false)
        return HitListViewHolder(view)
    }

    override fun onBindViewHolder(holder: HitListViewHolder, position: Int) {
        val item = visits[position]
        holder.tvCourtName.text = item.courtName
        holder.tvCourtLocation.text = item.location
        holder.tvLastVisited.text = holder.itemView.context.getString(R.string.last_visited_format, item.lastVisitedDate)
        holder.tvVisibilityBadge.text = if (item.isPublic) "Public" else "Private"
    }

    override fun getItemCount(): Int = visits.size

    class HitListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourtName: TextView = itemView.findViewById(R.id.tv_court_name)
        val tvCourtLocation: TextView = itemView.findViewById(R.id.tv_court_location)
        val tvLastVisited: TextView = itemView.findViewById(R.id.tv_last_visited)
        val tvVisibilityBadge: TextView = itemView.findViewById(R.id.tv_visibility_badge)
    }
}
