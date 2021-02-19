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
import fr.gof.promesse.model.Mascot
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import fr.gof.promesse.model.User
import java.text.DateFormat
import java.util.*


/**
 * Promise manager activity
 *
 * @constructor Create empty Promise manager activity
 */
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
            setPromiseInFields(titleBar, promiseNm)
        } else {
            titleBar.setText(R.string.titleCreatePromise)
            textViewDate.text = getDateToString(Date(System.currentTimeMillis()))
        }
    }

    private fun setPromiseInFields(
        titleBar: TextView,
        promiseNm: Promise
    ) {
        titleBar.setText(R.string.titleEditPromise)
        val title: EditText = findViewById(R.id.editTextTitle)
        title.setText(promiseNm.title)
        val duration: EditText = findViewById(R.id.editTextDuration)
        duration.setText(promise?.duration.toString())
        date = promiseNm.dateTodo
        val description: EditText = findViewById(R.id.editTextDescription)
        description.setText(promiseNm.description)
        val priority: Switch = findViewById(R.id.switchPriority)
        priority.isChecked = promiseNm.priority
        val professional: Switch = findViewById(R.id.switchProfessional)
        professional.isChecked = promiseNm.professional
        textViewDate.text = promiseNm.getDateToDoToString()
    }

    /**
     * Set date
     *
     * @param year
     * @param month
     * @param day
     */
    fun setDate(year : Int, month : Int, day : Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        textViewDate.text = getDateToString(calendar.time)
    }


    /**
     * Get date to string
     *
     * @param date
     */
    fun getDateToString(date : Date) = dfl.format(date)

    /**
     * On click date
     *
     * @param v
     */
    fun onClickDate (v : View) {
        // Create DatePickerDialog (Spinner Mode):
        val date = Date(System.currentTimeMillis())
        calendar.time = date
        // Create DatePickerDialog (Spinner Mode):
        val datePickerDialog = DatePickerDialog(this,
                dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()

    }

    /**
     * On click button validate
     *
     * @param v
     */
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
        val defaultUser = promiseDataBase.createDefaultAccount(Mascot("Super Mascotte", R.drawable.mascot1, R.drawable.mascot_afficher_1))
        val promiseNm = promise
        if (promiseNm != null) {
            updatePromise(
                promiseNm,
                editTextTitle,
                editTextDuration,
                switchPriority,
                switchProfessional,
                editTextDescription,
                defaultUser
            )
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
            defaultUser.addPromise(promise, promiseDataBase)
        }
        finish()
    }

    private fun updatePromise(
        promiseNm: Promise,
        editTextTitle: TextView,
        editTextDuration: TextView,
        switchPriority: Switch,
        switchProfessional: Switch,
        editTextDescription: TextView,
        defaultUser: User
    ) {
        promiseNm.title = editTextTitle.text.toString()
        promiseNm.duration =
            if (editTextDuration.text.toString() == "") null else editTextDuration.text.toString()
                .toInt()
        promiseNm.priority = switchPriority.isChecked
        promiseNm.professional = switchProfessional.isChecked
        promiseNm.dateTodo = calendar.time
        promiseNm.description = editTextDescription.text.toString()
        defaultUser.updatePromise(promiseNm, promiseDataBase)
    }

    /**
     * On click button cancel
     *
     * @param v
     */
    fun onClickButtonCancel (v : View) {
        finish()
    }
}