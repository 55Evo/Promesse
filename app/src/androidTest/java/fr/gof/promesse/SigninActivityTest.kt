package fr.gof.promesse

import android.content.Context
import android.widget.DatePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
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
class SigninActivityTest {


    @Rule
    @JvmField
    var activityRule = ActivityScenarioRule(SigninActivity::class.java)

    /**
     * Add promise success
     *
     */
    @Test
    fun signInSuccessful() {
        // Type text and then press the button.
        onView(withId(R.id.editTextEmail))
                .perform(typeText("test@connection.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword))
                .perform(typeText("MySecretPassword"), closeSoftKeyboard())
        onView(withId(R.id.buttonValidate))
                .perform(click())
    }

    @Test
    fun signInWrongEmail() {
        // Type text and then press the button.
        onView(withId(R.id.editTextEmail))
                .perform(typeText("wrong-email@connection.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword))
                .perform(typeText("MySecretPassword"), closeSoftKeyboard())
        onView(withId(R.id.buttonValidate))
                .perform(click())
        onView(withId(R.id.editTextEmail)).check(ViewAssertions.matches(hasErrorText("Cet email n'existe pas")))
    }

    @Test
    fun signInWrongPassword() {
        // Type text and then press the button.
        onView(withId(R.id.editTextEmail))
                .perform(typeText("test@connection.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword))
                .perform(typeText("MyWrongPassword"), closeSoftKeyboard())
        onView(withId(R.id.buttonValidate))
                .perform(click())
        onView(withId(R.id.editTextPassword)).check(ViewAssertions.matches(hasErrorText("Mot de passe incorrect")))
    }

}