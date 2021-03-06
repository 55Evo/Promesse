package fr.gof.promesse

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrInterface
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.listener.DeleteButtonListener
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
 */
class CalendarActivity : AppCompatActivity(), OnNavigationButtonClickedListener,
    OnDateSelectedListener {

    lateinit var customCalendar: CustomCalendar
    lateinit var promises: TreeSet<Promise>
    lateinit var promisesOfTheSelectedDay: TreeSet<Promise>
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: PromiseAdapter
    private lateinit var deleteButton: FloatingActionButton
    private lateinit var deleteListener: DeleteButtonListener
    private var calendar = Calendar.getInstance()
    private val today = Calendar.getInstance()
    var dateHashMap: MutableMap<Int, Any> = HashMap()
    var descHashMap: MutableMap<Any, Property> = HashMap()
    lateinit var monthDisplay: TextView
    private lateinit var slidr: SlidrInterface


    /**
     * On create method that is called at the start of activity to
     * instantiate it.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        deleteButton = findViewById(R.id.deleteButton)
        setDays()
        slidr = Slidr.attach(this, utils.config)
        monthDisplay = findViewById(R.id.monthTextView)
        promisesOfTheSelectedDay = user.getPromisesOfTheDay(Date(System.currentTimeMillis()))
        customCalendar = findViewById(R.id.custom_calendar)
        customCalendar.background = getResources().getDrawable(R.drawable.calendar_background)
        recyclerView = findViewById(R.id.recyclerViewPromises)
        initProperty()
        customCalendar.setMapDescToProp(descHashMap)
        customCalendar.setOnDateSelectedListener(this)
        setMonthInFrench()
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this)
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this)
        customCalendar.monthYearTextView.visibility = View.GONE
    }

    /**
     * On resume called when activity is called again.
     * It refresh the view.
     *
     * Méthode appelée quand une activité est ouverte de nouveau.
     * Elle permet de mettre à jour la vue.
     *
     */
    override fun onResume() {
        super.onResume()
        promises = user.getPromisesOfTheDay(calendar.time)
        adapter = PromiseAdapter(promises, PromiseEventListener(promises, this), this)
        deleteListener = DeleteButtonListener(adapter, this)
        deleteButton.setOnClickListener(deleteListener)
        updateCalendarWithPromises(dateHashMap, calendar, calendar.get(Calendar.DAY_OF_MONTH))
        adapter.notifyDataSetChanged()
        recyclerView.adapter = adapter
    }

    /**
     * Lock slider method that lock the back slide
     *
     * Méthode qui permet de bloquer le retour arrière
     * via le slide
     *
     */
    private fun lockSlider() {
        slidr.lock()
    }

    /**
     * Un lock slider method that unlock the back slide
     *
     * Méthode qui permet de débloquer le retour arrière
     * via le slide
     *
     */
    private fun unLockSlider() {
        slidr.unlock()
    }


    /**
     * Init property called in the onCreate to initialize the
     * days boxes of the calendar.
     *
     * Méthode appelée dans le onCreate pour initialiser les
     * cases des jours du calendrier.
     *
     */
    private fun initProperty() {
        val map = HashMap<String, Int>()
        map["default"] = R.layout.view_default
        map["first"] = R.layout.view_first_level
        map["second"] = R.layout.view_second_level
        map["third"] = R.layout.view_third_level
        map["default_selected"] = R.layout.view_default_selected
        map["first_selected"] = R.layout.view_first_selected
        map["second_selected"] = R.layout.view_second_selected
        map["third_selected"] = R.layout.view_third_selected

        for ((key, _) in map) {
            val defaultProperty = Property()
            defaultProperty.layoutResource = map[key] as Int
            defaultProperty.dateTextViewResource = R.id.text_view
            descHashMap[key] = defaultProperty

        }
    }


    /**
     * Set days in french
     *
     */
    private fun setDays() {
        var day: TextView = findViewById(R.id.tv_day_of_week_0)
        val shortDay = resources.getStringArray(R.array.shortDay)
        day.text = shortDay[0]
        day = findViewById(R.id.tv_day_of_week_1)
        day.text = shortDay[1]
        day = findViewById(R.id.tv_day_of_week_2)
        day.text = shortDay[2]
        day = findViewById(R.id.tv_day_of_week_3)
        day.text = shortDay[3]
        day = findViewById(R.id.tv_day_of_week_4)
        day.text = shortDay[4]
        day = findViewById(R.id.tv_day_of_week_5)
        day.text = shortDay[5]
        day = findViewById(R.id.tv_day_of_week_6)
        day.text = shortDay[6]
    }


    /**
     * Set month in french
     *
     */
    private fun setMonthInFrench() {
        val monthYear: TextView = customCalendar.monthYearTextView
        val monthYearString: List<String> = monthYear.text.split(" ")

        var res = ""
        when (monthYearString[0]) {
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
        monthDisplay.text = res
    }


    /**
     * Update calendar with promises to set the color of days boxes of the calendar.
     *
     * @param dateHashMap
     * @param month
     * @param selectedDay
     *
     * Met à jour les cases des jours du calendrier afin d'attribuer des couleurs
     * en fonction du nombre de promesses du jour.
     */
    private fun updateCalendarWithPromises(
        dateHashMap: MutableMap<Int, Any>,
        month: Calendar,
        selectedDay: Int = 0
    ) {
        promises = user.getAllPromisesOfTheMonth(user.email, month.time)
        val occurencePromises = IntArray(32) { 0 }
        for (promise: Promise in promises) {
            occurencePromises[promise.dateTodo.date]++
        }
        for (day: Int in 1 until occurencePromises.size) {
            when (occurencePromises[day]) {
                0 -> if (day == selectedDay) dateHashMap[selectedDay] =
                    "default_selected" else dateHashMap[day] = "default"
                in 1..2 -> if (day == selectedDay) dateHashMap[selectedDay] =
                    "first_selected" else dateHashMap[day] = "first"
                in 3..4 -> if (day == selectedDay) dateHashMap[selectedDay] =
                    "second_selected" else dateHashMap[day] = "second"
                else -> if (day == selectedDay) dateHashMap[selectedDay] =
                    "third_selected" else dateHashMap[day] = "third"
            }
        }
        autoSelectionWhenMonthChanged(month, selectedDay, occurencePromises, dateHashMap)
        this.dateHashMap = dateHashMap
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        adapter = PromiseAdapter(
            promisesOfTheSelectedDay,
            PromiseEventListener(promisesOfTheSelectedDay, this),
            this
        )
        deleteListener.adapter = adapter
        customCalendar.setDate(month, dateHashMap)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    /**
     * Auto selection when month changed.
     * Select the first day of the month when we change month and it's not the actual month
     * else it selects the actual day.
     *
     * @param month
     * @param selectedDay
     * @param occurencePromises
     * @param dateHashMap
     *
     * Permet de sélectionner automatiquement le 1 er du mois quand on change de mois
     * ou alors le jour actuel quand on retourne sur le mois actuel.
     */
    private fun autoSelectionWhenMonthChanged(
        month: Calendar,
        selectedDay: Int,
        occurencePromises: IntArray,
        dateHashMap: MutableMap<Int, Any>
    ) {
        if (month.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            selectedDay == 0 && month.get(Calendar.YEAR) == today.get(Calendar.YEAR)
        ) {
            promisesOfTheSelectedDay = user.getPromisesOfTheDay(today.time)
            when (occurencePromises[today.get(Calendar.DAY_OF_MONTH)]) {
                0 -> dateHashMap[today.get(Calendar.DAY_OF_MONTH)] = "default_selected"
                in 1..2 -> dateHashMap[today.get(Calendar.DAY_OF_MONTH)] = "first_selected"
                in 3..4 -> dateHashMap[today.get(Calendar.DAY_OF_MONTH)] = "second_selected"
                else -> dateHashMap[today.get(Calendar.DAY_OF_MONTH)] = "third_selected"
            }
        }
        if (month.get(Calendar.MONTH) != today.get(Calendar.MONTH) && selectedDay == 0 ||
            month.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            selectedDay == 0 && month.get(Calendar.YEAR) != today.get(Calendar.YEAR)
        ) {
            val cld = Calendar.getInstance()
            cld.set(Calendar.YEAR, month.get(Calendar.YEAR))
            cld.set(Calendar.MONTH, month.get(Calendar.MONTH))
            cld.set(Calendar.DAY_OF_MONTH, 1)
            promisesOfTheSelectedDay = user.getPromisesOfTheDay(cld.time)
            when (occurencePromises[1]) {
                0 -> dateHashMap[1] = "default_selected"
                in 1..2 -> dateHashMap[1] = "first_selected"
                in 3..4 -> dateHashMap[1] = "second_selected"
                else -> dateHashMap[1] = "third_selected"
            }
        }
    }


    /**
     * Called when a month navigation button is called.
     * @param whichButton Either `CustomCalendar.PREVIOUS` or `CustomCalendar.NEXT`
     * @param newMonth Calendar representation of the month that will be displayed next (including the day of month that will be selected)
     * @return For the new month, an array such that the first element is a map linking date to its description
     * (This description will be accessible from the `desc` parameter of the onDateSelected method of OnDateSelectedListener)
     * and the second element is a map linking date to the tag to be set on its date view
     * (This tag will be accessible from the `view` parameter of the onDateSelected method of the OnDateSelectedListener)
     *
     * Méthode appelée quand on clique sur les boutons pour changer de mois.
     * Elle permet de retourner un tableau qui contient une map liant la date à sa description
     * ainsi qu'une deuxième map permettant de lier la date à son tag pour pouvoir lui associer une vue.
     */
    override fun onNavigationButtonClicked(
        whichButton: Int,
        newMonth: Calendar?
    ): Array<MutableMap<Int, Any>> {
        if (newMonth != null) {
            Handler().postDelayed({
                calendar = newMonth
                setMonthInFrench()
                updateCalendarWithPromises(dateHashMap, newMonth)
            }, 1)
        }
        return arrayOf(descHashMap as MutableMap<Int, Any>, dateHashMap)
    }


    /**
     * Called when a date is selected.
     * @param view The date view that was clicked (the tag on this view will be as given in the map linking date to the tag)
     * @param selectedDate Calendar representation of the selected date
     * @param desc Description of the date (as given in the map linking date to its description)
     *
     * Méthode appelée quand un utilisateur appuie sur une date du calendrier afin d'afficher
     * les promesses du jour en-dessous.
     */
    override fun onDateSelected(view: View?, selectedDate: Calendar?, desc: Any?) {
        if (selectedDate != null) {
            promisesOfTheSelectedDay = user.getPromisesOfTheDay(selectedDate.time)
            updateCalendarWithPromises(
                dateHashMap,
                calendar,
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
        } else {
            updateCalendarWithPromises(dateHashMap, calendar)
        }
    }

}