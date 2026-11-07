package com.example.ourcourt

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreatePostActivity : AppCompatActivity() {

    private var currentCalendar: Calendar? = null
    private var selectedHour = 5
    private var selectedMinute = 5
    private var selectedAmPm = "PM"

    private var selectedMinSkill = "3.5"
    private var selectedMaxSkill = "4.5"

    private var datePopup: PopupWindow? = null
    private var timePopup: PopupWindow? = null
    private var skillPopup: PopupWindow? = null

    private val skillOptions = listOf("2.5", "3.0", "3.5", "4.0", "4.5", "5.0")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        val btnCancel = findViewById<ImageButton>(R.id.btn_cancel)
        val switchPartner = findViewById<SwitchCompat>(R.id.switch_partner_toggle)
        val layoutPartnerDetails = findViewById<View>(R.id.layout_partner_details)
        val tvPartnerExplanation = findViewById<View>(R.id.tv_partner_explanation)
        val btnPost = findViewById<View>(R.id.btn_post)
        val tvSelectedDate = findViewById<TextView>(R.id.tv_selected_date)
        val tvSelectedTime = findViewById<TextView>(R.id.tv_selected_time)
        val tvMinSkill = findViewById<TextView>(R.id.tv_min_skill)
        val tvMaxSkill = findViewById<TextView>(R.id.tv_max_skill)
        val dateTimeRow = tvSelectedDate.parent as View

        btnCancel.setOnClickListener { finish() }

        switchPartner.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                layoutPartnerDetails.visibility = View.VISIBLE
                tvPartnerExplanation.visibility = View.VISIBLE
            } else {
                layoutPartnerDetails.visibility = View.GONE
                tvPartnerExplanation.visibility = View.GONE
            }
        }

        tvSelectedDate.text = "Select date"
        tvSelectedTime.text = "Select time"
        updateSkillDisplay()

        tvSelectedDate.setOnClickListener {
            dismissAllPopups()
            showCalendarPopup(dateTimeRow)
        }

        tvSelectedTime.setOnClickListener {
            dismissAllPopups()
            showTimePopup(dateTimeRow)
        }

        tvMinSkill.setOnClickListener {
            dismissAllPopups()
            showSkillPopup(tvMinSkill, true)
        }

        tvMaxSkill.setOnClickListener {
            dismissAllPopups()
            showSkillPopup(tvMaxSkill, false)
        }

        btnPost.setOnClickListener {
            val content = findViewById<EditText>(R.id.et_post_content).text.toString().trim()
            val isPartner = switchPartner.isChecked

            if (content.isNotEmpty()) {
                if (isPartner) {
                    val dateStr = tvSelectedDate.text.toString()
                    val timeStr = tvSelectedTime.text.toString()
                    val timeDisplay = if (dateStr != "Select date" && timeStr != "Select time") "$dateStr, $timeStr" else "Today, 5:00 PM"
                    val request = PartnerRequestItem(
                        initials = "AR",
                        authorName = "Alex Rivera",
                        timestamp = "Just now",
                        skillBadge = "UTR 5.2",
                        content = content,
                        court = "Dolores Park Courts",
                        time = timeDisplay,
                        playType = "Casual Rally / Match"
                    )
                    CommunityDataStore.partnerRequests.add(0, request)
                } else {
                    val post = PostItem(
                        initials = "AR",
                        authorName = "Alex Rivera",
                        skillBadge = "UTR 5.2",
                        timestampCategory = "Just now · General",
                        content = content,
                        courtName = "Dolores Park Courts",
                        commentsCount = "0 comments"
                    )
                    CommunityDataStore.allPosts.add(0, post)
                }
            }
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun updateDateDisplay() {
        val tvSelectedDate = findViewById<TextView>(R.id.tv_selected_date)
        currentCalendar?.let {
            val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            tvSelectedDate.text = formatter.format(it.time)
        } ?: run {
            tvSelectedDate.text = "Select date"
        }
    }

    private fun updateTimeDisplay() {
        val tvSelectedTime = findViewById<TextView>(R.id.tv_selected_time)
        val minStr = String.format(Locale.getDefault(), "%02d", selectedMinute)
        tvSelectedTime.text = "$selectedHour:$minStr $selectedAmPm"
    }

    private fun updateSkillDisplay() {
        val tvMinSkill = findViewById<TextView>(R.id.tv_min_skill)
        val tvMaxSkill = findViewById<TextView>(R.id.tv_max_skill)
        tvMinSkill.text = "NTRP $selectedMinSkill"
        tvMaxSkill.text = "NTRP $selectedMaxSkill"
    }

    private fun showCalendarPopup(anchorView: View) {
        val popupView = layoutInflater.inflate(R.layout.panel_custom_calendar, null)
        val popup = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val tvMonthYear = popupView.findViewById<TextView>(R.id.tv_month_year)
        val containerWeeks = popupView.findViewById<ViewGroup>(R.id.container_calendar_weeks)
        val btnDone = popupView.findViewById<TextView>(R.id.btn_date_done)
        val btnCancel = popupView.findViewById<TextView>(R.id.btn_date_cancel)

        val tempCal = (currentCalendar ?: Calendar.getInstance()).clone() as Calendar
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonthYear.text = monthFormat.format(tempCal.time)

        var selectedDayNum = tempCal.get(Calendar.DAY_OF_MONTH)

        fun renderWeeks() {
            containerWeeks.removeAllViews()
            val year = tempCal.get(Calendar.YEAR)
            val month = tempCal.get(Calendar.MONTH)
            val firstDayOfWeek = Calendar.getInstance().apply { set(year, month, 1) }.get(Calendar.DAY_OF_WEEK) - 1
            val maxDays = Calendar.getInstance().apply { set(year, month, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)

            val daysList = mutableListOf<Int?>()
            repeat(firstDayOfWeek) { daysList.add(null) }
            for (d in 1..maxDays) { daysList.add(d) }
            while (daysList.size % 7 != 0) { daysList.add(null) }

            val weeks = daysList.chunked(7)
            val rowHeightPx = (36 * resources.displayMetrics.density).toInt()
            val circleSizePx = (32 * resources.displayMetrics.density).toInt()

            for (week in weeks) {
                val weekRow = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, rowHeightPx)
                    orientation = LinearLayout.HORIZONTAL
                    weightSum = 7f
                }
                for (day in week) {
                    val cellFrame = FrameLayout(this).apply {
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
                    }
                    if (day != null) {
                        val tv = TextView(this).apply {
                            layoutParams = FrameLayout.LayoutParams(circleSizePx, circleSizePx).apply {
                                gravity = Gravity.CENTER
                            }
                            gravity = Gravity.CENTER
                            text = day.toString()
                            textSize = 13f
                            if (day == selectedDayNum) {
                                setBackgroundResource(R.drawable.bg_selected_day)
                                setTextColor(Color.parseColor("#FFFFFF"))
                            } else {
                                background = null
                                setTextColor(Color.parseColor("#272727"))
                            }
                            setOnClickListener {
                                selectedDayNum = day
                                tempCal.set(Calendar.DAY_OF_MONTH, day)
                                tvMonthYear.text = monthFormat.format(tempCal.time)
                                renderWeeks()
                            }
                        }
                        cellFrame.addView(tv)
                    }
                    weekRow.addView(cellFrame)
                }
                containerWeeks.addView(weekRow)
            }
        }

        renderWeeks()

        btnDone.setOnClickListener {
            tempCal.set(Calendar.DAY_OF_MONTH, selectedDayNum)
            currentCalendar = tempCal
            updateDateDisplay()
            popup.dismiss()
        }

        btnCancel.setOnClickListener {
            popup.dismiss()
        }

        popup.setOnDismissListener {
            datePopup = null
        }

        anchorView.post {
            val location = IntArray(2)
            anchorView.getLocationOnScreen(location)
            val x = location[0]
            val y = location[1] - 328 - 24
            popup.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y)
            datePopup = popup
        }
    }

    private fun showTimePopup(anchorView: View) {
        val popupView = layoutInflater.inflate(R.layout.panel_custom_time, null)
        val popup = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val recyclerHour = popupView.findViewById<RecyclerView>(R.id.recycler_hour)
        val recyclerMinute = popupView.findViewById<RecyclerView>(R.id.recycler_minute)
        val recyclerAmpm = popupView.findViewById<RecyclerView>(R.id.recycler_ampm)
        val btnDone = popupView.findViewById<TextView>(R.id.btn_time_done)
        val btnCancel = popupView.findViewById<TextView>(R.id.btn_time_cancel)

        val hours = (1..12).map { it.toString() }
        val minutes = (0..59).map { String.format(Locale.getDefault(), "%02d", it) }
        val ampmList = listOf("AM", "PM")

        val hourSnap = LinearSnapHelper()
        val minuteSnap = LinearSnapHelper()
        val ampmSnap = LinearSnapHelper()

        recyclerHour.layoutManager = LinearLayoutManager(this)
        recyclerHour.adapter = TimeWheelAdapter(hours) {}
        hourSnap.attachToRecyclerView(recyclerHour)
        recyclerHour.scrollToPosition(selectedHour - 1)

        recyclerMinute.layoutManager = LinearLayoutManager(this)
        recyclerMinute.adapter = TimeWheelAdapter(minutes) {}
        minuteSnap.attachToRecyclerView(recyclerMinute)
        recyclerMinute.scrollToPosition(selectedMinute)

        recyclerAmpm.layoutManager = LinearLayoutManager(this)
        recyclerAmpm.adapter = TimeWheelAdapter(ampmList) {}
        ampmSnap.attachToRecyclerView(recyclerAmpm)
        recyclerAmpm.scrollToPosition(if (selectedAmPm == "AM") 0 else 1)

        recyclerHour.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val view = hourSnap.findSnapView(recyclerView.layoutManager)
                    view?.let {
                        val pos = recyclerView.getChildAdapterPosition(it)
                        if (pos != RecyclerView.NO_POSITION) {
                            selectedHour = hours[pos].toInt()
                        }
                    }
                }
            }
        })

        recyclerMinute.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val view = minuteSnap.findSnapView(recyclerView.layoutManager)
                    view?.let {
                        val pos = recyclerView.getChildAdapterPosition(it)
                        if (pos != RecyclerView.NO_POSITION) {
                            selectedMinute = minutes[pos].toInt()
                        }
                    }
                }
            }
        })

        recyclerAmpm.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val view = ampmSnap.findSnapView(recyclerView.layoutManager)
                    view?.let {
                        val pos = recyclerView.getChildAdapterPosition(it)
                        if (pos != RecyclerView.NO_POSITION) {
                            selectedAmPm = ampmList[pos]
                        }
                    }
                }
            }
        })

        btnDone.setOnClickListener {
            updateTimeDisplay()
            popup.dismiss()
        }

        btnCancel.setOnClickListener {
            popup.dismiss()
        }

        popup.setOnDismissListener {
            timePopup = null
        }

        anchorView.post {
            val location = IntArray(2)
            anchorView.getLocationOnScreen(location)
            val x = location[0]
            val y = location[1] - 274 - 24
            popup.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y)
            timePopup = popup
        }
    }

    private fun showSkillPopup(anchorView: View, isMinimum: Boolean) {
        val popupView = layoutInflater.inflate(R.layout.panel_custom_skill, null)
        val widthPx = (326 * resources.displayMetrics.density).toInt()
        val heightPx = (256 * resources.displayMetrics.density).toInt()
        val popup = PopupWindow(
            popupView,
            widthPx,
            heightPx,
            true
        )

        val recyclerView = popupView.findViewById<RecyclerView>(R.id.recycler_skills)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SkillAdapter(skillOptions, if (isMinimum) selectedMinSkill else selectedMaxSkill, selectedMinSkill, selectedMaxSkill, isMinimum) { chosen ->
            if (isMinimum) {
                if (chosen.toFloat() <= selectedMaxSkill.toFloat()) {
                    selectedMinSkill = chosen
                    updateSkillDisplay()
                    popup.dismiss()
                }
            } else {
                if (chosen.toFloat() >= selectedMinSkill.toFloat()) {
                    selectedMaxSkill = chosen
                    updateSkillDisplay()
                    popup.dismiss()
                }
            }
        }

        popup.setOnDismissListener {
            skillPopup = null
        }

        anchorView.post {
            val location = IntArray(2)
            anchorView.getLocationOnScreen(location)
            val x = location[0]
            val y = location[1] - heightPx - 16
            popup.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y)
            skillPopup = popup
        }
    }

    private fun dismissAllPopups() {
        datePopup?.dismiss()
        datePopup = null
        timePopup?.dismiss()
        timePopup = null
        skillPopup?.dismiss()
        skillPopup = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissAllPopups()
    }
}

