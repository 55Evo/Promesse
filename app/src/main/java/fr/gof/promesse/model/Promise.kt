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
 */
data class Promise(
    var id: Int,
    var title: String,
    var recipient: String,
    var category: Category,
    var duration: Int?,
    var state: State,
    var priority: Boolean,
    var description: String,
    var professional: Boolean,
    var dateCreation: Date,
    var dateTodo: Date,
    var subtasks: MutableList<Subtask>,
    var isChecked: Boolean = false,
    var isDescDeployed: Boolean = false
) : Serializable, Comparable<Promise> {

    /**
     * Equals to remove / add promise of a TreeSet.
     *
     * @param other
     * @return
     *
     * Pour pouvoir ajouter un retirer une promesse d'un TreeSet.
     */
    override fun equals(other: Any?): Boolean {
        if (other is Promise) {
            return other.id == this.id
        }
        return false
    }

    /**
     * To string.
     *
     * @return string
     */
    override fun toString(): String {
        return title
    }

    /**
     * Dfl is a dateFormatter to format dateToDo and dateCreation.
     */
    private val dfl: DateFormat = DateFormat.getDateInstance(DateFormat.FULL)

    /**
     * Get date to do to string at the good format.
     *
     */
    fun getDateToDoToString(): String = dfl.format(dateTodo)

    /**
     * Get date creation to string at the good format.
     *
     */
    fun getDateToString(): String = dfl.format(dateTodo)

    /**
     * Compare to that is used by the TreeSet.
     *
     * @param other
     * @return
     */
    override fun compareTo(other: Promise): Int {
        return if (this.id == other.id) 0 else compare(other)
    }

    /**
     * Compare that is used by the TreeSet.
     *
     * @param other
     * @return
     */
    private fun compare(other: Promise): Int {
        return if (this.dateTodo.before(other.dateTodo)) -1 else 1
    }

    /**
     * Hash code for HashSet.
     *
     * @return
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }

    /**
     * Get nb st done.
     *
     * @return the number of subtasks done.
     *
     * Retourne le nombre de sous-tâches finies.
     */
    fun getNbStDone(): Int {
        var nbDone = 0
        for (st: Subtask in this.subtasks) {
            if (st.done) {
                nbDone++
            }
        }
        return nbDone
    }

}