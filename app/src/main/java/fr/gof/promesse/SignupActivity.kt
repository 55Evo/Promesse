package fr.gof.promesse

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Mascot
import fr.gof.promesse.model.User

/**
 * Signup activity
 *
 * @constructor Create empty Signup activity
 */
class SignupActivity : AppCompatActivity() {

    val promiseDataBase = PromiseDataBase(this@SignupActivity)

    /**
     * On create method that is called at the start of activity to
     * instantiate it.
     *
     * @param savedInstanceState
     *
     * Set le layout avec activity_signup.xml
     */
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    /**
     * On click signup that is called when user click on signupButton.
     * This method check if all the fileds aren't empty and if password matches
     * with confirmPassword
     *
     * @param v
     *
     * Méthode appelée quand on clique sur le bouton signup. Cette méthode
     * permet de vérifier la validité des champs et si les mots de passe
     * correspondent
     */
    fun onClickSignup(v: View) {
        val email = findViewById<TextInputLayout>(R.id.email).editText
        val nom = findViewById<TextInputLayout>(R.id.name).editText
        val password = findViewById<TextInputLayout>(R.id.password).editText
        val confirmPassword = findViewById<TextInputLayout>(R.id.confirmpassword).editText
        val username = findViewById<TextInputLayout>(R.id.username).editText
        var error = false

        if (email?.length() == 0) {
            email.error = getString(R.string.emptyField)
            error = true
        }

        if (nom?.length() == 0) {
            nom.error = getString(R.string.emptyField)
            error = true
        }

        if (password?.length() == 0) {
            password.error = getString(R.string.emptyField)
            error = true
        }

        if (confirmPassword?.length() == 0) {
            confirmPassword.error = getString(R.string.emptyField)
            error = true
        }

        if (password?.length()!! < 8) {
            password.error = getString(R.string.passwordTooShort)
            error = true
        }

        if (username?.length() == 0) {
            username.error = getString(R.string.emptyField)
            error = true
        }

        if (username?.length()!! > 20) {
            username.error = getString(R.string.tooMuchCharacters)
            error = true
        }

        if (username.text.toString().contains("@")) {
            username.error = getString(R.string.usernameDoesntContainsAt)
            error = true
        }

        if (email?.length()!! > 320) {
            email.error = getString(R.string.tooMuchCharacters)
            error = true
        }

        if (nom?.length()!! > 50) {
            nom.error = getString(R.string.tooMuchCharacters)
            error = true
        }

        if (password.length() > 100) {
            password.error = getString(R.string.tooMuchCharacters)
            error = true
        }

        if (promiseDataBase.usernameExist(username.text.toString())) {
            username.error = getString(R.string.usernameAlreadyExist)
            error = true
        }

        if ((!error) && confirmPassword?.text.toString() != password.text.toString()) {
            confirmPassword?.error = getString(R.string.pawordDoesntMatch)
            confirmPassword?.setText("")
            error = true
        }

        if (error) {
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email?.error = getString(R.string.invalidFormat)
            error = true
        }

        if (error) {
            return
        }

        if (promiseDataBase.emailExist(email.text.toString())) {
            email.error = getString(R.string.alreadyExist)
            error = true
        }

        if (error) {
            return
        }

        val usr = User(
            email.text.toString(),
            username.text.toString(),
            nom.text.toString(),
            password.text.toString(),
            Mascot.JACOU
        )
        promiseDataBase.createAccount(usr)
        user = usr
        val myIntent = Intent(this, ChooseMascotActivity::class.java)
        startActivity(myIntent)
    }

    /**
     * On click cancel that is called when user click on cancelButton.
     * It finish the activity that close the app.
     *
     * @param v
     *
     * Méthode appelée lors d'un clic sur le bouton cancel. Cette dernière
     * termine l'activité et donc ferme l'application.
     */
    fun onClickCancel(v: View) {
        finish()
    }

}