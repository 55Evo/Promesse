package fr.gof.promesse

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import fr.gof.promesse.database.PromiseDataBase
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
    lateinit var promises: MutableSet<Promise>
    val promiseDataBase = PromiseDataBase(this@CalendarActivity)
    var dateHashMap : MutableMap<Int, Any> = HashMap()
    var descHashMap : MutableMap<Any, Property> = HashMap()
    var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity)

        customCalendar = findViewById(R.id.custom_calendar)

        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this)
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this)

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

        customCalendar.setMapDescToProp(descHashMap)

        updateCalendarWithPromises(dateHashMap, calendar)

        customCalendar.setOnDateSelectedListener(this)
        customCalendar.setNavigationButtonDrawable(CustomCalendar.PREVIOUS, R.drawable.previous_icon)
        customCalendar.setNavigationButtonDrawable(CustomCalendar.NEXT, R.drawable.next_icon)
    }

    private fun updateCalendarWithPromises(
        dateHashMap: MutableMap<Int, Any>,
        month: Calendar
    ) {
        promises = promiseDataBase.getAllPromisesOfTheMonth(utils.user.email, month.time) as MutableSet
        var occurencePromises = IntArray(32) {0}
        for (promise: Promise in promises) {
            occurencePromises[promise.dateTodo.date]++
        }

        for (day: Int in 1 until occurencePromises.size) {
            when (occurencePromises[day]) {
                0 -> dateHashMap[day] = "default"
                in 1..2 -> dateHashMap[day] = "first"
                in 3..4 -> dateHashMap[day] = "second"
                else -> dateHashMap[day] = "third"
            }
        }
        customCalendar.setDate(month, dateHashMap)
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
        updateCalendarWithPromises(dateHashMap, calendar)
    }

}