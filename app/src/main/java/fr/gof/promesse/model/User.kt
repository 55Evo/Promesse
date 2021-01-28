package fr.gof.promesse.model

import fr.gof.promesse.database.PromiseDataBase

data class User (var email : String, var name : String, var password : String, var mascot : Mascot){

    fun addPromise(promise : Promise, db : PromiseDataBase) {
        db.addPromise(email, promise)
    }

    fun getAllPromise(db : PromiseDataBase) : Set<Promise>{
        return db.getAllPromises(email)
    }
}