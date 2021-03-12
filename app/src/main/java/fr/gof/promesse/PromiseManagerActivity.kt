package fr.gof.promesse

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrInterface
import com.r0adkll.slidr.model.SlidrPosition
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.adapter.CategoryAdapter
import fr.gof.promesse.adapter.SubtaskEditorAdapter
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.CategoryListener
import fr.gof.promesse.listener.SubtaskListener
import fr.gof.promesse.model.Category
import fr.gof.promesse.model.Promise

import fr.gof.promesse.model.State
import fr.gof.promesse.model.Subtask
import java.text.DateFormat
import java.util.*


/**
 * Promise manager activity
 *
 * @constructor Create empty Promise manager activity
 */
class PromiseManagerActivity : AppCompatActivity() {

    private val dateSetListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth -> setDate(year, monthOfYear, dayOfMonth)}
    private val listCatgerie: List<Category> = listOf(
            Category.SPORT,
            Category.ETUDES,
            Category.CUISINE,
            Category.LOISIRS,
            Category.DEFAUT

    )
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

    lateinit var adapterCategory : CategoryAdapter
    lateinit var adapterSubtask : SubtaskEditorAdapter
    lateinit var rvCategory:  RecyclerView
    lateinit var rvSubtask:  RecyclerView
    lateinit var backgroundImage : ImageView
    var choosenCategory: Category = Category.DEFAUT
    lateinit var subtasks : MutableList<Subtask>


    val promiseDataBase = PromiseDataBase(this@PromiseManagerActivity)
    lateinit var textViewDate : TextView
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))
    var promise : Promise?= null
    lateinit var date : Date
    val dfl = DateFormat.getDateInstance(DateFormat.FULL);


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.promise_manager_activity)
        slidr = Slidr.attach(this, utils.config);
        backgroundImage = findViewById(R.id.backgroundImage)
        rvCategory = findViewById(R.id.recycler_Category)
        rvCategory.setHasFixedSize(true)
        rvSubtask = findViewById(R.id.recycler_subtask)
        rvSubtask.setHasFixedSize(true)





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
            subtasks = mutableListOf()
        }



        if (promiseNm != null) {
            backgroundImage.setImageResource(promiseNm.category.background)
            adapterCategory = CategoryAdapter(this, listCatgerie,CategoryListener(listCatgerie, this) , promiseDataBase, backgroundImage, promiseNm.category)
        }
        else{
            adapterCategory = CategoryAdapter(this, listCatgerie,CategoryListener(listCatgerie, this) , promiseDataBase, backgroundImage, Category.DEFAUT)
        }
        adapterCategory.chosenCategory = choosenCategory
        rvCategory.adapter = adapterCategory
            rvCategory.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)

        if (promiseNm != null) {
            adapterCategory.listCategory[adapterCategory.listCategory.lastIndexOf(promiseNm.category)].check = true
        }
        else{
            adapterCategory.listCategory[adapterCategory.listCategory.lastIndexOf(Category.DEFAUT)].check = true
        }
//        adapter.listCategory[adapter.listCategory.indexOf(adapter.chosenCategory)].background = R.drawable.cuisine
//        adapter.notifyDataSetChanged()

        adapterSubtask = SubtaskEditorAdapter(subtasks, SubtaskListener(subtasks, this), this)
        rvSubtask.adapter = adapterSubtask
        rvSubtask.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

    }
    private fun lockSlider(){
        slidr.lock()
    }
    private fun unLockSlider(){
        slidr.unlock()
    }
    private fun setPromiseInFields(
        titleBar: TextView,
        promiseNm: Promise
    ) {
        titleBar.setText(R.string.titleEditPromise)
        val title: EditText = findViewById(R.id.editTextTitle)
        title.setText(promiseNm.title)
        val duration: EditText = findViewById(R.id.editTextDuration)
        if (promise?.duration != null)
            duration.setText(promise?.duration.toString())
        date = promiseNm.dateTodo
        calendar.time = date
        val description: EditText = findViewById(R.id.editTextDescription)
        description.setText(promiseNm.description)
        val priority: Switch = findViewById(R.id.switchPriority)
        priority.isChecked = promiseNm.priority
        val professional: Switch = findViewById(R.id.switchProfessional)
        professional.isChecked = promiseNm.professional
        textViewDate.text = promiseNm.getDateToDoToString()
        subtasks = promiseNm.subtasks
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
        if (promise != null ) {
            calendar.time = promise!!.dateTodo
        }
        else {
            calendar.time = date
        }
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
        choosenCategory = adapterCategory.chosenCategory
        val editTextTitle : TextView = findViewById(R.id.editTextTitle)
        val editTextDuration : TextView = findViewById(R.id.editTextDuration)
        val switchPriority : Switch = findViewById(R.id.switchPriority)
        val switchProfessional : Switch = findViewById(R.id.switchProfessional)
        val editTextDescription : TextView = findViewById(R.id.editTextDescription)
        if (editTextTitle.length() == 0) {
            editTextTitle.error = getString(R.string.emptyField)
            return
        }

        Log.d("ttttttttttttttt","yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy")


        val promiseNm = promise

        if (promiseNm != null) {
            updatePromise(
                promiseNm,
                editTextTitle,
                editTextDuration,
                switchPriority,
                switchProfessional,
                editTextDescription
            )
        } else { //creation nouvelle promesse
            subtasks = mutableListOf()
            for (st : Subtask in adapterSubtask.subtaskList) {
                if (st.title != "") {
                    subtasks.add(st)
                }
            }
            val promise = Promise(
                    -1,
                    editTextTitle.text.toString(),
                    adapterCategory.chosenCategory,
                    if (editTextDuration.text.toString() == "") null else editTextDuration.text.toString().toInt(),
                    State.TODO,
                    switchPriority.isChecked,
                    editTextDescription.text.toString(),
                    switchProfessional.isChecked,
                    Date(System.currentTimeMillis()),
                    calendar.time,
                    subtasks
            )


            user.addPromise(promise)
        }

        finish()
    }

    private fun updatePromise(
        promiseNm: Promise,
        editTextTitle: TextView,
        editTextDuration: TextView,
        switchPriority: Switch,
        switchProfessional: Switch,
        editTextDescription: TextView
    ) {
        promiseNm.title = editTextTitle.text.toString()
        promiseNm.category = adapterCategory.chosenCategory
        promiseNm.duration =
            if (editTextDuration.text.toString() == "") null else editTextDuration.text.toString()
                .toInt()
        promiseNm.priority = switchPriority.isChecked
        promiseNm.professional = switchProfessional.isChecked
        promiseNm.dateTodo = calendar.time
        promiseNm.description = editTextDescription.text.toString()
        subtasks = mutableListOf()
        for (st : Subtask in adapterSubtask.subtaskList) {
            if (st.title != "") {
                subtasks.add(st)
            }
        }
        promiseNm.subtasks = subtasks
        user.updatePromise(promiseNm)

    }

    /**
     * On click button cancel
     *
     * @param v
     */
    fun onClickButtonCancel (v : View) {
        finish()
    }

    fun onClickButtonAddSubtask (v : View) {
        var position = subtasks.size
        subtasks.add(Subtask(-1, "", false))
        adapterSubtask.subtaskList = subtasks
        adapterSubtask.notifyDataSetChanged()

    }


}