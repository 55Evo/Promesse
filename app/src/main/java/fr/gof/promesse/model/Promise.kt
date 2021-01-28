package fr.gof.promesse.model

import java.sql.Timestamp
import java.util.*

data class Promise (val id : Int,
                    var title : String,
                    var duration : Int,
                    var state : State,
                    var priority : Boolean,
                    var description : String,
                    var professional : Boolean,
                    var dateCreation : Date,
                    var dateTodo : Date,
                    var subtasks : MutableList<Subtask>?) {

}