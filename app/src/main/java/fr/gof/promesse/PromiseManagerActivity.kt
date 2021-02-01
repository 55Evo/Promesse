package fr.gof.promesse

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import java.util.*


class PromiseManagerActivity : AppCompatActivity() {

    private val dateSetListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth -> setDate(year, monthOfYear, dayOfMonth)}

    val promiseDataBase = PromiseDataBase(this@PromiseManagerActivity)
    private lateinit var textViewDate : TextView
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))
    lateinit var promise : Promise
    lateinit var date : Date


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.promise_manager_activity)


        promise = getIntent().getSerializableExtra("Promise") as Promise ;
        var test : EditText = findViewById(R.id.editTextTitle)
        test.setText( promise.title )


        textViewDate = findViewById(R.id.textViewDatePicker)
        // Date Select Listener.
        date = Date(System.currentTimeMillis())
        calendar.time = date
        updateDate()

    }

    fun setDate(year : Int, month : Int, day : Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        updateDate()
    }

    fun updateDate() {
        textViewDate.text = dayOfWeekToString(calendar.get(Calendar.DAY_OF_WEEK)) + " " + calendar.get(Calendar.DAY_OF_MONTH).toString() + " " + monthToString(calendar.get(Calendar.MONTH) + 1) + " " + calendar.get(Calendar.YEAR)
    }

    fun dayOfWeekToString(day : Int) = getString(when (day) {
        1 -> R.string.sunday
        2 -> R.string.monday
        3 -> R.string.thusday
        4 -> R.string.wednesday
        5 -> R.string.thurday
        6 -> R.string.friday
        7 -> R.string.saturday
        else -> throw IllegalArgumentException()
    })

    fun monthToString(month : Int) = getString(when (month) {
        1 -> R.string.january
        2 -> R.string.february
        3 -> R.string.march
        4 -> R.string.april
        5 -> R.string.may
        6 -> R.string.june
        7 -> R.string.july
        8 -> R.string.august
        9 -> R.string.september
        10 -> R.string.october
        11 -> R.string.november
        12 -> R.string.december
        else -> throw IllegalArgumentException()
    })

    fun onClickDate (v : View) {
        // Create DatePickerDialog (Spinner Mode):
        val date = Date(System.currentTimeMillis())
        calendar.time = date
        // Create DatePickerDialog (Spinner Mode):
        val datePickerDialog = DatePickerDialog(this,
                dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()

    }

    fun onClickButtonValidate (v : View) {
        //Recuperation des éléments
        val editTextTitle : TextView = findViewById(R.id.editTextTitle)
        val editTextDuration : TextView = findViewById(R.id.editTextDuration)
        val switchPriority : TextView = findViewById(R.id.switchPriority)
        val switchProfessional : TextView = findViewById(R.id.switchProfessional)
        val editTextDescription : TextView = findViewById(R.id.editTextDescription)

        if (editTextTitle.length() == 0) {
            editTextTitle.error = getString(R.string.emptyField)
            return
        }
        val defaultUser = promiseDataBase.createDefaultAccount()
        val promise = Promise(
                -1,
                editTextTitle.text.toString(),
                if (editTextDuration.text.toString() == "") null else editTextDuration.text.toString().toInt(),
                State.TODO,
                switchPriority.isSelected,
                editTextDescription.text.toString(),
                switchProfessional.isSelected,
                Date(System.currentTimeMillis()),
                calendar.time,
                null
        )
        defaultUser.addPromise(promise, promiseDataBase)
        //On change d'activité (vers SearchActivity)
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun onClickButtonCancel (v : View) {
        //On change d'activité (vers SearchActivity)
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
        finish()
    }
}