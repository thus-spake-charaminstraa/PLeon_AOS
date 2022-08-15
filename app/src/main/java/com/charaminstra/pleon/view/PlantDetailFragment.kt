package com.charaminstra.pleon.view

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.charaminstra.pleon.R
import com.charaminstra.pleon.adapter.ActionObject
import com.charaminstra.pleon.adapter.FeedAdapter
import com.charaminstra.pleon.calendar.MonthViewContainer
import com.charaminstra.pleon.databinding.CalendarDayLayoutBinding
import com.charaminstra.pleon.databinding.FragmentPlantDetailBinding
import com.charaminstra.pleon.foundation.model.ScheduleDataObject
import com.charaminstra.pleon.plant_register.PlantIdViewModel
import com.charaminstra.pleon.viewmodel.PlantDetailViewModel
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.notify
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*


@AndroidEntryPoint
class PlantDetailFragment : Fragment() {
    private val TAG = javaClass.name
    private val today = LocalDate.now()
    private val viewModel: PlantIdViewModel by viewModels()
    private val plantDetailViewModel: PlantDetailViewModel by viewModels()
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var binding : FragmentPlantDetailBinding
    lateinit var plantId : String
    private var selectedDate: LocalDate? = null
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var navController: NavController
    private var scheduleList : List<ScheduleDataObject> = listOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dateFormat = SimpleDateFormat(resources.getString(com.charaminstra.pleon.common_ui.R.string.date_format))
        navController = this.findNavController()
        binding = FragmentPlantDetailBinding.inflate(layoutInflater)
        /*plant Id*/
        arguments?.getString("id")?.let {
            plantDetailViewModel.plantId = it
            /**/
            plantId = it
            viewModel.loadData(plantId)
            binding.editBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("id",plantId)
                navController.navigate(R.id.plant_detail_to_plant_edit_fragment,bundle)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding.backBtn.setOnClickListener {
            navController.popBackStack()
        }


        //binding.calendarMonth.text =currentMonth.toString()
        //plantDetailViewModel.getSchedule(currentMonth.year,currentMonth.monthValue)
//        plantDetailViewModel.getSchedule(2022,8)
//        Log.i("주목주목",plantDetailViewModel.scheduleData.value.toString())


        val daysOfWeek=daysOfWeekFromLocale()
        /* month binder*/
        binding.calendarView.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .toUpperCase(Locale.ENGLISH)
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    }
                    month.yearMonth
                }
            }
        }

        binding.calendarMonthPrevBtn.setOnClickListener {
            binding.calendarView.findLastVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.yearMonth.previous)
            }
        }
        binding.calendarMonthNextBtn.setOnClickListener {
            binding.calendarView.findLastVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.yearMonth.next)
            }
        }

        setCalendarView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        observeViewModel()

        binding.feedRecyclerview.adapter = feedAdapter
        binding.feedRecyclerview.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
        binding.calendarView.monthScrollListener = { month ->
            val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
            plantDetailViewModel.getSchedule(month.yearMonth.year,month.yearMonth.monthValue)
            binding.calendarMonth.text = title
            selectDate(month.yearMonth.atDay(1))
        }

    }

    private fun initList() {
        feedAdapter = FeedAdapter()
        feedAdapter.onClickFeed = { feedId ->
            val bundle = Bundle()
            bundle.putString("id", feedId)
            navController.navigate(R.id.plant_detail_to_feed_detail_fragment,bundle)
        }
    }

    private fun observeViewModel() {
        viewModel.data.observe(viewLifecycleOwner, Observer {
            binding.plantName.text = it.name
            Glide.with(binding.root)
                .load(it.thumbnail)
                .into(binding.plantImage)
            binding.plantSpeciesDesc.text = it.species
            binding.plantAdoptDayDesc.text = dateFormat.format(it.adopt_date)
            //binding.plantMood.text = it.mood
            binding.plantDDayDesc.text = it.d_day.toString()

        })
        plantDetailViewModel.scheduleData.observe(viewLifecycleOwner, Observer {
            scheduleList = it
            //binding.calendarView.notify
        })
        plantDetailViewModel.feedList.observe(viewLifecycleOwner, Observer {
            feedAdapter.refreshItems(it)
        })
    }

    override fun onResume() {
        super.onResume()
        plantDetailViewModel.getFeed(null)
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)
        }
    }
    fun daysOfWeekFromLocale(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val daysOfWeek = DayOfWeek.values()
        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        // Only necessary if firstDayOfWeek is not DayOfWeek.MONDAY which has ordinal 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            return rhs + lhs
        }
        return daysOfWeek
    }

    private fun setCalendarView(){
        with(binding.calendarView){
            /*calendar date start*/
            val currentMonth = YearMonth.now()
            val firstMonth = currentMonth.minusMonths(10)
            val lastMonth = currentMonth.plusMonths(10)
            val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

            binding.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
            binding.calendarView.scrollToMonth(currentMonth)

            selectDate(today)

            /* day binder */
            binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
                // Called only when a new container is needed.
                override fun create(view: View) = DayViewContainer(view)

                // Called every time we need to reuse a container.
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.day = day
                    container.binding.calendarDayText.text = day.date.dayOfMonth.toString()

                    /* day click */
                    container.binding.root.setOnClickListener {
                        plantDetailViewModel.getFeed(day.date.toString())
                        selectDate(day.date)
                    }

                    Log.i("day", day.toString())
                    Log.i("day.date", day.date.toString())
                    //Log.i("scheduleList",scheduleList.toString())

                    /* dot 표시 */
                    for (item in scheduleList) {
                        val date = dateFormat.format(item.timestamp)
                        if (date == day.date.toString()) {
                            for (k in item.kinds) {
                                if (k == "water") {
                                    container.binding.dot1.visibility = View.VISIBLE
                                    container.binding.dot1.setImageResource(R.drawable.ic_dot_water)
                                } else if (k == "air") {
                                    container.binding.dot2.visibility = View.VISIBLE
                                    container.binding.dot2.setImageResource(R.drawable.ic_dot_air)
                                } else if (k == "spray") {
                                    container.binding.dot3.visibility = View.VISIBLE
                                    container.binding.dot3.setImageResource(R.drawable.ic_dot_spray)
                                } else if (k == "prune") {
                                    container.binding.dot4.visibility = View.VISIBLE
                                    container.binding.dot4.setImageResource(R.drawable.ic_dot_prune)
                                } else if (k == "fertilize") {
                                    container.binding.dot5.visibility = View.VISIBLE
                                    container.binding.dot5.setImageResource(R.drawable.ic_dot_fertilize)
                                } else if (k == "repot") {
                                    container.binding.dot6.visibility = View.VISIBLE
                                    container.binding.dot6.setImageResource(R.drawable.ic_dot_repot)
                                }
                            }

                        }
                    }


                    if (day.owner == DayOwner.THIS_MONTH) {
                        /* 해당하는 달의 글씨 색만 black */
                        when (day.date) {
                            /* 오늘의 글씨색과 배경 */
                            today -> {
                                container.binding.calendarDayText.setTextColor(resources.getColor(R.color.black))
                                container.binding.calendarDayText.setBackgroundResource(R.drawable.round_calendar_today)
                            }
                            /* 선택한 날짜의 글씨색과 배경 */
                            selectedDate -> {
                                container.binding.calendarDayText.setTextColor(resources.getColor(R.color.white))
                                container.binding.calendarDayText.setBackgroundResource(R.drawable.round_calendar_clickday)
                            }
                            /* 그 외의의 글씨색과 배경 */
                            else -> {
                                container.binding.calendarDayText.setTextColor(resources.getColor(R.color.black))
                                container.binding.calendarDayText.background = null
                            }
                        }
                    } else {
                        container.binding.calendarDayText.setTextColor(resources.getColor(R.color.calendar_text_grey))
                    }
                }
            }
        }
    }
}

class DayViewContainer(view: View) : ViewContainer(view) {
    // With ViewBinding
    val binding = CalendarDayLayoutBinding.bind(view)
    lateinit var day: CalendarDay

//    init {
//        view.setOnClickListener {
//            if (day.owner == DayOwner.THIS_MONTH) {
//                selectDate(day.date)
//            }
//        }
//    }
}

