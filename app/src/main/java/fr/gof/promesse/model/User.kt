package fr.gof.promesse.model

import fr.gof.promesse.database.PromiseDataBase
import java.util.*

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

    /**
     * Add promise
     *
     * @param promise
     * @param db
     */
    fun addPromise(promise: Promise, db: PromiseDataBase) {
        db.addPromise(email, promise)
    }

    /**
     * Get all promise
     *
     * @param db
     * @return
     */
    fun getAllPromise(db: PromiseDataBase) : Set<Promise>{
        return db.getAllPromises(email)
    }


    /**
     * Get all promises of the day
     *
     * @param db
     * @return
     */
    fun getAllPromisesOfTheDay(db: PromiseDataBase, date: Date = Date(System.currentTimeMillis())) : Set<Promise>{
        return db.getAllPromisesOfTheDay(email, date)
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
    fun getSearchResultsSorted(name : String, choiceOfSort : Sort, db: PromiseDataBase) : Set<Promise> =
        db.getAllPromisesNameLike(name, choiceOfSort, this)

    /**
     * Update promise
     *
     * @param promise
     * @param db
     */
    fun updatePromise(promise: Promise, db: PromiseDataBase) {
        db.updatePromise(email, promise)
    }

    /**
     * Delete promise
     *
     * @param promise
     * @param db
     */
    fun deletePromise(promise : Promise, db : PromiseDataBase) {
        db.deletePromise(promise)
    }

    fun setToDone(promise : Promise, db : PromiseDataBase) {
        promise.state = State.DONE
        db.updatePromise(email, promise)
    }
}