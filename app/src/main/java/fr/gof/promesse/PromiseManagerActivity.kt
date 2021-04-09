package fr.gof.promesse

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrInterface
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
import fr.gof.promesse.services.DndManager
import java.text.DateFormat
import java.util.*


/**
 * Promise manager activity
 *
 * @constructor Create empty Promise manager activity
 */
class PromiseManagerActivity : AppCompatActivity() {

    private val dateSetListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        setDate(
            year,
            monthOfYear,
            dayOfMonth
        )
    }
    private val listCatgerie: List<Category> = listOf(
        Category.SPORT,
        Category.ETUDES,
        Category.CUISINE,
        Category.LOISIRS,
        Category.DEFAUT

    )
    private lateinit var slidr: SlidrInterface
    lateinit var adapterCategory: CategoryAdapter
    lateinit var adapterSubtask: SubtaskEditorAdapter
    lateinit var rvCategory: RecyclerView
    lateinit var rvSubtask: RecyclerView
    lateinit var backgroundImage: ImageView
    var choosenCategory: Category = Category.DEFAUT
    lateinit var subtasks: MutableList<Subtask>
    val promiseDataBase = PromiseDataBase(this@PromiseManagerActivity)
    lateinit var textViewDate: TextView
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))
    var promise: Promise? = null
    lateinit var date: Date
    val dfl = DateFormat.getDateInstance(DateFormat.FULL)
    lateinit var priority: Switch


    /**
     * On create method that is called at the start of activity to
     * instantiate it.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promise_manager)
        slidr = Slidr.attach(this, utils.config);
        backgroundImage = findViewById(R.id.backgroundImage)
        rvCategory = findViewById(R.id.recycler_Category)
        rvCategory.setHasFixedSize(true)
        rvSubtask = findViewById(R.id.recycler_subtask)
        rvSubtask.setHasFixedSize(true)
        priority = findViewById(R.id.switchPriority)
        priority.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                var dndMngr = DndManager(this)
                dndMngr.askPermission()
            }
        }
        val titleBar: TextView = findViewById(R.id.textViewTitleBar)
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
            adapterCategory = CategoryAdapter(
                this,
                listCatgerie,
                CategoryListener(listCatgerie, this),
                promiseDataBase,
                backgroundImage,
                promiseNm.category
            )
        } else {
            adapterCategory = CategoryAdapter(
                this,
                listCatgerie,
                CategoryListener(listCatgerie, this),
                promiseDataBase,
                backgroundImage,
                Category.DEFAUT
            )
        }
        adapterCategory.chosenCategory = choosenCategory
        rvCategory.adapter = adapterCategory
        rvCategory.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)

        if (promiseNm != null) {
            adapterCategory.chosenCategory = promiseNm.category
            adapterCategory.listCategory[adapterCategory.listCategory.lastIndexOf(promiseNm.category)].check =
                true
            rvCategory.scrollToPosition(adapterCategory.listCategory.lastIndexOf(promiseNm.category))
        } else {
            adapterCategory.listCategory[adapterCategory.listCategory.lastIndexOf(Category.DEFAUT)].check =
                true

        }

        adapterSubtask = SubtaskEditorAdapter(subtasks, SubtaskListener(subtasks, this), this)
        rvSubtask.adapter = adapterSubtask
        rvSubtask.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
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
     * Set promise in fields that set the fields of activity by informations
     * of promise passed in parameters.
     *
     * @param titleBar
     * @param promiseNm
     *
     * Méthos qui met à jour les champs de l'activité en fonction des informations
     * de la promesse passée en paramètres.
     */
    private fun setPromiseInFields(
        titleBar: TextView,
        promiseNm: Promise
    ) {
        titleBar.setText(R.string.titleEditPromise)
        val title: EditText = findViewById(R.id.editTextTitle)
        title.setText(promiseNm.title)
        val recipient: EditText = findViewById(R.id.editTextRecipient)
        recipient.setText(promiseNm.recipient)
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
    fun setDate(year: Int, month: Int, day: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        textViewDate.text = getDateToString(calendar.time)
    }


    /**
     * Get date to string
     *
     * @param date
     * @return date with good format
     */
    fun getDateToString(date: Date) = dfl.format(date)

    /**
     * On click date called when user click on the field.
     * It open a datePicker at the date of today.
     *
     * @param v
     *
     * Méthos appelée lorsque l'utilisateur clique sur le champ
     * date du formulaire.
     * Elle ouvre un datePicker à la date du jour.
     */
    fun onClickDate(v: View) {
        // Create DatePickerDialog (Spinner Mode):
        val date = Date(System.currentTimeMillis())
        if (promise != null) {
            calendar.time = promise!!.dateTodo
        } else {
            calendar.time = date
        }
        // Create DatePickerDialog (Spinner Mode):
        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()

    }

    /**
     * On click button validate called when user click on the saveButton.
     * It update the database with informations of the form.
     * It create a new promise or update odl promise.
     *
     * @param v
     *
     * Méthode appelée lorsque l'utilisateur appuie sur le bouton enregistrer.
     * Elle met à jour la base de données en fonction de ce qui a été entré
     * dans le formulaire.
     * Soit elle crée une nouvelle promesse, soit elle update l'ancienne
     * en fonction de promiseNM.
     */
    fun onClickButtonValidate(v: View) {
        //Recuperation des éléments
        choosenCategory = adapterCategory.chosenCategory
        val editTextTitle: TextView = findViewById(R.id.editTextTitle)
        val editTextRecipient: EditText = findViewById(R.id.editTextRecipient)
        val editTextDuration: TextView = findViewById(R.id.editTextDuration)
        val switchPriority: Switch = findViewById(R.id.switchPriority)
        val switchProfessional: Switch = findViewById(R.id.switchProfessional)
        val editTextDescription: TextView = findViewById(R.id.editTextDescription)
        if (editTextTitle.length() == 0) {
            editTextTitle.error = getString(R.string.emptyField)
            return
        }
        val promiseNm = promise
        if (promiseNm != null) {
            updatePromise(
                promiseNm,
                editTextTitle,
                editTextRecipient,
                editTextDuration,
                switchPriority,
                switchProfessional,
                editTextDescription
            )
        } else { //creation nouvelle promesse
            subtasks = mutableListOf()
            for (st: Subtask in adapterSubtask.subtaskList) {
                if (st.title != "") {
                    subtasks.add(st)
                }
            }
            val promise = Promise(
                -1,
                editTextTitle.text.toString(),
                editTextRecipient.text.toString(),
                adapterCategory.chosenCategory,
                if (editTextDuration.text.toString() == "") null else editTextDuration.text.toString()
                    .toInt(),
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
        user.stopDnd(this)
        finish()
    }

    /**
     * Update promise in the database.
     *
     * @param promiseNm
     * @param editTextTitle
     * @param editTextDuration
     * @param switchPriority
     * @param switchProfessional
     * @param editTextDescription
     */
    private fun updatePromise(
        promiseNm: Promise,
        editTextTitle: TextView,
        editTextRecipient: TextView,
        editTextDuration: TextView,
        switchPriority: Switch,
        switchProfessional: Switch,
        editTextDescription: TextView
    ) {
        promiseNm.title = editTextTitle.text.toString()
        promiseNm.recipient = editTextRecipient.text.toString()
        promiseNm.category = adapterCategory.chosenCategory
        promiseNm.duration =
            if (editTextDuration.text.toString() == "") null else editTextDuration.text.toString()
                .toInt()
        promiseNm.priority = switchPriority.isChecked
        promiseNm.professional = switchProfessional.isChecked
        promiseNm.dateTodo = calendar.time
        promiseNm.description = editTextDescription.text.toString()
        subtasks = mutableListOf()
        for (st: Subtask in adapterSubtask.subtaskList) {
            if (st.title != "") {
                subtasks.add(st)
            }
        }
        promiseNm.subtasks = subtasks
        user.updatePromise(promiseNm)

    }

    /**
     * On click button cancel called when user click on the cancelButton.
     * It finish the activity to go back.
     *
     * @param v
     *
     * Méthode appelée lorsque l'utilisateur appuie sur le bouton cancel.
     * Cela ferme l'activité et retourne en arrière.
     */
    fun onClickButtonCancel(v: View) {
        finish()
    }

    /**
     * On click button add subtask called when user click on the addSubtaskButton.
     * It create a new field to fill in.
     *
     * @param v
     *
     * Méthode appelée lorsque l'utilisateur appuie sur le bouton d'ajout de sous-tâche.
     * Elle permet de créer un nouveau champ à remplir pour ajouter une sous-tâche
     * à la promesse.
     */
    fun onClickButtonAddSubtask(v: View) {
        var position = subtasks.size
        subtasks.add(Subtask(-1, "", false))
        adapterSubtask.subtaskList = subtasks
        adapterSubtask.notifyDataSetChanged()

    }


}