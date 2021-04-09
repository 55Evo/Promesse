package fr.gof.promesse

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.database.PromiseDataBase

/**
 * Signin activity
 *
 * @constructor Create empty Signin activity
 */
class SigninActivity : AppCompatActivity() {

    val promiseDataBase = PromiseDataBase(this@SigninActivity)
    private lateinit var preferences: SharedPreferences

    /**
     * On create method that is called at the start of activity to
     * instantiate it.
     *
     * @param savedInstanceState
     */
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        if (promiseDataBase.userIsEmpty()) {
            val myIntent = Intent(this, SignupActivity::class.java)
            startActivity(myIntent)
            finish()
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this@SigninActivity)
        autoSignin()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
    }

    /**
     * Auto signin that log the user automatically if the user has
     * already logged in.
     *
     * Cette méthode permet une connexion automatique si l'utilisateur
     * s'est déjà connecté auparavant.
     */
    private fun autoSignin() {
        val userEmail = preferences.getString("userEmail", "")
        if (userEmail != "") {
            user = promiseDataBase.getUser(userEmail!!)
            val myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent)
            finish()
        }
    }

    /**
     * Onclick no account called when user click on noAccountButton.
     * It switch to the signupActivity.
     *
     * @param v
     *
     * Méthode appelée lorsqu'on clique sur le bouton "je n'ai pas de compte"
     * et qui redirige vers la page de création de compte
     */
    fun onclickNoAccount(v: View) {
        val myIntent = Intent(this, SignupActivity::class.java)
        startActivity(myIntent)
    }

    /**
     * Onclick sign in called when user click on signinButton.
     * It check if fields are correctly entered.
     *
     * @param v
     *
     * Méthode appelée lors d'un clic sur le bouton de connexion.
     * Elle vérifie si les champs ont bien été entrés puis connecte
     * l'utilisateur.
     */
    fun onclickSignIn(v: View) {
        val email = findViewById<TextInputLayout>(R.id.email).editText
        val password = findViewById<TextInputLayout>(R.id.password).editText

        var error = false
        if (email?.length() == 0) {
            email.error = getString(R.string.emptyField)
            error = true
        }
        if (password?.length() == 0) {
            password.error = getString(R.string.emptyField)
            error = true
        }
        if (error) {
            return
        }
        if (!promiseDataBase.emailOrUsernameExists(email?.text.toString())) {
            email?.error = getString(R.string.unknownEmail)
            error = true
        }

        if (error) {
            return
        }
        if (promiseDataBase.check(email?.text.toString(), password?.text.toString())) {
            user = promiseDataBase.getUser(email?.text.toString())
            preferences.edit().putString("userEmail", user.email).apply()
            val myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent)
        } else {
            password?.error = getString(R.string.wrongPassword)
        }

    }
}