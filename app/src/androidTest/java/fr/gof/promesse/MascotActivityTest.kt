package fr.gof.promesse

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import fr.gof.promesse.adapter.MascotAdapter
import org.junit.Rule
import org.junit.Test

class MascotActivityTest {


    @Rule
    @JvmField
    var activityRule = ActivityScenarioRule(ChooseMascotActivity::class.java)

    /**
     * Add promise success
     *
     */
    @Test
    fun SelectMascot() {
        // Type text and then press the button.
        onView(ViewMatchers.withId(R.id.recycler_mascot)).perform(RecyclerViewActions.actionOnItemAtPosition<MascotAdapter.MyViewHolder>(2, click()))
    }
}