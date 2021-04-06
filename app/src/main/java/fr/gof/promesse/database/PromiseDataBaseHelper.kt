package fr.gof.promesse.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import fr.gof.promesse.R

/**
 * Promise data base helper
 *
 * @constructor
 *
 * @param context
 */
class PromiseDataBaseHelper(context: Context?) : SQLiteOpenHelper(context, R.string.app_name.toString(), null, R.integer.database_version) {

    val createAccount = "CREATE TABLE Account(\n" +
            "   Email VARCHAR(320),\n" +
            "   Username VARCHAR(20) UNIQUE,\n" +
            "   Mascot VARCHAR(50) NOT NULL,\n" +
            "   Name VARCHAR(50) NOT NULL,\n" +
            "   Password VARCHAR(100) NOT NULL,\n" +
            "   PRIMARY KEY(Email)\n" +
            ");"

    val createPromise = "CREATE TABLE Promise(\n" +
            "   Id_Promise INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "   Title VARCHAR(50) NOT NULL,\n" +
            "   Category VARCHAR(50) NOT NULL,\n" +
            "   Duration INT,\n" +
            "   State CHAR(10) NOT NULL,\n" +
            "   Priority LOGICAL NOT NULL,\n" +
            "   Description VARCHAR(250),\n" +
            "   Professional LOGICAL NOT NULL,\n" +
            "   Date_Creation DATETIME NOT NULL,\n" +
            "   Date_Todo DATE,\n" +
            "   Email VARCHAR(320) NOT NULL,\n" +
            "   FOREIGN KEY(Email) REFERENCES Account(Email)\n" +
            ");"
    val createSubtask = "CREATE TABLE Subtask(\n" +
            "   Id_Subtask INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "   Title VARCHAR(50) NOT NULL,\n" +
            "   Done LOGICAL NOT NULL,\n" +
            "   Id_Promise INT NOT NULL,\n" +
            "   FOREIGN KEY(Id_Promise) REFERENCES Promise(Id_Promise)\n" +
            ");"

    val dropAccount = "DROP TABLE IF EXISTS Account;"
    val dropPromise = "DROP TABLE IF EXISTS Promise;"
    val dropSubtask = "DROP TABLE IF EXISTS Subtask;"

    //Creation base de données
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createAccount)
        db?.execSQL(createPromise)
        db?.execSQL(createSubtask)
    }

    //Suppression des anciennes tables et création de nouvelles
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(dropSubtask)
        db?.execSQL(dropPromise)
        db?.execSQL(dropAccount)
        db?.execSQL(createAccount)
        db?.execSQL(createPromise)
        db?.execSQL(createSubtask)
    }

}