class TimeWheelAdapter(
    private val items: List<String>,
    private val onItemSelected: (String) -> Unit
) : RecyclerView.Adapter<TimeWheelAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv: TextView = itemView as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val heightPx = (34 * parent.context.resources.displayMetrics.density).toInt()
        val tv = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx)
            gravity = Gravity.CENTER
            textSize = 18f
            setTextColor(Color.parseColor("#686868"))
        }
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tv.text = item
        holder.tv.setOnClickListener {
            onItemSelected(item)
        }
    }

    override fun getItemCount(): Int = items.size
}

class SkillAdapter(
    private val skills: List<String>,
    private val selectedSkill: String,
    private val minSkill: String,
    private val maxSkill: String,
    private val isMinimum: Boolean,
    private val onSelected: (String) -> Unit
) : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {

    class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rowLayout: View = itemView.findViewById(R.id.layout_skill_row)
        val prefix: TextView = itemView.findViewById(R.id.tv_skill_prefix)
        val value: TextView = itemView.findViewById(R.id.tv_skill_value)
        val check: ImageView = itemView.findViewById(R.id.iv_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_skill_row, parent, false)
        return SkillViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = skills[position]
        holder.prefix.text = "NTRP"
        holder.value.text = skill

        val isSelected = (skill == selectedSkill)
        val isDisabled = if (isMinimum) {
            skill.toFloat() > maxSkill.toFloat()
        } else {
            skill.toFloat() < minSkill.toFloat()
        }

        if (isDisabled) {
            holder.rowLayout.setBackgroundColor(Color.TRANSPARENT)
            holder.rowLayout.isEnabled = false
            holder.prefix.setTextColor(Color.parseColor("#BDBDBD"))
            holder.value.setTextColor(Color.parseColor("#BDBDBD"))
            holder.check.visibility = View.GONE
            holder.rowLayout.setOnClickListener(null)
        } else {
            holder.prefix.setTextColor(Color.parseColor("#686868"))
            holder.rowLayout.isEnabled = true
            if (isSelected) {
                holder.rowLayout.setBackgroundColor(Color.parseColor("#F5F5F5"))
                holder.check.visibility = View.VISIBLE
                holder.value.setTextColor(Color.parseColor("#272727"))
                holder.value.textSize = 14f
                holder.value.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                holder.rowLayout.setBackgroundColor(Color.TRANSPARENT)
                holder.check.visibility = View.GONE
                holder.value.setTextColor(Color.parseColor("#272727"))
                holder.value.textSize = 14f
                holder.value.setTypeface(null, android.graphics.Typeface.NORMAL)
            }

            holder.rowLayout.setOnClickListener {
                onSelected(skill)
            }
        }
    }

    override fun getItemCount(): Int = skills.size
}
