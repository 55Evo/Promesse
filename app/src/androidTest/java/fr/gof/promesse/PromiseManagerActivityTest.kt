package fr.gof.promesse

import android.content.Context
import android.widget.DatePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import fr.gof.promesse.adapter.PromiseAdapter
import fr.gof.promesse.model.Promise
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*


/**
 * Promise manager activity test
 *
 * @constructor Create empty Promise manager activity test
 */
@LargeTest
class PromiseManagerActivityTest {

    private lateinit var promiseToAdd: Promise
    private lateinit var dateTodo : Date
    private lateinit var dateCrea : Date

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))

    @Rule
    @JvmField
    var activityRule = ActivityScenarioRule(MainActivity::class.java)
    lateinit var instrumentationContext: Context

    /**
     * Init valid string
     *
     */
    @Before
    fun initValidString() {
//
//        dateTodo = Date(System.currentTimeMillis()+1000000)
//        dateCrea = Date(System.currentTimeMillis())
//        promiseToAdd = Promise(-1, "Titre", 1, State.TODO, true, "desc", false, dateCrea, dateTodo, null)
//        calendar.time = dateTodo
//        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    /**
     * Add promise success
     *
     */
    @Test
    fun addPromiseSuccess() {
        // Type text and then press the button.
        onView(withId(R.id.buttonAdd))
                .perform(click())
        onView(withId(R.id.editTextTitle))
                .perform(typeText(promiseToAdd.title), closeSoftKeyboard())
        onView(withId(R.id.editTextDuration))
                .perform(typeText(promiseToAdd.duration.toString()), closeSoftKeyboard())
        onView(withId(R.id.switchPriority))
                .perform(click())
        onView(withId(R.id.editTextDescription))
                .perform(typeText(promiseToAdd.duration.toString()), closeSoftKeyboard())
        onView(withId(R.id.textViewDatePicker))
                .perform(click())

        onView(isAssignableFrom(DatePicker::class.java)).perform(PickerActions.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.buttonSave)).perform(click())


    }

    /**
     * Add promise missing field
     *
     */
    @Test
    fun addPromiseMissingField() {

        // Type text and then press the button.
        onView(withId(R.id.buttonAdd))
                .perform(click())
        onView(withId(R.id.editTextDuration))
                .perform(typeText(promiseToAdd.duration.toString()), closeSoftKeyboard())
        onView(withId(R.id.switchPriority))
                .perform(click())
        onView(withId(R.id.editTextDescription))
                .perform(typeText(promiseToAdd.duration.toString()), closeSoftKeyboard())
        onView(withId(R.id.textViewDatePicker))
                .perform(click())

        onView(isAssignableFrom(DatePicker::class.java)).perform(PickerActions.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.buttonSave)).perform(click())
        onView(withId(R.id.editTextTitle)).check(matches(hasErrorText("Veuillez remplir ce champ")))

    }

    /**
     * Remove promise
     *
     */
    @Test
    fun removePromise() {
        // Type text and then press the button.
        onView(withId(R.id.buttonAdd))
                .perform(click())
        onView(withId(R.id.editTextTitle))
                .perform(typeText(promiseToAdd.title), closeSoftKeyboard())
        onView(withId(R.id.buttonSave)).perform(click())
        onView(withId(R.id.recyclerViewPromesse)).perform(RecyclerViewActions.actionOnItemAtPosition<PromiseAdapter.PromiseViewHolder>(0, longClick()))
        onView(withId(R.id.deleteButton))
                .perform(click())
    }

    /**
     * Edit promise
     *
     */
    @Test
    fun editPromise() {
        // Type text and then press the button.
        onView(withId(R.id.buttonAdd))
                .perform(click())
        onView(withId(R.id.editTextTitle))
                .perform(typeText(promiseToAdd.title), closeSoftKeyboard())
        onView(withId(R.id.buttonSave)).perform(click())
        onView(withId(R.id.recyclerViewPromesse)).perform(RecyclerViewActions.actionOnItemAtPosition<PromiseAdapter.PromiseViewHolder>(0, click()))
        onView(withId(R.id.recyclerViewPromesse)).perform(RecyclerViewActions.actionOnItemAtPosition<PromiseAdapter.PromiseViewHolder>(0, MyViewAction.clickChildViewWithId(R.id.buttonEdit)))
        onView(withId(R.id.editTextTitle))
                .perform(typeText("Edited!"), closeSoftKeyboard())
    }


    /**
     * Edit promise missing field
     *
     */
    @Test
    fun editPromiseMissingField() {
        // Type text and then press the button.
        onView(withId(R.id.buttonAdd))
                .perform(click())
        onView(withId(R.id.editTextTitle))
                .perform(typeText(promiseToAdd.title), closeSoftKeyboard())
        onView(withId(R.id.buttonSave)).perform(click())
        onView(withId(R.id.recyclerViewPromesse)).perform(RecyclerViewActions.actionOnItemAtPosition<PromiseAdapter.PromiseViewHolder>(0, click()))
        onView(withId(R.id.recyclerViewPromesse)).perform(RecyclerViewActions.actionOnItemAtPosition<PromiseAdapter.PromiseViewHolder>(0, MyViewAction.clickChildViewWithId(R.id.buttonEdit)))
        onView(withId(R.id.editTextTitle))
                .perform(replaceText(""), closeSoftKeyboard())
    }
}