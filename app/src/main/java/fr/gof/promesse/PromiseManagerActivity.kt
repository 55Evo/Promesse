package fr.gof.promesse

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import java.text.DateFormat
import java.util.*


class PromiseManagerActivity : AppCompatActivity() {

    private val dateSetListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth -> setDate(year, monthOfYear, dayOfMonth)}

    val promiseDataBase = PromiseDataBase(this@PromiseManagerActivity)
    lateinit var textViewDate : TextView
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))
    var promise : Promise ?= null
    lateinit var date : Date
    val dfl = DateFormat.getDateInstance(DateFormat.FULL);


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.promise_manager_activity)
        var titleBar : TextView = findViewById(R.id.textViewTitleBar)
        if (intent.getSerializableExtra("Promise") != null) {
            promise = intent.getSerializableExtra("Promise") as Promise
        }
        textViewDate = findViewById(R.id.textViewDatePicker)

        val promiseNm = promise
        if (promiseNm != null) {
            titleBar.setText(R.string.titleEditPromise)
            var title : EditText = findViewById(R.id.editTextTitle)
            title.setText(promiseNm.title)
            var duration : EditText = findViewById(R.id.editTextDuration)
            duration.setText(promise?.duration.toString())
            date = promiseNm.dateTodo
            var description : EditText = findViewById(R.id.editTextDescription)
            description.setText(promiseNm.description)
            var priority : Switch = findViewById(R.id.switchPriority)
            priority.isChecked = promiseNm.priority
            var professional : Switch = findViewById(R.id.switchProfessional)
            professional.isChecked = promiseNm.professional
            textViewDate.text = promiseNm.getDateToDoToString()
        } else {
            titleBar.setText(R.string.titleCreatePromise)
            textViewDate.text = getDateToString(Date(System.currentTimeMillis()))
        }
        // Date Select Listener
        //calendar.time = date


    }

    fun setDate(year : Int, month : Int, day : Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        textViewDate.text = getDateToString(calendar.time)
    }


    fun getDateToString(date : Date) = dfl.format(date)

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
        val switchPriority : Switch = findViewById(R.id.switchPriority)
        val switchProfessional : Switch = findViewById(R.id.switchProfessional)
        val editTextDescription : TextView = findViewById(R.id.editTextDescription)
        if (editTextTitle.length() == 0) {
            editTextTitle.error = getString(R.string.emptyField)
            return
        }
        val promiseNm = promise
        if (promiseNm != null) { //Update promise
            promiseNm.title = editTextTitle.text.toString()
            promiseNm.duration = if (editTextDuration.text.toString() == "") null else editTextDuration.text.toString().toInt()
            promiseNm.priority = switchPriority.isChecked
            promiseNm.professional = switchProfessional.isChecked
            promiseNm.dateTodo = calendar.time
            promiseNm.description = editTextDescription.text.toString()
            utils.user.updatePromise(promiseNm, promiseDataBase)
        } else { //creation nouvelle promesse
            val promise = Promise(
                    -1,
                    editTextTitle.text.toString(),
                    if (editTextDuration.text.toString() == "") null else editTextDuration.text.toString().toInt(),
                    State.TODO,
                    switchPriority.isChecked,
                    editTextDescription.text.toString(),
                    switchProfessional.isChecked,
                    Date(System.currentTimeMillis()),
                    calendar.time,
                    null
            )
            utils.user.addPromise(promise, promiseDataBase)
        }

        finish()
    }
    fun onClickButtonCancel (v : View) {
        finish()
    }
}