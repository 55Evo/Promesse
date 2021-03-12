package fr.gof.promesse

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrInterface
import com.r0adkll.slidr.model.SlidrPosition
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.model.Promise
import org.naishadhparmar.zcustomcalendar.CustomCalendar
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener
import org.naishadhparmar.zcustomcalendar.Property
import java.util.*
import kotlin.collections.HashMap


/**
 * Calendar activity
 *
 * @constructor Create empty Calendar activity
 */
class CalendarActivity : AppCompatActivity(), OnNavigationButtonClickedListener,
    OnDateSelectedListener {

    lateinit var customCalendar: CustomCalendar
    lateinit var promises: MutableList<Promise>
    lateinit var promisesOfTheSelectedDay: MutableList<Promise>
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : PromiseAdapter
    private var calendar = Calendar.getInstance()
    private val today = Calendar.getInstance()
    var dateHashMap : MutableMap<Int, Any> = HashMap()
    var descHashMap : MutableMap<Any, Property> = HashMap()
    lateinit var monthDisplay : TextView
    private lateinit var slidr: SlidrInterface
    var  config : SlidrConfig =  SlidrConfig.Builder()
        .position(SlidrPosition.LEFT)
        .sensitivity(1f)
        .scrimColor(Color.BLACK)
        .scrimStartAlpha(0.8f)
        .scrimEndAlpha(0f)
        .velocityThreshold(2400F)
        .distanceThreshold(0.25f)
        .edge(true)
        .edgeSize(0.18f) // The % of the screen that counts as the edge, default 18%
        .build();
    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity)
        setDaysInFrench()
        slidr = Slidr.attach(this, utils.config);
        monthDisplay = findViewById(R.id.monthTextView)
        promisesOfTheSelectedDay = user.getPromisesOfTheDay(Date(System.currentTimeMillis())).toMutableList()
        customCalendar = findViewById(R.id.custom_calendar)
        customCalendar.background = getResources().getDrawable(R.drawable.calendar_background)
        recyclerView = findViewById(R.id.recyclerViewPromises)
        initProperty()
        customCalendar.setMapDescToProp(descHashMap)
        updateCalendarWithPromises(dateHashMap, calendar, calendar.get(Calendar.DAY_OF_MONTH))
        recyclerView.adapter = adapter
        customCalendar.setOnDateSelectedListener(this)
        setMonthInFrench()
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this)
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this)
        customCalendar.monthYearTextView.visibility = View.GONE
    }

    private fun lockSlider(){
        slidr.lock()
    }
    private fun unLockSlider(){
        slidr.unlock()
    }
    /**
     * Init property
     *
     */
    private fun initProperty() {
        var map = HashMap<String,Int>()
        map["default"] = R.layout.default_view
        map["first"] = R.layout.first_level_view
        map["second"] = R.layout.second_level_view
        map["third"] = R.layout.third_level_view
        map["default_selected"] = R.layout.default_selected_view
        map["first_selected"] = R.layout.first_selected_view
        map["second_selected"] = R.layout.second_selected_view
        map["third_selected"] = R.layout.third_selected_view

        for ((key,value) in map){
            var defaultProperty = Property()
            defaultProperty.layoutResource = map[key] as Int
            defaultProperty.dateTextViewResource = R.id.text_view
            descHashMap[key] = defaultProperty

        }
    }


    /**
     * Set days in french
     *
     */
    private fun setDaysInFrench(){
        var day: TextView = findViewById(R.id.tv_day_of_week_0)
        day.text = "Lun"
        day = findViewById(R.id.tv_day_of_week_1)
        day.text = "Mar"
        day = findViewById(R.id.tv_day_of_week_2)
        day.text = "Mer"
        day = findViewById(R.id.tv_day_of_week_3)
        day.text = "Jeu"
        day = findViewById(R.id.tv_day_of_week_4)
        day.text = "Ven"
        day = findViewById(R.id.tv_day_of_week_5)
        day.text = "Sam"
        day = findViewById(R.id.tv_day_of_week_6)
        day.text = "Dim"
    }


    /**
     * Set month in french
     *
     */
    private fun setMonthInFrench(){
        var monthYear: TextView = customCalendar.monthYearTextView
        var monthYearString : List<String> = monthYear.text.split(" ")

        var res = ""
        when(monthYearString[0]){
            "January" -> res = "Janvier ${monthYearString[1]}"
            "February" -> res = "Fevrier ${monthYearString[1]}"
            "March" -> res = "Mars ${monthYearString[1]}"
            "April" -> res = "Avril ${monthYearString[1]}"
            "May" -> res = "Mai ${monthYearString[1]}"
            "June" -> res = "Juin ${monthYearString[1]}"
            "July" -> res = "Juillet ${monthYearString[1]}"
            "August" -> res = "Aout ${monthYearString[1]}"
            "September" -> res = "Septembre ${monthYearString[1]}"
            "October" -> res = "Octobre ${monthYearString[1]}"
            "November" -> res = "Novembre ${monthYearString[1]}"
            "December" -> res = "Decembre ${monthYearString[1]}"
        }
        //monthYear.visibility = View.GONE

        monthDisplay.text = res
    }


    /**
     * Update calendar with promises
     *
     * @param dateHashMap
     * @param month
     * @param selectedDay
     */
    private fun updateCalendarWithPromises(
        dateHashMap: MutableMap<Int, Any>,
        month: Calendar,
        selectedDay: Int = 0
    ) {
        promises = user.getAllPromisesOfTheMonth(user.email,month.time).toMutableList()
        var occurencePromises = IntArray(32) {0}
        for (promise: Promise in promises) {
            occurencePromises[promise.dateTodo.date]++
        }
        for (day: Int in 1 until occurencePromises.size) {
            when (occurencePromises[day]) {
                0 -> if(day == selectedDay) dateHashMap[selectedDay] = "default_selected" else dateHashMap[day] = "default"
                in 1..2 -> if(day == selectedDay) dateHashMap[selectedDay] = "first_selected" else dateHashMap[day] = "first"
                in 3..4 -> if(day == selectedDay) dateHashMap[selectedDay] = "second_selected" else dateHashMap[day] = "second"
                else -> if(day == selectedDay) dateHashMap[selectedDay] = "third_selected" else dateHashMap[day] = "third"
            }
        }
        autoSelectionWhenMonthChanged(month, selectedDay, occurencePromises, dateHashMap)
        this.dateHashMap = dateHashMap
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        adapter = PromiseAdapter(promisesOfTheSelectedDay, PromiseEventListener(promisesOfTheSelectedDay, this), this)
        customCalendar.setDate(month, dateHashMap)
//        adapter.notifyDataSetChanged()
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    /**
     * Auto selection when month changed
     * Select the first day of the month when we change month and it's not the actual month
     * else it selects the actual day.
     *
     * @param month
     * @param selectedDay
     * @param occurencePromises
     * @param dateHashMap
     */
    private fun autoSelectionWhenMonthChanged(month: Calendar, selectedDay: Int, occurencePromises: IntArray, dateHashMap: MutableMap<Int, Any>) {
        if (month.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                selectedDay == 0 && month.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
            promisesOfTheSelectedDay = user.getPromisesOfTheDay(today.time).toMutableList()
            when (occurencePromises[today.get(Calendar.DAY_OF_MONTH)]) {
                0 -> dateHashMap[today.get(Calendar.DAY_OF_MONTH)] = "default_selected"
                in 1..2 -> dateHashMap[today.get(Calendar.DAY_OF_MONTH)] = "first_selected"
                in 3..4 -> dateHashMap[today.get(Calendar.DAY_OF_MONTH)] = "second_selected"
                else -> dateHashMap[today.get(Calendar.DAY_OF_MONTH)] = "third_selected"
            }
        }
        if (month.get(Calendar.MONTH) != today.get(Calendar.MONTH) && selectedDay == 0 ||
                month.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                selectedDay == 0 && month.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {
            var cld = Calendar.getInstance()
            cld.set(Calendar.YEAR, month.get(Calendar.YEAR))
            cld.set(Calendar.MONTH, month.get(Calendar.MONTH))
            cld.set(Calendar.DAY_OF_MONTH, 1)
            promisesOfTheSelectedDay = user.getPromisesOfTheDay(cld.time).toMutableList()
            when (occurencePromises[1]) {
                0 -> dateHashMap[1] = "default_selected"
                in 1..2 -> dateHashMap[1] = "first_selected"
                in 3..4 -> dateHashMap[1] = "second_selected"
                else -> dateHashMap[1] = "third_selected"
            }
        }
    }


    /**
     * Called when a month navigation button is called
     * @param whichButton Either `CustomCalendar.PREVIOUS` or `CustomCalendar.NEXT`
     * @param newMonth Calendar representation of the month that will be displayed next (including the day of month that will be selected)
     * @return For the new month, an array such that the first element is a map linking date to its description (This description will be accessible from the `desc` parameter of the onDateSelected method of OnDateSelectedListener) and the second element is a map linking date to the tag to be set on its date view (This tag will be accessible from the `view` parameter of the onDateSelected method of the OnDateSelectedListener)
     */
    override fun onNavigationButtonClicked(
        whichButton: Int,
        newMonth: Calendar?
    ): Array<MutableMap<Int, Any>>? {
        if (newMonth != null) {
            Handler().postDelayed({
                calendar = newMonth
                setMonthInFrench()
                updateCalendarWithPromises(dateHashMap, newMonth)
            },1)
        }
        return arrayOf(descHashMap as MutableMap<Int, Any>, dateHashMap)
    }


    /**
     * On resume
     *
     */
    override fun onResume() {
        super.onResume()
        promises = user.getPromisesOfTheDay(calendar.time).toMutableList()
        adapter = PromiseAdapter(promises, PromiseEventListener(promises, this),this)
        adapter.notifyDataSetChanged()
        recyclerView.adapter = adapter
    }


    /**
     * Called when a date is selected
     * @param view The date view that was clicked (the tag on this view will be as given in the map linking date to the tag)
     * @param selectedDate Calendar representation of the selected date
     * @param desc Description of the date (as given in the map linking date to its description)
     */
    override fun onDateSelected(view: View?, selectedDate: Calendar?, desc: Any?) {
        if (selectedDate != null) {
            promisesOfTheSelectedDay = user.getPromisesOfTheDay(selectedDate.time).toMutableList()
            updateCalendarWithPromises(dateHashMap, calendar, selectedDate.get(Calendar.DAY_OF_MONTH))
        } else {
            updateCalendarWithPromises(dateHashMap, calendar)
        }
    }

}