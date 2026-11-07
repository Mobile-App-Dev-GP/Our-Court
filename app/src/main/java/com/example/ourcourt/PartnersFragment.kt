package com.example.ourcourt

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class PartnersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var tvEmptyState: TextView
    private lateinit var cardLocationDropdown: MaterialCardView
    private lateinit var tvSelectedLocation: TextView
    private lateinit var fabCreatePost: FloatingActionButton

    private var currentSelectedLocation = "San Francisco"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_partners, container, false)
        recyclerView = view.findViewById(R.id.recycler_partners)
        tabLayout = view.findViewById(R.id.tab_layout_partners)
        tvEmptyState = view.findViewById(R.id.tv_empty_state)
        cardLocationDropdown = view.findViewById(R.id.card_location_dropdown)
        tvSelectedLocation = view.findViewById(R.id.tv_selected_location)
        fabCreatePost = view.findViewById(R.id.fab_create_post)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        updateState(0)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                updateState(position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        cardLocationDropdown.setOnClickListener {
            showLocationPopup()
        }

        fabCreatePost.setOnClickListener {
            val intent = Intent(requireContext(), CreatePostActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        updateState(tabLayout.selectedTabPosition)
    }

    private fun showLocationPopup() {
        val popupView = layoutInflater.inflate(R.layout.popup_community_location, null)
        val popupWindow = PopupWindow(
            popupView,
            cardLocationDropdown.width,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val layoutLocalBoard = popupView.findViewById<View>(R.id.layout_local_board)
        val etZipInput = popupView.findViewById<EditText>(R.id.et_zip_input)
        val btnApplyZip = popupView.findViewById<Button>(R.id.btn_apply_zip)
        val tvZipError = popupView.findViewById<TextView>(R.id.tv_zip_error)

        layoutLocalBoard.setOnClickListener {
            currentSelectedLocation = "San Francisco"
            tvSelectedLocation.text = currentSelectedLocation
            popupWindow.dismiss()
            updateState(tabLayout.selectedTabPosition)
        }

        btnApplyZip.setOnClickListener {
            val zip = etZipInput.text.toString().trim()
            if (zip.length != 5 || !zip.all { it.isDigit() }) {
                tvZipError.text = "Enter a valid 5-digit ZIP code."
                tvZipError.visibility = View.VISIBLE
            } else {
                tvZipError.visibility = View.GONE
                currentSelectedLocation = "ZIP $zip"
                tvSelectedLocation.text = currentSelectedLocation
                popupWindow.dismiss()
                updateState(tabLayout.selectedTabPosition)
            }
        }

        popupWindow.showAsDropDown(cardLocationDropdown, 0, 4)
    }

    private fun updateState(tabPosition: Int) {
        val posts = CommunityDataStore.allPosts
        val requests = CommunityDataStore.partnerRequests

        if (tabPosition == 0) {
            recyclerView.setBackgroundColor(Color.parseColor("#F7F7F7"))
            recyclerView.adapter = CombinedCommunityAdapter(posts, requests)
            val isEmpty = posts.isEmpty() && requests.isEmpty()
            tvEmptyState.text = "No posts in this area yet"
            tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
            recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        } else {
            recyclerView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            recyclerView.adapter = PartnerRequestAdapter(requests)
            tvEmptyState.text = "No partner requests in this area yet"
            tvEmptyState.visibility = if (requests.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (requests.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}

data class PostItem(
    val initials: String,
    val authorName: String,
    val skillBadge: String,
    val timestampCategory: String,
    val content: String,
    val courtName: String,
    val commentsCount: String
)

data class PartnerRequestItem(
    val initials: String,
    val authorName: String,
    val timestamp: String,
    val skillBadge: String,
    val content: String,
    val court: String,
    val time: String,
    val playType: String
)

class CombinedCommunityAdapter(
    private val posts: List<PostItem>,
    private val requests: List<PartnerRequestItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_POST = 0
        private const val TYPE_REQUEST = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < posts.size) TYPE_POST else TYPE_REQUEST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_POST) {
            PostViewHolder(inflater.inflate(R.layout.item_community_post, parent, false))
        } else {
            PartnerRequestViewHolder(inflater.inflate(R.layout.item_partner_request, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            val item = posts[position]
            holder.initials.text = item.initials
            holder.authorName.text = item.authorName
            holder.skillBadge.text = item.skillBadge
            holder.timestampCategory.text = item.timestampCategory
            holder.content.text = item.content
            holder.courtName.text = item.courtName
            holder.commentsCount.text = item.commentsCount
        } else if (holder is PartnerRequestViewHolder) {
            val item = requests[position - posts.size]
            holder.initials.text = item.initials
            holder.authorName.text = item.authorName
            holder.timestamp.text = item.timestamp
            holder.skillBadge.text = item.skillBadge
            holder.content.text = item.content
            holder.court.text = item.court
            holder.time.text = item.time
            holder.playType.text = item.playType
        }
    }

    override fun getItemCount(): Int = posts.size + requests.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val initials: TextView = itemView.findViewById(R.id.tv_avatar_initials)
        val authorName: TextView = itemView.findViewById(R.id.tv_author_name)
        val skillBadge: TextView = itemView.findViewById(R.id.tv_skill_badge)
        val timestampCategory: TextView = itemView.findViewById(R.id.tv_timestamp_category)
        val content: TextView = itemView.findViewById(R.id.tv_post_content)
        val courtName: TextView = itemView.findViewById(R.id.tv_court_name_ref)
        val commentsCount: TextView = itemView.findViewById(R.id.tv_comments_count)
    }

    class PartnerRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val initials: TextView = itemView.findViewById(R.id.tv_avatar_initials)
        val authorName: TextView = itemView.findViewById(R.id.tv_author_name)
        val timestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
        val skillBadge: TextView = itemView.findViewById(R.id.tv_skill_badge)
        val content: TextView = itemView.findViewById(R.id.tv_post_content)
        val court: TextView = itemView.findViewById(R.id.tv_req_court)
        val time: TextView = itemView.findViewById(R.id.tv_req_time)
        val playType: TextView = itemView.findViewById(R.id.tv_req_playtype)
    }
}

class PartnerRequestAdapter(
    private val requests: List<PartnerRequestItem>
) : RecyclerView.Adapter<PartnerRequestAdapter.PartnerRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_partner_request, parent, false)
        return PartnerRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartnerRequestViewHolder, position: Int) {
        val item = requests[position]
        holder.initials.text = item.initials
        holder.authorName.text = item.authorName
        holder.timestamp.text = item.timestamp
        holder.skillBadge.text = item.skillBadge
        holder.content.text = item.content
        holder.court.text = item.court
        holder.time.text = item.time
        holder.playType.text = item.playType
    }

    override fun getItemCount(): Int = requests.size

    class PartnerRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val initials: TextView = itemView.findViewById(R.id.tv_avatar_initials)
        val authorName: TextView = itemView.findViewById(R.id.tv_author_name)
        val timestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
        val skillBadge: TextView = itemView.findViewById(R.id.tv_skill_badge)
        val content: TextView = itemView.findViewById(R.id.tv_post_content)
        val court: TextView = itemView.findViewById(R.id.tv_req_court)
        val time: TextView = itemView.findViewById(R.id.tv_req_time)
        val playType: TextView = itemView.findViewById(R.id.tv_req_playtype)
    }
}
