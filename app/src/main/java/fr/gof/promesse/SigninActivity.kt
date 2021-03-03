package fr.gof.promesse

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import fr.gof.promesse.database.PromiseDataBase

class SigninActivity : AppCompatActivity() {

    val promiseDataBase = PromiseDataBase(this@SigninActivity)

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        if(promiseDataBase.userIsEmpty()) {
            val myIntent = Intent( this, SignupActivity::class.java)
            startActivity(myIntent)
            finish()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
    }

    fun onclickNoAccount(v:View) {
        val myIntent = Intent(this, SignupActivity::class.java)
        startActivity(myIntent)
    }

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
        if(!promiseDataBase.emailExist(email?.text.toString())) {
            email?.error = getString(R.string.unknownEmail)
            error = true
        }

        if (error) {
            return
        }
        if(promiseDataBase.checkPassword(email?.text.toString(), password?.text.toString())) {

            utils.user = promiseDataBase.getUser(email?.text.toString())
            val myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent)
        } else {
            password?.error = getString(R.string.wrongPassword)
        }

    }
}