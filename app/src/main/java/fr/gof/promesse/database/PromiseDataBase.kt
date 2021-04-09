package fr.gof.promesse.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.model.*
import java.lang.IllegalArgumentException
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*


/**
 * Promise data base
 *
 * @constructor
 *
 * @param context
 *
 * Classe permettant les échanges avec la BDD
 *
 *
 */
class PromiseDataBase(context: Context) {
    val database = PromiseDataBaseHelper(context)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    /**
     * Create account
     *
     * @param user
     * @return
     *
     * Fonction permettant de rajouter un utilisateur à la BDD en fonction d'un user passé
     * en paramètre
     */
    fun createAccount(user: User) {
        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        val values = ContentValues()
        values.put("Email", user.email)
        values.put("Username", user.username)
        values.put("Mascot", user.mascot.name)
        values.put("Name", user.name)
        values.put("Password", sha1(user.password))
        dbwritable.insert("Account", null, values)
        dbwritable.close()
    }

    /**
     * Create notification
     *
     * @param promise
     * @return l'id de la Notification
     *
     * Rajoute à la base de donnée l'utilisateur pour qui l'on réalise une promesse si il est présent
     * dans celle-ci
     */
    fun createNotification(promise: Promise): Long {
        if (!usernameExist(promise.recipient)) return -1L
        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        val values = ContentValues()
        values.put("Username", promise.recipient)
        values.put("Titre", promise.title)
        values.put("Read", 0)
        values.put("Author", user.username)
        values.put("Date_Notification", dateFormat.format(System.currentTimeMillis()))
        val id = dbwritable.insert("Notification", null, values)
        dbwritable.close()
        return id
    }

    /**
     * Update mascot
     *
     * @param mascot
     *
     * Met à jour la mascotte d'un utilisateur dans la BDD
     */
    fun updateMascot(mascot: Mascot) {

        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        val values = ContentValues()
        values.put("Mascot", mascot.name)
        dbwritable.update("Account", values, "Account.Email = '${user.email}'", null)
        dbwritable.close()
        user.mascot = mascot
    }

    /**
     * Update category
     *
     * @param categorie
     * @param promesse
     *
     * Met à jour la catégorie d'une promesse dans la bdd
     */
    fun updateCategory(categorie: Category, promesse: Promise) {

        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        val values = ContentValues()
        values.put("Category", categorie.name)
        dbwritable.update("Promise", values, "Promise.Id_promise = '${promesse.id}'", null)
        dbwritable.close()

    }

    /**
     * Update date
     *
     * @param promise
     * Met à jour la date d'une promesse passée en paramètre dans la BDD
     */
    fun updateDate(promise: Promise) {
        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        val values = ContentValues()
        values.put("Date_Todo", dateFormat.format(promise.dateTodo))
        dbwritable.update(
            "Promise",
            values,
            "Promise.Email = '${user.email}' and Promise.Id_Promise = ${promise.id}",
            null
        )
        dbwritable.close()

    }

    /**
     * Delete promise
     *
     * @param promesse
     * Supprimer une promesse de la base de données ainsi que ses sous-taches si elle en comporte
     */
    fun deletePromise(promesse: Promise) {
        //Ouverture
        val dbwritable: SQLiteDatabase = this.database.writableDatabase

        //Suppression des sous-tâches s'il y en a
        if (promesse.subtasks != null) {
            for (sub in promesse.subtasks!!) {
                dbwritable.delete("Subtask", "Subtask.Id_Subtask = ${sub.id}", null)
            }
        }
        //Suppression de la promesse
        dbwritable.delete("Promise", "Promise.Id_Promise = ${promesse.id}", null)

        //Fermeture
        dbwritable.close()
    }

    /**
     * Add promise
     *
     * @param email
     * @param promise
     * Ajout d'une promesse à la base de donnée
     */
    fun addPromise(email: String, promise: Promise): Long {
        //Ouverture
        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        //Ajout des valeurs
        val values = ContentValues()
        promiseToValues(values, email, promise)
        val id = dbwritable.insert("Promise", null, values)
        //Ajout des sous-tâches

        for (sub in promise.subtasks) {
            val subvalues = ContentValues()
            subvalues.put("Id_Promise", id)
            subvalues.put("Title", sub.title)
            subvalues.put("Done", sub.done)
            dbwritable.insert("Subtask", null, subvalues)
        }

        //Fermeture
        dbwritable.close()
        return id
    }

