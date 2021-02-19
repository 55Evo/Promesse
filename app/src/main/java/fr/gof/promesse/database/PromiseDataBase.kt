package fr.gof.promesse.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import fr.gof.promesse.R
import fr.gof.promesse.model.*
import java.lang.Exception
import fr.gof.promesse.model.*
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*


class PromiseDataBase (context : Context){

    val database = PromiseDataBaseHelper(context)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    //Création d'un compte
    fun createDefaultAccount(mascot : Mascot) : User {
//        val dbwritable: SQLiteDatabase = this.database.writableDatabase
//        val user = User("default@test.fr", "Monsieur", "root",mascot)
//        val values = ContentValues()
//        values.put("Email", user.email)
//        values.put("Mascot", mascot.name)
//        values.put("Name", user.name)
//        values.put("Password",user.password )
//        dbwritable.insert("Account", null, values)
//        dbwritable.close()
//        return user
        return utils.user
    }

    fun updateMascot(mascot : Mascot){

        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        val values = ContentValues()
        values.put("Mascot", mascot.name)
        dbwritable.update("Account", values,"Account.Email = '${utils.user.email}'", null)
        dbwritable.close()
        utils.user.mascot = mascot
    }

    fun updateDate(promise : Promise){
        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        val values = ContentValues()
        values.put("Date_Todo",dateFormat.format(promise.dateTodo))
        dbwritable.update("Promise", values,"Promise.Email = '${utils.user.email}' and Promise.Id_Promise = ${promise.id}", null)
        dbwritable.close()

    }


    fun deletePromise(promesse : Promise) {
        //Ouverture
        val dbwritable: SQLiteDatabase = this.database.writableDatabase

        //Suppression des sous-tâches s'il y en a
        if(promesse.subtasks != null) {
            for(sub in promesse.subtasks!!) {
                dbwritable.delete("Subtask","Subtask.Id_Subtask = ${sub.id}", null)
            }
        }
        //Suppression de la promesse
        dbwritable.delete("Promise","Promise.Id_Promise = ${promesse.id}", null)

        //Fermeture
        dbwritable.close()
    }

    fun addPromise(email : String, promise : Promise) {
        //Ouverture
        val dbwritable: SQLiteDatabase = this.database.writableDatabase

        //Ajout des valeurs
        val values = ContentValues()
        values.put("Email", email)
        values.put("Title", promise.title)
        values.put("Duration", promise.duration)
        values.put("State", promise.state.toString())
        values.put("Priority", if(promise.priority) 1 else 0)
        values.put("Professional", if(promise.professional) 1 else 0)
        values.put("Date_Creation", dateFormat.format(promise.dateCreation))
        values.put("Date_Todo", dateFormat.format(promise.dateTodo))
        values.put("Description", promise.description)

        val id = dbwritable.insert("Promise", null, values)

        //Ajout des sous-tâches
        if(promise.subtasks != null) {
            for (sub in promise.subtasks!!) {
                println("oo???")
                val subvalues = ContentValues()
                subvalues.put("Id_Promise", id)
                subvalues.put("Title", sub.title)
                subvalues.put("Done", sub.done)
                dbwritable.insert("Subtask", null, subvalues)
            }
        }
        //Fermeture
        dbwritable.close()
    }

