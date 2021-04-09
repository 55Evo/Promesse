package fr.gof.promesse.model

import java.io.Serializable

/**
 * Subtask
 *
 * @property id
 * @property title
 * @property done
 * @constructor Create empty Subtask
 */
data class Subtask(val id: Int, var title: String, var done: Boolean) : Serializable
