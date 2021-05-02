package fr.gof.promesse.model

import android.app.Activity
import android.app.NotificationManager
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.services.DndManager
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 * User
 *
 * @property email
 * @property name
 * @property password
 * @property mascot
 */
data class User(
    var email: String,
    var username: String,
    var name: String,
    var password: String,
    var mascot: Mascot
) {

    private lateinit var listPromise: TreeSet<Promise>
    lateinit var db: PromiseDataBase


    /**
     * Add promise to the listPromise and to the database.
     *
     * @param promise
     *
     * Permet d'ajouter une promesse à la liste et à la base de données.
     */
    fun addPromise(promise: Promise) {
        promise.id = db.addPromise(email, promise).toInt()
        listAddPromise(promise)
    }

    /**
     * List add promise that add a promise to the listPromise.
     *
     * @param promise
     *
     * Ajoute une promesse à la liste après l'avoir supprimée avant.
     */
    private fun listAddPromise(promise: Promise) {
        removePromise(promise)
        listPromise.add(promise)
    }

    /**
     * Unread notification that creates a notification in the database to another user.
     *
     * @param promise
     * @return
     *
     * Méthode qui crée une notification relative à une promesse dans la base de données afin que l'utilisateur
     * à qui elle est liée puisse savoir que la promesse a été faite.
     */
    fun unreadNotification(promise: Promise): Long {
        if (promise.recipient.isNotEmpty()) return db.createNotification(promise)
        return -1
    }

    /**
     * Remove promise of the list.
     *
     * @param promise
     * @return
     */
    private fun removePromise(promise: Promise): Boolean {
        return listPromise.remove(promise)
    }

    /**
     * Load promises of the database in the listPromise.
     *
     * @param db
     *
     * Récupère les promesses de la base de données pour les
     * mettre dans la liste.
     */
    fun loadPromises(db: PromiseDataBase) {
        this.db = db
        listPromise = db.getAllPromises()
    }

    /**
     * Get all promise.
     *
     * @return all the promises
     */
    fun getAllPromise(): TreeSet<Promise> {
        val res: TreeSet<Promise> = TreeSet()
        for (p in listPromise) {
            res.add(p)
        }

        return res
    }


    /**
     * Get all promises of the day.
     *
     * @return the promises of the day
     */
    fun getAllPromisesOfTheDay(): TreeSet<Promise> {
        val dateTodayEvening = Date()
        dateTodayEvening.hours = 23
        dateTodayEvening.seconds = 59
        dateTodayEvening.minutes = 59
        val res = TreeSet<Promise>()
        for (promise in listPromise) {
            if (promise.dateTodo.before(dateTodayEvening) &&
                promise.dateTodo.after(Date(dateTodayEvening.time - (86400000 * 4))) &&
                promise.state != State.DONE
            ) {
                res.add(promise)
            }
        }
        return res
    }

    /**
     * Get all promises of the day category.
     *
     * @return all promises of the day by category
     *
     * Retourne toutes les promesses du jour en fonction de la catégorie.
     */
    fun getAllPromisesOfTheDayCategory(): MutableList<Promise> {
        val lP = this.getAllPromisesOfTheDay()
        val res = mutableListOf<Promise>()
        val hashMap = HashMap<Category, MutableList<Promise>>()
        for (promise in lP) {
            var liste = mutableListOf<Promise>()
            if (hashMap[promise.category] != null) {
                liste = hashMap[promise.category]!!
            }
            liste.add(promise)

            hashMap[promise.category] = liste
        }
        for (key in hashMap) {
            for (e in key.value) {
                res.add(e)
            }
        }
        return res
    }

    /**
     * Get all promises of the month.
     *
     * @param email
     * @param date
     * @return all the promises of the month
     */
    fun getAllPromisesOfTheMonth(email: String, date: Date): TreeSet<Promise> {
        return db.getAllPromisesOfTheMonth(email, date) as TreeSet<Promise>
    }

    /**
     * Get promises of the day.
     *
     * @param date
     * @return the promises of the day
     */
    fun getPromisesOfTheDay(date: Date = Date(System.currentTimeMillis())): TreeSet<Promise> {
        return db.getPromisesOfTheDay(email, date) as TreeSet<Promise>
    }

    /**
     * Get promises sorted by priority.
     *
     * @param setToSort
     * @return
     */
    fun getPromisesSortedByPriority(setToSort: Set<Promise>): Set<Promise> {
        val setSorted: TreeSet<Promise> = TreeSet { p1, p2 ->
            if (p1.state == State.DONE && p2.state != State.DONE)
                1
            else if (p2.state == State.DONE && p1.state != State.DONE)
                -1
            else {
                if (p1.priority && p2.priority) {
                    p1.compareTo(p2)
                } else if (p1.priority && !p2.priority) {
                    -1
                } else {
                    1
                }
            }
        }
        setSorted.addAll(setToSort)
        return setSorted
    }

    /**
     * Stop dnd.
     *
     * @param context
     */
    fun stopDnd(context: Activity) {
        val notifMngr = DndManager(context)
        for (promise in getAllPromise()) {
            if (promise.priority && (promise.state == State.IN_PROGRESS)) {
                return
            }
        }
        notifMngr.setRingMode(NotificationManager.INTERRUPTION_FILTER_ALL)
    }

    /**
     * Start dnd.
     *
     * @param context
     */
    fun startDnd(context: Activity) {
        val notifMngr = DndManager(context)
        notifMngr.setRingMode(NotificationManager.INTERRUPTION_FILTER_NONE)
    }

    /**
     * Get promises sorted by name.
     *
     * @param setToSort
     * @return
     */
    fun getPromisesSortedByName(setToSort: Set<Promise>): Set<Promise> {
        val setSorted: TreeSet<Promise> = TreeSet { p1, p2 ->
            if (p1.state == State.DONE && p2.state != State.DONE)
                1
            else if (p2.state == State.DONE && p1.state != State.DONE)
                -1
            else {
                when {
                    p1.title == p2.title -> p1.compareTo(p2)
                    p1.title < p2.title -> -1
                    else -> 1
                }
            }
        }
        setSorted.addAll(setToSort)
        return setSorted
    }

    /**
     * Get promises sorted by date.
     *
     * @param setToSort
     * @return
     */
    fun getPromisesSortedByDate(setToSort: Set<Promise>): Set<Promise> {
        val setSorted: TreeSet<Promise> = TreeSet { p1, p2 ->
            if (p1.state == State.DONE && p2.state != State.DONE)
                1
            else if (p2.state == State.DONE && p1.state != State.DONE)
                -1
            else {
                p1.compareTo(p2)
            }
        }
        setSorted.addAll(setToSort)
        return setSorted
    }

    /**
     * Is username exists.
     *
     * @param username
     */
    fun isUsernameExist(username: String) = db.usernameExist(username)

    /**
     * Check connection that checks if the connexion informations are correct.
     *
     * @param password
     *
     * Vérifie si les informations de connexion sont correctes.
     */
    fun checkConnection(password: String) = db.check(email, password)

    /**
     * Update user.
     *
     * @param usr
     */
    fun updateUser(usr: User, isPasswordChanged: Boolean = false) {
        val oldUsername = username
        mascot = usr.mascot
        name = usr.name
        username = usr.username
        password = usr.password
        db.updateUser(oldUsername, isPasswordChanged)
    }

    /**
     * Get search results sorted.
     *
     * @param name
     * @param choiceOfSort
     * @return
     */
    fun getSearchResultsSorted(name: String, choiceOfSort: Sort) =
        (db.getAllPromisesNameLike(name, choiceOfSort, this)) as TreeSet<Promise>

    /**
     * Update promise in the database.
     *
     * @param promise
     */
    fun updatePromise(promise: Promise) {
        db.updatePromise(email, promise)
        listAddPromise(promise)
    }

    /**
     * Delete promise of the database.
     *
     * @param promise
     */
    fun deletePromise(promise: Promise) {
        db.deletePromise(promise)
        removePromise(promise)
    }

    /**
     * Set to done that set a promise to done state.
     *
     * @param promise
     *
     * Met un promesse à l'état de finie.
     */
    fun setToDone(promise: Promise) {
        promise.state = State.DONE
        db.updatePromise(email, promise)
        val it = listPromise.iterator()
        while (it.hasNext()) {
            val p = it.next()
            if (p.id == promise.id) {
                it.remove()
            }

        }

    }

    /**
     * Update done subtask that update the state of a subtatsk to done.
     *
     * @param clickedItem
     * @param done
     *
     * Met une sous-tâche à l'état de finie.
     */
    fun updateDoneSubtask(clickedItem: Subtask) {
        db.updateSubtask(clickedItem.id, clickedItem.done)
    }

    /**
     * Remove notification of the database.
     *
     * @param id
     *
     * Retire une notification de la base de données.
     */
    fun removeNotification(id: Long) {
        if (id != -1L)
            db.deleteNotification(id)
    }

    /**
     * Get notification.
     *
     * @return
     */
    fun getNotification(): HashSet<Notification> {
        return db.getNotification()
    }
}