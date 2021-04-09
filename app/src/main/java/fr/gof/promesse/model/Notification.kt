package fr.gof.promesse.model

import java.text.DateFormat
import java.util.*

/**
 * Notification
 *
 * @property recipient
 * @property author
 * @property date
 * @property title
 * @property read
 * @constructor Create empty Notification
 */
data class Notification(
    val recipient: String,
    val author: String,
    val date: Date,
    val title: String,
    val read: Boolean
) {
    private val dfl: DateFormat = DateFormat.getDateInstance(DateFormat.FULL)
    fun getDateToString(): String = dfl.format(date)
}