package fr.gof.promesse.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import fr.gof.promesse.R

/**
 * Promise data base helper
 *
 * @param context
 */
class PromiseDataBaseHelper(context: Context?) :
    SQLiteOpenHelper(context, R.string.app_name.toString(), null, R.integer.database_version) {
    // création de la table Account (Utilisateur)
    private val createAccount = "CREATE TABLE Account(\n" +
            "   Email VARCHAR(320),\n" +
            "   Username VARCHAR(20) UNIQUE,\n" +
            "   Mascot VARCHAR(50) NOT NULL,\n" +
            "   Name VARCHAR(50) NOT NULL,\n" +
            "   Password VARCHAR(100) NOT NULL,\n" +
            "   PRIMARY KEY(Email)\n" +
            ");"

    // création de la table Notification rassemblant toutes les notifications recues par les utilisateurs
    // c'est à dire : chaque utilisateur peut avoir des promesses réalisées pour lui et consulter leur titre
    private val createNotification = "CREATE TABLE Notification(\n" +
            "   Id_Notification INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "   Username VARCHAR(20),\n" +
            "   Titre VARCHAR(50) NOT NULL,\n" +
            "   Read LOGICAL NOT NULL,\n" +
            "   Date_Notification DATE,\n" +
            "   Author VARCHAR(20)\n" +
            ");"

    // création de la table promise de la bdd
    private val createPromise = "CREATE TABLE Promise(\n" +
            "   Id_Promise INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "   Title VARCHAR(50) NOT NULL,\n" +
            "   Recipient VARCHAR(50),\n" +
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

    // création de la table Subtask de la bdd
    private val createSubtask = "CREATE TABLE Subtask(\n" +
            "   Id_Subtask INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "   Title VARCHAR(50) NOT NULL,\n" +
            "   Done LOGICAL NOT NULL,\n" +
            "   Id_Promise INT NOT NULL,\n" +
            "   FOREIGN KEY(Id_Promise) REFERENCES Promise(Id_Promise)\n" +
            ");"

    private val dropAccount = "DROP TABLE IF EXISTS Account;"
    private val dropPromise = "DROP TABLE IF EXISTS Promise;"
    private val dropSubtask = "DROP TABLE IF EXISTS Subtask;"
    private val dropNotification = "DROP TABLE IF EXISTS Notification;"

    //Creation base de données
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createAccount)
        db?.execSQL(createPromise)
        db?.execSQL(createSubtask)
        db?.execSQL(createNotification)
    }

    //Suppression des anciennes tables et création de nouvelles
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(dropSubtask)
        db?.execSQL(dropPromise)
        db?.execSQL(dropAccount)
        db?.execSQL(dropNotification)
        db?.execSQL(createAccount)
        db?.execSQL(createPromise)
        db?.execSQL(createSubtask)
        db?.execSQL(createNotification)
    }

}