    /**
     * Promise to values
     *
     * @param values
     * @param email
     * @param promise
     *
     * Fonction privée permettant de remplir tout les champs de la bdd avec les informations de la promesse
     */
    private fun promiseToValues(
        values: ContentValues,
        email: String,
        promise: Promise
    ) {
        values.put("Email", email)
        values.put("Title", promise.title)
        values.put("Recipient", promise.recipient)
        values.put("Category", promise.category.nom)
        values.put("Duration", promise.duration)
        values.put("State", promise.state.toString())
        values.put("Priority", if (promise.priority) 1 else 0)
        values.put("Professional", if (promise.professional) 1 else 0)
        values.put("Date_Creation", dateFormat.format(promise.dateCreation))
        values.put("Date_Todo", dateFormat.format(promise.dateTodo))
        values.put("Description", promise.description)
    }

    /**
     * Get promise
     *
     * @param curs
     * @param dbreadable
     * @return
     *
     * Fonction permettant de récupérer de la base de donnée une promesse avec tout ce qui la compose
     * (Titre, date, sous-taches...)
     */
    fun getPromise(curs: Cursor, dbreadable: SQLiteDatabase): TreeSet<Promise> {
        var promiseList = TreeSet<Promise>()
        try {
            while (curs.moveToNext()) {
                val id = curs.getInt(curs.getColumnIndexOrThrow("Id_Promise"))
                val title = curs.getString(curs.getColumnIndexOrThrow("Title"))
                val recipient = curs.getString(curs.getColumnIndexOrThrow("Recipient"))
                val category = curs.getString(curs.getColumnIndexOrThrow("Category"))
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
                val dateCreation =
                    dateFormat.parse(curs.getString(curs.getColumnIndexOrThrow("Date_Creation")))
                val dateTodo =
                    dateFormat.parse(curs.getString(curs.getColumnIndexOrThrow("Date_Todo")))

                val subtasks = mutableListOf<Subtask>()
                val curs2 = dbreadable.query(
                    "Subtask",
                    arrayOf("Id_Subtask", "Title", "Done"),
                    "Id_Promise = $id",
                    null,
                    null,
                    null,
                    null
                )
                try {
                    while (curs2.moveToNext()) {
                        subtasks.add(
                            Subtask(
                                curs2.getInt(curs2.getColumnIndexOrThrow("Id_Subtask")),
                                curs2.getString(curs2.getColumnIndexOrThrow("Title")),
                                curs2.getInt(curs2.getColumnIndexOrThrow("Done")) > 0
                            )
                        )
                    }
                } finally {
                    curs2.close()
                }
                promiseList.add(
                    Promise(
                        id,
                        title,
                        recipient,
                        Category.valueOf(category.toUpperCase()),
                        duration,
                        state,
                        priority,
                        description,
                        professional,
                        dateCreation,
                        dateTodo,
                        subtasks
                    )
                )

            }
        } finally {
            curs.close()
        }

        return promiseList
    }


    /**
     * Get all promises
     *
     * @param email
     * @return
     *
     * Fonction permettant d'extraire de la BDD l'ensemble des promesses appartenant à un utilisateur
     */
    fun getAllPromises(): TreeSet<Promise> {
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf(
            "Id_Promise",
            "Title",
            "Recipient",
            "Category",
            "Duration",
            "State",
            "Priority",
            "Description",
            "Professional",
            "Date_Creation",
            "Date_Todo"
        )
        val select = arrayOf(user.email)
        val curs: Cursor = dbreadable.query("Promise", col, "Email = ?", select, null, null, null)
        return getPromise(curs, dbreadable)
    }

    /**
     * Get all promises name like
     *
     * @param name
     * @param choiceOfSort
     * @param user
     * @return
     */
    @JvmOverloads
    fun getAllPromisesNameLike(name: String, choiceOfSort: Sort, user: User): Set<Promise> {
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf(
            "Id_Promise",
            "Title",
            "Recipient",
            "Category",
            "Duration",
            "State",
            "Priority",
            "Description",
            "Professional",
            "Date_Creation",
            "Date_Todo"
        )
        val select = arrayOf("%$name%", user.email)
        val curs: Cursor =
            dbreadable.query("Promise", col, "Title LIKE ? AND Email=?", select, null, null, null)
        return when (choiceOfSort) {
            Sort.DATE -> user.getPromisesSortedByDate(getPromise(curs, dbreadable))
            Sort.NAME -> user.getPromisesSortedByName(getPromise(curs, dbreadable))
            Sort.PRIORITY -> user.getPromisesSortedByPriority(getPromise(curs, dbreadable))
        }
    }

