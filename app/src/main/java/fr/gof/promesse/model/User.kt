package fr.gof.promesse.model

import android.app.Activity
import android.app.NotificationManager
import android.util.Log
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.services.DndManager
import java.text.SimpleDateFormat
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
 * @constructor Create empty User
 */
data class User(var email: String, var name: String, var password: String, var mascot: Mascot){
private lateinit var listPromise:TreeSet<Promise>
lateinit var db : PromiseDataBase
    /**
     * Add promise
     *
     * @param promise
     * @param db
     */

    fun addPromise(promise: Promise) {
        promise.id = db.addPromise(email, promise).toInt()
        Log.d("----------------------id---------------",promise.id.toString())
        listAddPromise(promise)

    }


    private fun listAddPromise(promise: Promise) {
        var res = removePromise(promise)
        Log.d("----------------ici-------------------------------------",res.toString())
        print("coucouuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu$res")

        listPromise.add(promise)
    }

    private fun removePromise(promise: Promise) : Boolean{
        return listPromise.remove(promise)
    }

    fun loadPromises(db: PromiseDataBase){
        this.db = db
        listPromise = db.getAllPromises(email)
    }
    /**
     * Get all promise
     *
     * @param db
     * @return
     */
    fun getAllPromise() : TreeSet<Promise>{
        return listPromise.clone() as TreeSet<Promise>
    }


    /**
     * Get all promises of the day
     *
     * @param db
     * @return
     */
    fun getAllPromisesOfTheDay() : TreeSet<Promise>{
        var dateTodayEvening = Date()
        dateTodayEvening.hours = 23
        dateTodayEvening.seconds = 59
        dateTodayEvening.minutes = 59
        var res = TreeSet<Promise>()
        for (promise in listPromise) {
            if (promise.dateTodo.before(dateTodayEvening) && promise.dateTodo.after(Date(dateTodayEvening.time-(86400000 * 4))) && promise.state != State.DONE) {
                res.add(promise)
            }
        }
        return res
    }

    fun getAllPromisesOfTheDayCategory() : MutableList<Promise>{
        var lP = this.getAllPromisesOfTheDay()
        var res = mutableListOf<Promise>()
        var hashMap = HashMap<Category,MutableList<Promise>>()
        for (promise in lP){
            var liste = mutableListOf<Promise>()
            if (hashMap[promise.category] !=null){
                liste = hashMap[promise.category]!!
            }
            liste.add(promise)

            hashMap[promise.category] = liste
        }
        for (key in hashMap){
            for (e in key.value){
                res.add(e)
            }
        }
        return res
    }

    fun getAllPromisesOfTheMonth(email: String, date: Date): TreeSet<Promise>{
        return db.getAllPromisesOfTheMonth(email,date) as TreeSet<Promise>
    }

    fun getPromisesOfTheDay(date: Date = Date(System.currentTimeMillis())): TreeSet<Promise> {
        return db.getPromisesOfTheDay(email, date) as TreeSet<Promise>
    }

    /**
     * Get promises sorted by priority
     *
     * @param db
     * @param setToSort
     * @return
     *///Tirer par priorité puis par date
    fun getPromisesSortedByPriority(setToSort: Set<Promise>) : Set<Promise>{
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
    fun getListPromises() : Set<Promise>{
        return getPromisesSortedByPriority(getAllPromisesOfTheDay())
    }

    fun stopDnd(context: Activity) {
        val notifMngr = DndManager(context)
        for(promise in getAllPromise()) {
            if (promise.priority && (promise.state == State.IN_PROGRESS)) {
                return
            }
        }
        notifMngr.setRingMode(NotificationManager.INTERRUPTION_FILTER_ALL)
    }

    fun startDnd(context: Activity) {
        val notifMngr = DndManager(context)
        notifMngr.setRingMode(NotificationManager.INTERRUPTION_FILTER_NONE)
    }

    /**
     * Get promises sorted by name
     *
     * @param db
     * @param setToSort
     * @return
     *///Trier par nom puis par date
    fun getPromisesSortedByName( setToSort: Set<Promise>) : Set<Promise>{
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
    fun getPromisesSortedByDate( setToSort: Set<Promise>) : Set<Promise>{
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
    fun generatePromises(){

        for (nm in 1..10){
            var listSubTask = mutableListOf<Subtask>()
            listSubTask.add(Subtask(1000+nm,"sous tache numéro 1", false))
            listSubTask.add(Subtask(700+nm,"sous tache numéro 2", false))
            listSubTask.add(Subtask(400+nm,"sous tache numéro 3", false))
            var promesse = Promise(-1,"Promesse numero $nm" ,Category.values()[nm%5],1,State.TODO, false,
                "Ceci est la derscription de la premiere promesse, bon courage a vous pour la réaliser ! :)",false, Date(),Date(),listSubTask)
            this.addPromise(promesse)
        }

    }
    /**
     * Get search results sorted
     *
     * @param name
     * @param choiceOfSort
     * @param db
     * @return
     */
    fun getSearchResultsSorted(name: String, choiceOfSort: Sort) =
        (db.getAllPromisesNameLike(name, choiceOfSort, this)) as TreeSet<Promise>

    /**
     * Update promise
     *
     * @param promise
     * @param db
     */
    fun updatePromise(promise: Promise) {
        db.updatePromise(email, promise)
        listAddPromise(promise)
    }

    /**
     * Delete promise
     *
     * @param promise
     * @param db
     */
    fun deletePromise(promise: Promise) {
        db.deletePromise(promise)
        removePromise(promise)

    }

    fun setToDone(promise: Promise) {
        promise.state = State.DONE
        db.updatePromise(email, promise)
        var it = listPromise.iterator()
        while (it.hasNext()) {
            var p = it.next()
            if (p.id == promise.id) {
                it.remove()
            }

        }

    }

    fun updatePromiseDate(promise: Promise) {
        db.updateDate(promise)
        listAddPromise(promise)

    }

    fun updateDoneSubtask(clickedItem: Subtask, done: Boolean) {
        db.updateSubtask(clickedItem.id, clickedItem.done)
    }
}