package fr.gof.promesse

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrInterface
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.adapter.NotificationAdapter
import fr.gof.promesse.database.PromiseDataBase

/**
 * Profile activity
 *
 * @constructor Create empty Profile activity
 */
class ProfileActivity : AppCompatActivity() {
    lateinit var adapter: NotificationAdapter
    lateinit var recyclerView: RecyclerView
    val promiseDataBase = PromiseDataBase(this)
    private lateinit var slidr: SlidrInterface
    private lateinit var preferences: SharedPreferences

    /**
     * On create method that is called at the start of activity to
     * instantiate it.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slidr = Slidr.attach(this, utils.config);
        setContentView(R.layout.activity_profile)
        recyclerView = findViewById(R.id.rvNotification)
        recyclerView.setHasFixedSize(false)
        adapter = NotificationAdapter(this, user.getNotification(), promiseDataBase)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }

    /**
     * On resume called when activity is called again.
     * It refresh the view.
     *
     * Méthode appelée quand une activité est ouverte de nouveau.
     * Elle permet de mettre à jour la vue.
     *
     */
    override fun onResume() {
        super.onResume()
        val mascot: ImageView = findViewById(R.id.imageViewMascotProfile)
        val textViewName: TextView = findViewById(R.id.textViewName)
        val textViewEmail: TextView = findViewById(R.id.textViewEmail)
        val textViewUsername: TextView = findViewById(R.id.textViewUsername)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        textViewName.text = user.name
        textViewEmail.text = user.email
        textViewUsername.text = user.username
        mascot.setImageResource(user.mascot.image_drawable)
    }

    /**
     * On click logout called when the logoutButton is pressed.
     * It clears the sharedPreferences of the app and returns
     * to the signinActivity.
     *
     * @param v
     *
     * Méthode appelée lorsque l'utilisateur clique sur le bouton déconnexion.
     * Cette méthode permet de nettoyer les préférences de l'application
     *  afin de ne pas être connecté automatiquement sur l'ancien compte
     *  et renvoie sur l'activité de connexion.
     */
    fun onClickLogout(v: View) {
        preferences.edit().clear().apply()
        val myIntent = Intent(this, SigninActivity::class.java)
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(myIntent)
    }

    /**
     * On click edit info called when editInfoButton is pressed.
     * It open editInfoActivity.
     *
     * @param v
     *
     * Méthode appelée lorsqu'on appuie sur le bouton d'édition du profil.
     * Elle ouvre l'activité correspondante.
     */
    fun onClickEditInfo(v: View) {
        val myIntent = Intent(this, EditInfoActivity::class.java)
        startActivity(myIntent)
    }
}