    /**
     * Get all promises of the day
     *
     * @param email
     * @return
     *
     * Récupère toute les promesses des trois derniers jours non réalisées par l'utilisateur
     *
     */
    fun getAllPromisesOfTheDay(
        email: String,
        date: Date = Date(System.currentTimeMillis())
    ): Set<Promise> { // récupère les promesses de la journée et celles des trois jours précédents si elles ne sont pas finies
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val dateToDo = formatter.format(date)
        //Execution requête
        val col = arrayOf(
            "Id_Promise",
            "Title",
            "Recipient",
            "Category",
            "Duration",
            "State",
            "Priority",
            "Description",
            "Professional",
            "Date_Creation",
            "Date_Todo"
        )
        val select = arrayOf(email)
        //DATE('now','-1 day') Retourne la date d'hier sous format yyyy-mm-dd
        //DATE(Date_Todo) Retourne la date de Date_Todo sous format yyyy-mm-dd
        val curs: Cursor = dbreadable.query(
            "Promise", col,
            "((DATE(Date_Todo) = DATE('$dateToDo','-1 day') AND State <> 'DONE') \n" +
                    "OR (DATE(Date_Todo) = DATE('$dateToDo','-2 day') AND State <> 'DONE') \n" +
                    "OR (DATE(Date_Todo) = DATE('$dateToDo','-3 day') AND State <> 'DONE') \n" +
                    "OR DATE(Date_Todo) = DATE('$dateToDo') AND State <> 'DONE') " +
                    "AND Email = ?",
            select, null, null, null
        )
        return getPromise(curs, dbreadable)
    }

    /**
     * Update promise
     *
     * @param email
     * @param promise
     *
     * Met à jour une promesse précise d'un utilisateur
     *
     */
    fun updatePromise(email: String, promise: Promise) {

        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        val values = ContentValues()
        promiseToValues(values, email, promise)
        dbwritable.update(
            "Promise",
            values,
            "Email = '$email' AND Id_Promise = '${promise.id}'",
            null
        )
        dbwritable.delete("Subtask", "Id_Promise = '${promise.id}'", null)
        for (sub in promise.subtasks) {
            val subvalues = ContentValues()
            subvalues.put("Id_Promise", promise.id)
            subvalues.put("Title", sub.title)
            subvalues.put("Done", sub.done)
            dbwritable.insert("Subtask", null, subvalues)
        }
        dbwritable.close()
    }

    /**
     * Email or username exists
     *
     * @param usernameOrEmail
     * @return boolean
     * Fonction dans laquelle on passe soit un email soit un utilisateur en paramètre.
     * renvoie true si la bdd comporte cet email / username et false si il ne le contient pas
     *
     */
    fun emailOrUsernameExists(usernameOrEmail: String) =
        if (usernameOrEmail.contains("@")) {
            emailExist(usernameOrEmail)
        } else {
            usernameExist(usernameOrEmail)
        }

    /**
     * Email exist
     *
     * @param email
     * @return
     *
     * Renvoie true si l'email est présent dans la bdd
     */
    fun emailExist(email: String): Boolean {
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf("Email")
        val select = arrayOf(email)

        val curs: Cursor = dbreadable.query(
            "Account", col,
            "Email = ?",
            select, null, null, null
        )
        //Si il y en a, retourne true
        return curs.count != 0
    }

    /**
     * Username exist
     *
     * @param username
     * @return boolean
     *
     * renvoie true si l'username (pseudo) est présent dans la base de données
     */
    fun usernameExist(username: String): Boolean {
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf("Username")
        val select = arrayOf(username)

        val curs: Cursor = dbreadable.query(
            "Account", col,
            "Username = ?",
            select, null, null, null
        )
        //Si il y en a, retourne true
        return curs.count != 0
    }

    /**
     * User is empty
     *
     * @return boolean
     *
     *renvoie false si le mail de l'utilisateur n'est pas présent en base de données
     */
    fun userIsEmpty(): Boolean {
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf("Email")

        val curs: Cursor = dbreadable.query(
            "Account", col,
            null,
            null, null, null, null
        )
        //Si il y en a, retourne false
        return curs.count == 0
    }

    /**
     * Sha1
     *
     * @param input
     *
     * Fonction à appeler pour hasher le mdp d'un utilisateur
     */
    fun sha1(input: String) = hashString(input, "SHA-1")

    /**
     * Hash string
     *
     * @param input
     * @param algorithm
     * @return string -> mot de passe haché
     *
     * Fonction prenant en paramètre un string et retournant le hash de ce string en "sha-1" dans
     * notre cas mais marcherait en "md5" par exemple
     */
    private fun hashString(input: String, algorithm: String): String {
        return MessageDigest.getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }

