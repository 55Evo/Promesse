package fr.gof.promesse.model

import java.io.Serializable
import java.text.DateFormat
import java.util.*

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

    fun getDateToDoToString() = dfl.format(dateTodo)

    fun getDateCreationToString() = dfl.format(dateCreation)


}