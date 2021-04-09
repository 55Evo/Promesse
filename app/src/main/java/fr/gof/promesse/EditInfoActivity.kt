package fr.gof.promesse

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
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


class EditInfoActivity : AppCompatActivity() {
    val promiseDataBase = PromiseDataBase(this)
    lateinit var editTextName: TextInputEditText
    lateinit var editUsername: TextInputEditText
    lateinit var editTextNewPassword: TextInputEditText
    lateinit var editTextOldPassword: TextInputEditText
    lateinit var editTextConfirmPassword: TextInputEditText
    var mascotPosition: Int = 0

    private lateinit var preferences: SharedPreferences

    lateinit var adapter: MascotAdapter
    lateinit var recyclerView: RecyclerView
    private val listMascot: List<Mascot> = listOf(
        Mascot.JACOU,
        Mascot.RAYMOND,
        Mascot.EUSTACHE
    )

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_info)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        recyclerView = findViewById(R.id.recycler_mascot)
        recyclerView.setHasFixedSize(true)

        adapter = MascotAdapter(
            this, listMascot,
            MascotListener(listMascot, this), promiseDataBase, true
        )
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
        var helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(recyclerView)

        editTextName = findViewById(R.id.editTextName)
        editUsername = findViewById(R.id.editUsername)
        editTextOldPassword = findViewById(R.id.editTextOldPassword)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)

        editTextName.setText(user.name)
        editUsername.setText(user.username)
        mascotPosition = adapter.listMascot.lastIndexOf(user.mascot)
        recyclerView.scrollToPosition(mascotPosition)

    }

    /**
     * Hide keyboard
     *
     * @param activity
     */
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

    /**
     * On click update name
     *
     * @param v
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
            var usr = User(
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
     * On click update password
     *
     * @param v
     */
    fun onClickUpdatePassword(v: View) {
        Toast.makeText(applicationContext, "partie mise a jour de lutilisateur", Toast.LENGTH_LONG).show()
        //comparer les variable et basta
        if (user.checkConnection(user.email, editTextOldPassword.text.toString())) {
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