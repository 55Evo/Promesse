package fr.gof.promesse.model

import android.util.Log
import fr.gof.promesse.database.PromiseDataBase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

/**
 * User
 *
 * @property email
 * @property name
 * @property password
 * @property mascot
 * @constructor Create empty User
 */
data class User(var email: String, var name: String, var password: String, var mascot: Mascot){
lateinit var listPromise:MutableSet<Promise>
    /**
     * Add promise
     *
     * @param promise
     * @param db
     */
    fun addPromise(promise: Promise, db: PromiseDataBase) {

        promise.id = db.addPromise(email, promise).toInt()
        listAddPromise(promise)

    }

    private fun listAddPromise(promise: Promise) {
        removePromise(promise)
        listPromise.add(promise)
    }

    private fun removePromise(promise: Promise) {
        listPromise.remove(promise)
    }

    fun loadPromises(db: PromiseDataBase){
        listPromise = db.getAllPromises(email).toMutableSet()
    }
    /**
     * Get all promise
     *
     * @param db
     * @return
     */
    fun getAllPromise() : Set<Promise>{
        return listPromise
    }


    /**
     * Get all promises of the day
     *
     * @param db
     * @return
     */
    fun getAllPromisesOfTheDay(db: PromiseDataBase) : Set<Promise>{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        var res = HashSet<Promise>()
        for (promise in listPromise) {
            if (promise.dateTodo !=null){
            if (promise.dateTodo.time < System.currentTimeMillis() && promise.dateTodo.time >System.currentTimeMillis()-(86400000 * 3+1) && promise.state != State.DONE) {
                res.add(promise)
            }
            }
        }


        return res
    }

    /**
     * Get promises sorted by priority
     *
     * @param db
     * @param setToSort
     * @return
     *///Tirer par priorité puis par date
    fun getPromisesSortedByPriority(db: PromiseDataBase, setToSort: Set<Promise>) : Set<Promise>{
        var setSorted : TreeSet<Promise> = TreeSet { p1, p2 ->
            if(p1.state ==  State.DONE && p2.state != State.DONE)
                1
            else if(p2.state ==  State.DONE && p1.state != State.DONE)
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
     * Get list promises
     *
     * @param db
     * @return
     *///Fonction pour renvoyer les promesses du jour triées par priorité puis date d'exécution
    fun getListPromises(db: PromiseDataBase) : Set<Promise>{
        return getPromisesSortedByPriority(db, getAllPromisesOfTheDay(db))
    }

    /**
     * Get promises sorted by name
     *
     * @param db
     * @param setToSort
     * @return
     *///Trier par nom puis par date
    fun getPromisesSortedByName(db: PromiseDataBase, setToSort: Set<Promise>) : Set<Promise>{
        var setSorted : TreeSet<Promise> = TreeSet { p1, p2 ->
            if(p1.state ==  State.DONE && p2.state != State.DONE)
                1
            else if(p2.state ==  State.DONE && p1.state != State.DONE)
                -1
            else {
                if (p1.title == p2.title) {
                    p1.compareTo(p2)
                } else if (p1.title < p2.title) {
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
     * Get promises sorted by date
     *
     * @param db
     * @param setToSort
     * @return
     *///Trier par date
    fun getPromisesSortedByDate(db: PromiseDataBase, setToSort: Set<Promise>) : Set<Promise>{
        var setSorted : TreeSet<Promise> = TreeSet { p1, p2 ->
            if(p1.state ==  State.DONE && p2.state != State.DONE)
                1
            else if(p2.state ==  State.DONE && p1.state != State.DONE)
                -1
            else {
                p1.compareTo(p2)
            }
        }
        setSorted.addAll(setToSort)
        return setSorted
    }

    /**
     * Get search results sorted
     *
     * @param name
     * @param choiceOfSort
     * @param db
     * @return
     */
    fun getSearchResultsSorted(name: String, choiceOfSort: Sort, db: PromiseDataBase) : Set<Promise> =
        db.getAllPromisesNameLike(name, choiceOfSort, this)

    /**
     * Update promise
     *
     * @param promise
     * @param db
     */
    fun updatePromise(promise: Promise, db: PromiseDataBase) {
        db.updatePromise(email, promise)
        listAddPromise(promise)
    }

    /**
     * Delete promise
     *
     * @param promise
     * @param db
     */
    fun deletePromise(promise: Promise, db: PromiseDataBase) {
        db.deletePromise(promise)
        removePromise(promise)

    }

    fun setToDone(promise: Promise, db: PromiseDataBase) {
        promise.state = State.DONE
        db.updatePromise(email, promise)
        var it = listPromise.iterator()
        while (it.hasNext()) {
            var p = it.next()
            Log.d("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww",p.title)

            if (p.id == promise.id) {
                Log.d("--------------------------------------------oooooooooooooooooooooooooooooooooooooooo--",
                    p.id.toString())
                it.remove()
            }

        }

    }

    fun updatePromiseDate(promise: Promise,db: PromiseDataBase) {
        db.updateDate(promise)
        listAddPromise(promise)

    }
}