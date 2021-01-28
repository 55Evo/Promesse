package fr.gof.promesse

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class PromiseManagerActivity : AppCompatActivity() {

    private val dateSetListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth -> setDate(year, monthOfYear, dayOfMonth)}

    private lateinit var textViewDate : TextView
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))

    lateinit var date : Date


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.promise_manager_activity)
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
        textViewDate.text = calendar.get(Calendar.DAY_OF_MONTH).toString() + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR)
    }

    fun onClickDate (v : View) {
        // Create DatePickerDialog (Spinner Mode):
        val date = Date(System.currentTimeMillis())
        calendar.time = date
        // Create DatePickerDialog (Spinner Mode):
        val datePickerDialog = DatePickerDialog(this,
                dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
        Toast.makeText(this@PromiseManagerActivity, "Date clic !", Toast.LENGTH_SHORT).show()
    }

    fun onClickButtonValidate (v : View) {
        Toast.makeText(this@PromiseManagerActivity, "Validé", Toast.LENGTH_SHORT).show()
    }
    fun onClickButtonCancel (v : View) {
        Toast.makeText(this@PromiseManagerActivity, "Cancel", Toast.LENGTH_SHORT).show()
    }
}