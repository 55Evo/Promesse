package fr.gof.promesse

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.adapter.MascotAdapter
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.MascotListener
import fr.gof.promesse.model.Mascot
import fr.gof.promesse.model.User

class EditInfoActivity : AppCompatActivity() {
    val promiseDataBase = PromiseDataBase(this)
    lateinit var editTextName : TextInputEditText
    lateinit var editUsername : TextInputEditText
    lateinit var editTextNewPassword : TextInputEditText
    lateinit var editTextOldPassword : TextInputEditText
    lateinit var editTextConfirmPassword : TextInputEditText

    lateinit var adapter : MascotAdapter
    lateinit var recyclerView: RecyclerView
    private val listMascot: List<Mascot> = listOf(
        Mascot.JACOU,
        Mascot.RAYMOND,
        Mascot.EUSTACHE
    )


     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_edit_info)

         recyclerView= findViewById(R.id.recycler_mascot)
         recyclerView.setHasFixedSize(true)

         adapter = MascotAdapter(this, listMascot,
             MascotListener(listMascot, this) , promiseDataBase, true)
         recyclerView.adapter = adapter
         recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
         //for snap in center (magnetisme)
         var helper : SnapHelper = LinearSnapHelper()
         helper.attachToRecyclerView(recyclerView)

         editTextName = findViewById<TextInputEditText>(R.id.editTextName)
         editUsername = findViewById<TextInputEditText>(R.id.editUsername)
         editTextOldPassword = findViewById<TextInputEditText>(R.id.editTextOldPassword)
         editTextNewPassword = findViewById<TextInputEditText>(R.id.editTextNewPassword)
         editTextConfirmPassword = findViewById<TextInputEditText>(R.id.editTextConfirmPassword)

         editTextName.setText(user.name)
         editUsername.setText(user.username)

    }
    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun onClickUpdateName(v : View){
        var err = false
        if (editUsername.text.toString().isEmpty()){
            editUsername.error = getString(R.string.errUsername)
            err = true}
        if (editTextName.text.toString().isEmpty()){
            editTextName.error = getString(R.string.errName)
            err = true}

        if (user.isUsernameExist(editUsername.text.toString()) && user.username != editUsername.text.toString())  {
            editUsername.error = getString(R.string.usernameAlreadyExist)
            err = true}

        if (!err){
            var usr = User(user.email, editUsername.text.toString(),
                editTextName.text.toString(), user.password, user.mascot)
            user.updateUser(usr)
            Toast.makeText(applicationContext, getString(R.string.updateMessage), Toast.LENGTH_LONG).show()
            hideKeyboard(this)
        }
    }
    fun onClickUpdatePassword(v : View){
        var err = false
        Toast.makeText(applicationContext,"partie mise a jour de lutilisateur", Toast.LENGTH_LONG).show()
        //comparer les variable et basta
        if (user.checkConnection(user.email, editTextOldPassword.text.toString())){
            if (editTextNewPassword.text.toString() == editTextConfirmPassword.text.toString()){
                user.updateUser(User(user.email,user.username,
                    user.name, editTextNewPassword.text.toString(), user.mascot))
                    Toast.makeText(applicationContext, getString(R.string.updateMessage), Toast.LENGTH_LONG).show()
                //penser à rajouter le truc de paulinien
                }
            else{
                editTextConfirmPassword.error = getString(R.string.different_password)
                err = true}
            }
        else{
            editTextOldPassword.error = getString(R.string.bad_password)
            err = true
        }

    }
}