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

class SigninActivity : AppCompatActivity() {

    val promiseDataBase = PromiseDataBase(this@SigninActivity)
    private lateinit var preferences: SharedPreferences

    /**
     * On create
     *
     * @param savedInstanceState
     */
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        if(promiseDataBase.userIsEmpty()) {
            val myIntent = Intent( this, SignupActivity::class.java)
            startActivity(myIntent)
            finish()
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this@SigninActivity)
        autoSignin()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
    }

    private fun autoSignin(){
        val userEmail = preferences.getString("userEmail", "")
        if(userEmail != ""){
            user = promiseDataBase.getUser(userEmail!!)
            val myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent)
            finish()
        }
    }

    /**
     * Onclick no account
     *
     * @param v
     */
    fun onclickNoAccount(v:View) {
        val myIntent = Intent(this, SignupActivity::class.java)
        startActivity(myIntent)
    }

    /**
     * Onclick sign in
     *
     * @param v
     */
    fun onclickSignIn(v:View) {
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
        if(!promiseDataBase.emailOrUsernameExists(email?.text.toString())) {
            email?.error = getString(R.string.unknownEmail)
            error = true
        }

        if (error) {
            return
        }
        if(promiseDataBase.check(email?.text.toString(), password?.text.toString())) {
            user = promiseDataBase.getUser(email?.text.toString())
            preferences.edit().putString("userEmail", user.email).apply()
            val myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent)
        } else {
            password?.error = getString(R.string.wrongPassword)
        }

    }
}