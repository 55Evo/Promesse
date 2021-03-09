package fr.gof.promesse

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.PromiseEventListener
import fr.gof.promesse.model.Promise
import org.naishadhparmar.zcustomcalendar.CustomCalendar
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener
import org.naishadhparmar.zcustomcalendar.Property
import java.util.*
import java.util.concurrent.TimeUnit
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
    private val promiseDataBase = PromiseDataBase(this@CalendarActivity)
    private var calendar = Calendar.getInstance()
    var dateHashMap : MutableMap<Int, Any> = HashMap()
    var descHashMap : MutableMap<Any, Property> = HashMap()
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity)
        setDaysInFrench()

        promisesOfTheSelectedDay = MainActivity.user.getPromisesOfTheDay(promiseDataBase, Date(System.currentTimeMillis())).toMutableList()
        customCalendar = findViewById(R.id.custom_calendar)
        recyclerView = findViewById(R.id.recyclerViewPromises)

        var defaultProperty = Property()
        defaultProperty.layoutResource = R.layout.default_view
        defaultProperty.dateTextViewResource = R.id.text_view
        descHashMap["default"] = defaultProperty

        var firstProperty = Property()
        firstProperty.layoutResource = R.layout.first_level_view
        firstProperty.dateTextViewResource = R.id.text_view
        descHashMap["first"] = firstProperty

        var secondProperty = Property()
        secondProperty.layoutResource = R.layout.second_level_view
        secondProperty.dateTextViewResource = R.id.text_view
        descHashMap["second"] = secondProperty

        var thirdProprety = Property()
        thirdProprety.layoutResource = R.layout.third_level_view
        thirdProprety.dateTextViewResource = R.id.text_view
        descHashMap["third"] = thirdProprety

        var defaultSelectedProprety = Property()
        defaultSelectedProprety.layoutResource = R.layout.default_selected_view
        defaultSelectedProprety.dateTextViewResource = R.id.text_view
        descHashMap["default_selected"] = defaultSelectedProprety

        var firstSelectedProprety = Property()
        firstSelectedProprety.layoutResource = R.layout.first_selected_view
        firstSelectedProprety.dateTextViewResource = R.id.text_view
        descHashMap["first_selected"] = firstSelectedProprety

        var secondSelectedProprety = Property()
        secondSelectedProprety.layoutResource = R.layout.second_selected_view
        secondSelectedProprety.dateTextViewResource = R.id.text_view
        descHashMap["second_selected"] = secondSelectedProprety

        var thirdSelectedProprety = Property()
        thirdSelectedProprety.layoutResource = R.layout.third_selected_view
        thirdSelectedProprety.dateTextViewResource = R.id.text_view
        descHashMap["third_selected"] = thirdSelectedProprety

        customCalendar.setMapDescToProp(descHashMap)

        updateCalendarWithPromises(dateHashMap, calendar, calendar.get(Calendar.DAY_OF_MONTH))
        recyclerView.adapter = adapter
        customCalendar.setOnDateSelectedListener(this)
        setMonthInFrench()
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this)
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this)
    }

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
        monthYear.text = res
    }

    private fun updateCalendarWithPromises(
        dateHashMap: MutableMap<Int, Any>,
        month: Calendar,
        selectedDay: Int = 0
    ) {
        promises = promiseDataBase.getAllPromisesOfTheMonth(MainActivity.user.email, month.time).toMutableList()
        var occurencePromises = IntArray(32) {0}
        for (promise: Promise in promises) {
            occurencePromises[promise.dateTodo.date]++
        }

        for (day: Int in 1 until occurencePromises.size) {
            when (occurencePromises[day]) {
                0 -> if(day==selectedDay) dateHashMap[selectedDay] = "default_selected" else dateHashMap[day] = "default"
                in 1..2 -> if(day==selectedDay) dateHashMap[selectedDay] = "first_selected" else dateHashMap[day] = "first"
                in 3..4 -> if(day==selectedDay) dateHashMap[selectedDay] = "second_selected" else dateHashMap[day] = "second"
                else -> if(day==selectedDay) dateHashMap[selectedDay] = "third_selected" else dateHashMap[day] = "third"
            }
        }

        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        adapter = PromiseAdapter(promisesOfTheSelectedDay, PromiseEventListener(promisesOfTheSelectedDay, this), this)
        customCalendar.setDate(month, dateHashMap)
        adapter.notifyDataSetChanged()
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
                updateCalendarWithPromises(dateHashMap, newMonth)
                setMonthInFrench()
            }, 1)
        }
        return arrayOf(descHashMap as MutableMap<Int, Any>, dateHashMap)
    }

    /**
     * Called when a date is selected
     * @param view The date view that was clicked (the tag on this view will be as given in the map linking date to the tag)
     * @param selectedDate Calendar representation of the selected date
     * @param desc Description of the date (as given in the map linking date to its description)
     */
    override fun onDateSelected(view: View?, selectedDate: Calendar?, desc: Any?) {
        if (selectedDate != null) {
            promisesOfTheSelectedDay = MainActivity.user.getPromisesOfTheDay(promiseDataBase, selectedDate.time).toMutableList()
            updateCalendarWithPromises(dateHashMap, calendar, selectedDate.get(Calendar.DAY_OF_MONTH))
        } else {
            updateCalendarWithPromises(dateHashMap, calendar)
        }

        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}