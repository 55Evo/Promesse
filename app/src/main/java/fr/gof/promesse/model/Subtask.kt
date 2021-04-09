package fr.gof.promesse.model

import java.io.Serializable

/**
 * Subtask
 *
 * @property id
 * @property title
 * @property done
 */
data class Subtask(val id: Int, var title: String, var done: Boolean) : Serializable