    fun getPromise(curs: Cursor, dbreadable: SQLiteDatabase): Set<Promise>{
        var promiseList = HashSet<Promise>()
        try {
            while (curs.moveToNext()) {
                val id = curs.getInt(curs.getColumnIndexOrThrow("Id_Promise"))
                val title = curs.getString(curs.getColumnIndexOrThrow("Title"))
                val duration = curs.getInt(curs.getColumnIndexOrThrow("Duration"))
                val state = when (curs.getString(curs.getColumnIndexOrThrow("State"))) {
                    "TODO" -> State.TODO
                    "IN_PROGRESS" -> State.IN_PROGRESS
                    "DONE" -> State.DONE
                    else -> throw IllegalArgumentException()
                }
                val priority = curs.getInt(curs.getColumnIndexOrThrow("Priority")) > 0
                val description = curs.getString(curs.getColumnIndexOrThrow("Description"))
                val professional = curs.getInt(curs.getColumnIndexOrThrow("Professional")) > 0
                val dateCreation = dateFormat.parse(curs.getString(curs.getColumnIndexOrThrow("Date_Creation")))
                val dateTodo = dateFormat.parse(curs.getString(curs.getColumnIndexOrThrow("Date_Todo")))

                val subtasks = mutableListOf<Subtask>()
                val curs2 = dbreadable.query("Subtask", arrayOf("Id_Subtask", "Title", "Done"), "Id_Promise = $id", null, null, null, null)
                try {
                    println("aa??")
                    while(curs2.moveToNext()) {
                        subtasks.add(Subtask(curs2.getInt(curs2.getColumnIndexOrThrow("Id_Subtask")), curs2.getString(curs2.getColumnIndexOrThrow("Title")), curs2.getInt(curs2.getColumnIndexOrThrow("Done"))>0))
                    }
                } finally {
                    curs2.close()
                }
                promiseList.add(Promise(id, title,duration,state,priority,description,professional,dateCreation,dateTodo, if (subtasks.isNotEmpty()) subtasks else null))

            }
        } finally {
            curs.close()
        }

        return promiseList
    }


    fun getAllPromises(email : String = "default@test.fr") : Set<Promise> {
        val dbreadable : SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf("Id_Promise", "Title", "Duration", "State", "Priority", "Description", "Professional", "Date_Creation", "Date_Todo")
        val select = arrayOf(email)
        val curs: Cursor = dbreadable.query("Promise", col, "Email = ?", select, null, null, null)
        return getPromise(curs, dbreadable)
    }

    @JvmOverloads
    fun getAllPromisesNameLike(name : String, choiceOfSort : Sort, user: User) : Set<Promise> {
        val dbreadable : SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf("Id_Promise", "Title", "Duration", "State", "Priority", "Description", "Professional", "Date_Creation", "Date_Todo")
        val select = arrayOf("%$name%")
        val curs: Cursor = dbreadable.query("Promise", col, "Title LIKE ?", select, null, null, null)
        return when(choiceOfSort){
            Sort.DATE -> user.getPromisesSortedByDate(this, getPromise(curs, dbreadable))
            Sort.NAME -> user.getPromisesSortedByName(this, getPromise(curs, dbreadable))
            Sort.PRIORITY -> user.getPromisesSortedByPriority(this, getPromise(curs, dbreadable))
        }
    }

    fun getAllPromisesOfTheDay(email: String): Set<Promise> { // récupère les promesses de la journée et celles des trois jours précédents si elles ne sont pas finies
        val dbreadable : SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf("Id_Promise", "Title", "Duration", "State", "Priority", "Description", "Professional", "Date_Creation", "Date_Todo")
        val select = arrayOf(email)
        //DATE('now','-1 day') Retourne la date d'hier sous format yyyy-mm-dd
        //DATE(Date_Todo) Retourne la date de Date_Todo sous format yyyy-mm-dd
        val curs: Cursor = dbreadable.query("Promise", col,
            "((DATE(Date_Todo) = DATE('now','-1 day') AND State <> 'DONE') \n" +
                "OR (DATE(Date_Todo) = DATE('now','-2 day') AND State <> 'DONE') \n" +
                "OR (DATE(Date_Todo) = DATE('now','-3 day') AND State <> 'DONE') \n" +
                "OR DATE(Date_Todo) = DATE('now') AND State <> 'DONE') " +
                "AND Email = ?",
            select, null, null, null)
        return getPromise(curs, dbreadable)
    }

    fun updatePromise(email : String, promise: Promise) {

        val dbwritable : SQLiteDatabase = this.database.writableDatabase
        val values = ContentValues()

        values.put("Title", promise.title)
        values.put("Duration", promise.duration)
        values.put("State", promise.state.toString())
        values.put("Priority", if(promise.priority) 1 else 0)
        values.put("Professional", if(promise.professional) 1 else 0)
        values.put("Date_Creation", dateFormat.format(promise.dateCreation))
        values.put("Date_Todo", dateFormat.format(promise.dateTodo))
        values.put("Description", promise.description)

        dbwritable.update("Promise", values,"Email = '$email' AND Id_Promise = '${promise.id}'", null)
        dbwritable.close()

    }

}