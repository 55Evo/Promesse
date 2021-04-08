package fr.gof.promesse.model

import java.text.DateFormat
import java.util.*

data class Notification (val recipent: String, val author: String, val date: Date, val title: String, val read: Boolean) {
    val dfl = DateFormat.getDateInstance(DateFormat.FULL)
    fun getDateToString() = dfl.format(date)
}