package fr.gof.promesse.model

import java.io.Serializable
import java.text.DateFormat
import java.util.*

/**
 * Promise
 *
 * @property id
 * @property title
 * @property duration
 * @property state
 * @property priority
 * @property description
 * @property professional
 * @property dateCreation
 * @property dateTodo
 * @property subtasks
 * @property isChecked
 * @property isDescDeployed
 * @constructor Create empty Promise
 */
data class Promise (val id : Int,
                    var title : String,
                    var duration : Int?,
                    var state : State,
                    var priority : Boolean,
                    var description : String,
                    var professional : Boolean,
                    var dateCreation : Date,
                    var dateTodo : Date,
                    var subtasks : MutableList<Subtask>?,
                    var isChecked : Boolean = false,
                    var isDescDeployed : Boolean = false) : Serializable, Comparable<Promise> {

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))

    override operator fun compareTo(other: Promise): Int {
        return if(this.dateTodo == other.dateTodo){
            -1
        } else {
            if(this.dateTodo.after(other.dateTodo)){
                1
            } else {
                -1
            }
        }
    }

    override fun toString(): String {
        return title
    }
    val dfl = DateFormat.getDateInstance(DateFormat.FULL);

    /**
     * Get date to do to string
     *
     */
    fun getDateToDoToString() = dfl.format(dateTodo)

    /**
     * Get date creation to string
     *
     */
    fun getDateCreationToString() = dfl.format(dateCreation)


}