    /**
     * Check username
     *
     * @param email
     * @param username
     * @return boolean -> renvoie true si la base de donnée contient bien un utilisateur
     * avec le mot de passe présent en paramètre (il regardera soit l'username si on se connecte
     * avec l'username (pseudo) soit le mail si l'on se connecte par mail
     */
    fun check(usernameOfEmail: String, password: String): Boolean {
        var password = sha1(password)
        return if (usernameOfEmail.contains("@")) {
            checkEmail(usernameOfEmail, password)
        } else {
            checkUsername(usernameOfEmail, password)
        }
        return false
    }

    /**
     * Check email
     *
     * @param email
     * @param password
     * @return boolean
     *Fonction renvoyant -> true si la bdd contient un utilisateur avec ce mail et mdp et false sinon
     */
    private fun checkEmail(email: String, password: String): Boolean {
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf("Email")
        val select = arrayOf(email, password)
        val curs: Cursor = dbreadable.query(
            "Account", col,
            "Email = ? AND Password = ?",
            select, null, null, null
        )
        //Si il y en a, retourne true
        return curs.count != 0
    }

    /**
     * Check username
     *
     * @param username
     * @param password
     * @return boolean
     * Fonction renvoyant -> true si la bdd contient un utilisateur avec cet username et mdp et false sinon
     */
    private fun checkUsername(username: String, password: String): Boolean {
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf("Email")
        val select = arrayOf(username, password)
        val curs: Cursor = dbreadable.query(
            "Account", col,
            "Username = ? AND Password = ?",
            select, null, null, null
        )
        //Si il y en a, retourne true
        return curs.count != 0
    }

    /**
     * Delete notification
     *
     * @param id
     * Fonction permettant de supprimer une notification en fonction de son id
     */
    fun deleteNotification(id: Long) {
        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        dbwritable.delete("Notification", "Notification.Id_Notification = ${id}", null)
        dbwritable.close()

    }

    /**
     * Get user
     *
     * @param usernameOrEmail
     *
     * Fonction permettant de récupérer un utilisateur en fonction de son mail ou de son username
     * au bon vouloir de l'utilisateur
     */
    fun getUser(usernameOrEmail: String) =
        if (usernameOrEmail.contains("@")) {
            getUserByEmail(usernameOrEmail)
        } else {
            getUserByUsername(usernameOrEmail)
        }

    /**
     * Get user by email
     *
     * @param email
     * @return User
     *
     * Renvoie l'utilisateur de la base de donnée correspondant à l'email passé en paramètre
     */
    private fun getUserByEmail(email: String): User {
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf("Email", "Username", "Name", "Mascot")
        val select = arrayOf(email)
        val curs: Cursor = dbreadable.query(
            "Account", col,
            "Email = ? ",
            select, null, null, null
        )
        curs.moveToFirst()
        //Si il y en a, retourne true
        return User(
            curs.getString(curs.getColumnIndexOrThrow("Email")),
            curs.getString(curs.getColumnIndexOrThrow("Username")),
            curs.getString(curs.getColumnIndexOrThrow("Name")),
            "",
            Mascot.valueOf(curs.getString(curs.getColumnIndexOrThrow("Mascot")))
        )
    }

    /**
     * Get user by username
     *
     * @param email
     * @return User
     *
     * Renvoie un utilisateur à partir d'un username (pseudo)
     */
    private fun getUserByUsername(username: String): User {
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        //Execution requête
        val col = arrayOf("Email", "Username", "Name", "Mascot")
        val select = arrayOf(username)
        val curs: Cursor = dbreadable.query(
            "Account", col,
            "Username = ? ",
            select, null, null, null
        )
        curs.moveToFirst()
        //Si il y en a, retourne true
        return User(
            curs.getString(curs.getColumnIndexOrThrow("Email")),
            curs.getString(curs.getColumnIndexOrThrow("Username")),
            curs.getString(curs.getColumnIndexOrThrow("Name")),
            "",
            Mascot.valueOf(curs.getString(curs.getColumnIndexOrThrow("Mascot")))
        )
    }

    /**
     * Get all promises of the month
     *
     * @param email
     * @param date
     * @return
     *
     * Fonction utilisée dans le calendrier pour récupérer toute les promesse d'un mois précis récupéré
     * à partir d'une date passée en paramètre
     */
    fun getAllPromisesOfTheMonth(email: String, date: Date): Set<Promise> {
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val dateToDo = formatter.format(date)
        //Execution requête
        val col = arrayOf(
            "Id_Promise",
            "Title",
            "Recipient",
            "Category",
            "Duration",
            "State",
            "Priority",
            "Description",
            "Professional",
            "Date_Creation",
            "Date_Todo"
        )
        val select = arrayOf(email)
        //DATE('now','-1 day') Retourne la date d'hier sous format yyyy-mm-dd
        //DATE(Date_Todo) Retourne la date de Date_Todo sous format yyyy-mm-dd
        val curs: Cursor = dbreadable.query(
            "Promise", col,
            "strftime('%Y %m', Date_Todo) = strftime('%Y %m', DATE('$dateToDo')) AND State <> 'DONE' \n" +
                    "AND Email = ?",
            select, null, null, null
        )
        return getPromise(curs, dbreadable)
    }

