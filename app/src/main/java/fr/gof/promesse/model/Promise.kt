package fr.gof.promesse.model

import java.io.Serializable
import java.sql.Timestamp
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
                    var subtasks : MutableList<Subtask>?) : Serializable, Comparable<Promise> {

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

}