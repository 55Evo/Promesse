package fr.gof.promesse

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.textfield.TextInputEditText
import fr.gof.promesse.MainActivity.Companion.user
import fr.gof.promesse.adapter.MascotAdapter
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.listener.MascotListener
import fr.gof.promesse.model.Mascot
import fr.gof.promesse.model.User
import travel.ithaka.android.horizontalpickerlib.PickerLayoutManager

/**
 * Edit info activity
 *
 */
class EditInfoActivity : AppCompatActivity() {
    private val promiseDataBase = PromiseDataBase(this)
    private lateinit var editTextName: TextInputEditText
    private lateinit var editUsername: TextInputEditText
    private lateinit var editTextNewPassword: TextInputEditText
    private lateinit var editTextOldPassword: TextInputEditText
    private lateinit var editTextConfirmPassword: TextInputEditText
    private var mascotPosition: Int = 0

    private lateinit var preferences: SharedPreferences

    lateinit var adapter: MascotAdapter
    private lateinit var recyclerView: RecyclerView
    private val listMascot: List<Mascot> = listOf(
        Mascot.JACOU,
        Mascot.RAYMOND,
        Mascot.EUSTACHE
    )

    /**
     * On create method that is called at the start of activity to
     * instantiate all attributes.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_info)

        editTextName = findViewById(R.id.editTextName)
        editUsername = findViewById(R.id.editUsername)
        editTextOldPassword = findViewById(R.id.editTextOldPassword)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        recyclerView = findViewById(R.id.recycler_mascot)
        adapter = MascotAdapter(
            this, listMascot,
            MascotListener(listMascot, this), promiseDataBase, true
        )

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        val pickerLayoutManager = PickerLayoutManager(this, PickerLayoutManager.HORIZONTAL, false)
        pickerLayoutManager.isChangeAlpha = true
        pickerLayoutManager.scaleDownBy = 0.30f
        pickerLayoutManager.scaleDownDistance = 0.6f
        recyclerView.layoutManager = pickerLayoutManager
        pickerLayoutManager.setOnScrollStopListener { v ->
            val constraintLayout = v as ConstraintLayout
            mascotPosition = constraintLayout.tag as Int
        }
        //for snap in center (magnetisme)
        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(recyclerView)

        editTextName.setText(user.name)
        editUsername.setText(user.username)
        mascotPosition = adapter.listMascot.lastIndexOf(user.mascot)
        recyclerView.scrollToPosition(mascotPosition)

    }

    /**
     * Hide keyboard that can hide the keyboard when it's useless.
     *
     * @param activity
     *
     * Méthode qui cache le clavier quand on en a plus besoin.
     */
    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * On click update name that is called when user clicks on the updateNameButton.
     * This method check if the fields are empty or not and if the username already exists
     * and go back to the profileActivity.
     *
     * @param v
     *
     * Méthode appelée lorsque l'utilisateur appuie sur le bouton pour enregistrer
     * les modifications de nom / username.
     * Elle vérifie si les champs ne sont pas vides et si le nom d'utilisateur
     * n'est pas déjà pris et retourne sur l'activité de profil.
     */
    fun onClickUpdateName(v: View) {
        var err = false
        if (editUsername.text.toString().isEmpty()) {
            editUsername.error = getString(R.string.emptyField)
            err = true
        }
        if (editTextName.text.toString().isEmpty()) {
            editTextName.error = getString(R.string.emptyField)
            err = true
        }

        if (user.isUsernameExist(editUsername.text.toString()) && user.username != editUsername.text.toString()) {
            editUsername.error = getString(R.string.usernameAlreadyExist)
            err = true
        }

        if (!err) {
            val usr = User(
                user.email,
                editUsername.text.toString(),
                editTextName.text.toString(),
                user.password,
                listMascot[mascotPosition]
            )
            user.updateUser(usr)
            preferences.edit().clear().apply()
            preferences.edit().putString("userEmail", user.username).apply()
            hideKeyboard(this)
            finish()
        }
    }

    /**
     * On click update password that is called when user clicks on updatePasswordButton.
     * This method check if old password matches with current password and if new password
     * matches with confirm password.
     *
     * @param v
     *
     * Méthode appelée lorsque l'utilisateur clique sur le bouton pour enregistrer la
     * modification du mot de passe.
     * Elle vérifie la validité de l'ancien mot de passe et vérifie que le nouveau
     * mot de passe correspond bien à celui de la confirmation puis retourne à
     * l'activité du profil.
     */
    fun onClickUpdatePassword(v: View) {
        Toast.makeText(applicationContext, "partie mise a jour de lutilisateur", Toast.LENGTH_LONG)
            .show()
        //comparer les variable et basta
        if (user.checkConnection(editTextOldPassword.text.toString())) {
            if (editTextNewPassword.text.toString().length < 8) {
                editTextNewPassword.error = getString(R.string.passwordTooShort)
            } else {
                if (editTextNewPassword.text.toString() == editTextConfirmPassword.text.toString()) {
                    user.updateUser(
                        User(
                            user.email,
                            user.username,
                            user.name,
                            editTextNewPassword.text.toString(),
                            user.mascot
                        )
                    )
                    finish()
                } else {
                    editTextConfirmPassword.error = getString(R.string.different_password)
                }
            }
        } else {
            editTextOldPassword.error = getString(R.string.bad_password)
        }
    }
}