    /**
     * Get promises of the day
     *
     * @param email
     * @param date
     * @return
     * Fonction premettant de récupérer de la bdd toutes les promesses du jour. Cette fonction est utilisée
     * dans le calendrier afin d'afficher les promesses à réaliser pour un jour précis
     */
    fun getPromisesOfTheDay(
        email: String,
        date: Date
    ): Set<Promise> { // récupère les promesses de la journée et celles des trois jours précédents si elles ne sont pas finies
        val dbreadable: SQLiteDatabase = this.database.readableDatabase
        val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val dateToDo = formatter.format(date)
        //Execution requête
        val col = arrayOf(
            "Id_Promise",
            "Title",
            "Recipient",
            "Category",
            "Duration",
            "State",
            "Priority",
            "Description",
            "Professional",
            "Date_Creation",
            "Date_Todo"
        )
        val select = arrayOf(email)
        //DATE('now','-1 day') Retourne la date d'hier sous format yyyy-mm-dd
        //DATE(Date_Todo) Retourne la date de Date_Todo sous format yyyy-mm-dd
        val curs: Cursor = dbreadable.query(
            "Promise", col,
            "DATE(Date_Todo) = DATE('$dateToDo') AND State <> 'DONE' \n" +
                    "AND Email = ?",
            select, null, null, null
        )
        return getPromise(curs, dbreadable)
    }

    /**
     * Update subtask
     *
     * @param id
     * @param done
     * Met à jour une sous tache dans la bdd en la terminant ou non (done -> true ou false)
     */
    fun updateSubtask(id: Int, done: Boolean) {
        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        val values = ContentValues()
        values.put("Done", done)
        dbwritable.update("Subtask", values, "Id_Subtask = '$id'", null)
        dbwritable.close()
    }

    /**
     * Update user
     *
     * @param oldUsername ancien username à mettre à jour dans la table Notification
     * Fonction permettant de mettre à jour dans la base de donnée un utilisateur précis
     * Lors de la mise à jour d'un utilisateur il est nécessaire de noter la nécessité de modifier
     * aussi la table Notification afin de changer le destinataire d'une promesse avec le nouvel username
     *
     */
    fun updateUser(oldUsername: String) {
        val dbwritable: SQLiteDatabase = this.database.writableDatabase
        val values = ContentValues()
        values.put("Name", user.name)
        values.put("Username", user.username)
        values.put("Password", sha1(user.password))
        values.put("Mascot", user.mascot.name)
        val valuesNotifications = ContentValues()
        valuesNotifications.put("Username", user.username)
        dbwritable.update("Account", values, "Email = '${user.email}'", null)
        dbwritable.update("Notification", valuesNotifications, "Username = '${oldUsername}'", null)
        dbwritable.close()
    }

    /**
     * Get notification
     *
     * @return HashSet<Notifications>
     * Fonction permettant de retourner la liste de toutes les Noptifications (promesse réalisées à son effigie)
     * de l'utilisateur de l'application
     */
    fun getNotification(): HashSet<Notification> {
        val dbreadable: SQLiteDatabase = this.database.readableDatabase

        //Execution requête
        val col = arrayOf("Username", "Titre", "Date_Notification", "Read", "Author")
        val select = arrayOf(user.username)
        val curs: Cursor = dbreadable.query(
            "Notification", col,
            "Username = ?",
            select, null, null, null
        )
        var notifList = HashSet<Notification>()
        try {
            while (curs.moveToNext()) {
                val username = curs.getString(curs.getColumnIndexOrThrow("Username"))
                val titre = curs.getString(curs.getColumnIndexOrThrow("Titre"))
                val date =
                    dateFormat.parse(curs.getString(curs.getColumnIndexOrThrow("Date_Notification")))
                val read = curs.getInt(curs.getColumnIndexOrThrow("Username")) == 1
                val author = curs.getString(curs.getColumnIndexOrThrow("Author"))
                notifList.add(Notification(username, author, date, titre, read))
            }
        } finally {
            curs.close()
        }
        return notifList
    }
}