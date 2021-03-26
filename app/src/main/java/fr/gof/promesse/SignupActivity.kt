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

class SignupActivity : AppCompatActivity() {

    val promiseDataBase = PromiseDataBase(this@SignupActivity)

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun onClickSignup(v: View) {
        val email = findViewById<TextInputLayout>(R.id.email).editText
        val nom = findViewById<TextInputLayout>(R.id.name).editText
        val password = findViewById<TextInputLayout>(R.id.password).editText
        val confirmpassword = findViewById<TextInputLayout>(R.id.confirmpassword).editText
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
        if (confirmpassword?.length() == 0) {
            confirmpassword.error = getString(R.string.emptyField)
            error = true
        }
        if (password?.length()!! < 8) {
            password?.error = getString(R.string.passwordTooShort)
            error = true
        }
        if ((!error) && confirmpassword?.text.toString() != password?.text.toString()) {
            confirmpassword?.error = getString(R.string.pawordDoesntMatch)
            confirmpassword?.setText("")
            error = true
        }
        if (error) {
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email?.text.toString()).matches()) {
            email?.error = getString(R.string.invalidFormat)
            error = true
        }
        if (error) {
            return;
        }
        if(promiseDataBase.emailExist(email?.text.toString())) {
            email?.error = getString(R.string.alreadyExist)
            error = true
        }
        if (error) {
            return;
        }

        val usr = User(email?.text.toString(), nom?.text.toString(), password?.text.toString(), Mascot.JACOU)
        promiseDataBase.createAccount(usr)
        user = usr
        val myIntent = Intent(this, ChooseMascotActivity::class.java)
        startActivity(myIntent)
    }

    fun onClickCancel(v: View) {
        finish()
    }

}