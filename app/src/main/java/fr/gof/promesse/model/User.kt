package fr.gof.promesse.model

import fr.gof.promesse.database.PromiseDataBase
import java.util.*

data class User(var email: String, var name: String, var password: String, var mascot: Mascot){

    fun addPromise(promise: Promise, db: PromiseDataBase) {
        db.addPromise(email, promise)
    }

    fun getAllPromise(db: PromiseDataBase) : Set<Promise>{
        return db.getAllPromises(email)
    }



    fun getAllPromisesOfTheDay(db: PromiseDataBase) : Set<Promise>{
        return db.getAllPromisesOfTheDay(email)
    }

    //Tirer par priorité puis par date
    fun getPromisesSortedByPriority(db: PromiseDataBase, setToSort: Set<Promise>) : Set<Promise>{
        var setSorted : TreeSet<Promise> = TreeSet { p1, p2 ->
            if (p1.priority && p2.priority) {
                p1.compareTo(p2)
            } else if (p1.priority && !p2.priority) {
                -1
            } else {
                1
            }
        }
        setSorted.addAll(setToSort)
        return setSorted
    }

    //Fonction pour renvoyer les promesses du jour triées par priorité puis date d'exécution
    fun getListPromises(db: PromiseDataBase) : Set<Promise>{
        return getPromisesSortedByPriority(db, getAllPromisesOfTheDay(db))
    }

    //Trier par nom puis par date
    fun getPromisesSortedByName(db: PromiseDataBase, setToSort: Set<Promise>) : Set<Promise>{
        var setSorted : TreeSet<Promise> = TreeSet { p1, p2 ->
            if (p1.title == p2.title) {
                p1.compareTo(p2)
            } else if (p1.title < p2.title) {
                -1
            } else {
                1
            }
        }
        setSorted.addAll(setToSort)
        return setSorted
    }

    //Trier par date
    fun getPromisesSortedByDate(db: PromiseDataBase, setToSort: Set<Promise>) : Set<Promise>{
        var setSorted : TreeSet<Promise> = TreeSet { p1, p2 ->
            p1.compareTo(p2)
        }
        setSorted.addAll(setToSort)
        return setSorted
    }

    fun getSearchResultsSorted(name : String, choiceOfSort : Sort, db: PromiseDataBase) : Set<Promise> =
        db.getAllPromisesNameLike(name, choiceOfSort, this)

    fun updatePromise(promise: Promise, db: PromiseDataBase) {
        db.updatePromise(email, promise)
    }

    fun deletePromise(promise : Promise, db : PromiseDataBase) {
        db.deletePromise(promise)
    }

}