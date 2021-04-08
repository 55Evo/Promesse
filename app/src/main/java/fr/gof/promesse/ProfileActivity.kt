package fr.gof.promesse

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
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

class ProfileActivity : AppCompatActivity() {
    lateinit var adapter : NotificationAdapter
    lateinit var recyclerView: RecyclerView
    val promiseDataBase = PromiseDataBase(this)
    private lateinit var slidr: SlidrInterface
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slidr = Slidr.attach(this, utils.config);
        setContentView(R.layout.activity_profile)
        recyclerView= findViewById(R.id.rvNotification)
        recyclerView.setHasFixedSize(true)

        adapter = NotificationAdapter(this, user.getNotification(), promiseDataBase)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        val textViewName : TextView = findViewById(R.id.textViewName)
        val textViewEmail : TextView = findViewById(R.id.textViewEmail)
        val textViewUsername : TextView = findViewById(R.id.textViewUsername)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        textViewName.text = user.name
        textViewEmail.text = user.email
        textViewUsername.text = user.username
    }

    fun onClickLogout(v: View) {
        preferences.edit().clear().apply()
        val myIntent = Intent(this, SigninActivity::class.java)
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(myIntent)
    }

    fun onClickEditInfo(v: View) {
        val myIntent = Intent(this, EditInfoActivity::class.java)
        startActivity(myIntent)
    }
}