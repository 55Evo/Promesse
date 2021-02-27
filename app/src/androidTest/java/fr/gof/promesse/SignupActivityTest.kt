package fr.gof.promesse

import android.content.Context
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class SignupActivityTest {


    @Rule
    @JvmField
    var activityRule = ActivityScenarioRule(SignupActivity::class.java)

    /**
     * Add promise success
     *
     */
    @Test
    fun signUpSuccessful() {
        // Type text and then press the button.
        onView(ViewMatchers.withId(R.id.editTextEmail))
                .perform(ViewActions.typeText("test@connection.com"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextName))
                .perform(ViewActions.typeText("Test"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextPassword))
                .perform(ViewActions.typeText("MySecretPassword"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
                .perform(ViewActions.typeText("MySecretPassword"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.buttonValidate))
                .perform(ViewActions.click())
    }
    @Test
    fun signUpInvalidEmailFormat() {
        // Type text and then press the button.
        onView(ViewMatchers.withId(R.id.editTextEmail))
                .perform(ViewActions.typeText("emailInvalide"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextName))
                .perform(ViewActions.typeText("Test"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextPassword))
                .perform(ViewActions.typeText("MySecretPassword"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
                .perform(ViewActions.typeText("MySecretPassword"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.buttonValidate))
                .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.editTextEmail)).check(ViewAssertions.matches(ViewMatchers.hasErrorText("Format invalide")))
    }
    @Test
    fun signUpInvalidPasswordDoesntMatch() {
        // Type text and then press the button.
        onView(ViewMatchers.withId(R.id.editTextEmail))
                .perform(ViewActions.typeText("test@connection.com"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextName))
                .perform(ViewActions.typeText("Test"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextPassword))
                .perform(ViewActions.typeText("MySecretPassword"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
                .perform(ViewActions.typeText("AnOtherSecretPassword"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.buttonValidate))
                .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.editTextConfirmPassword)).check(ViewAssertions.matches(ViewMatchers.hasErrorText("Les mots de passe de correspondent pas")))
    }
    @Test
    fun signUpInvalidEmailMissing() {
        // Type text and then press the button.
        onView(ViewMatchers.withId(R.id.editTextEmail))
                .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextName))
                .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextPassword))
                .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
                .perform(ViewActions.typeText(""), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.buttonValidate))
                .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.editTextEmail)).check(ViewAssertions.matches(ViewMatchers.hasErrorText("Veuillez remplir ce champ")))
        onView(ViewMatchers.withId(R.id.editTextName)).check(ViewAssertions.matches(ViewMatchers.hasErrorText("Veuillez remplir ce champ")))
        onView(ViewMatchers.withId(R.id.editTextPassword)).check(ViewAssertions.matches(ViewMatchers.hasErrorText("Le mot de passe est trop court")))
        onView(ViewMatchers.withId(R.id.editTextConfirmPassword)).check(ViewAssertions.matches(ViewMatchers.hasErrorText("Veuillez remplir ce champ")))
    }
}