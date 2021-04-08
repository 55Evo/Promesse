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
data class Promise(
    var id: Int,
    var title: String,
    var recipient : String,
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

    private var focus = false
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))


    /**
     * Equals
     *
     * @param other
     * @return
     */
    override fun equals(other: Any?): Boolean {
        if( other is Promise){
            return other.id == this.id
        }
        return false
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
    fun getDateToString() = dfl.format(dateTodo)

    /**
     * Compare to
     *
     * @param other
     * @return
     */
    override fun compareTo(other: Promise): Int {
        return if (this.id==other.id) return 0 else return compare(other)
    }

    /**
     * Compare
     *
     * @param other
     * @return
     */
    private fun compare(other : Promise) : Int{
        if (this.dateTodo.before(other.dateTodo)) return -1 else return 1
    }

    /**
     * Hash code
     *
     * @return
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }

    /**
     * Get nb st done
     *
     * @return
     */
    fun getNbStDone(): Int {
        var nbDone = 0
        for (st: Subtask in this.subtasks) {
            if (st.done) {
                nbDone ++
            }
        }
        return nbDone
    }


}