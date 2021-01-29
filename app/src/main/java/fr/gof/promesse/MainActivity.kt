package fr.gof.promesse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.State
import fr.gof.promesse.model.User
import java.util.*

class MainActivity : AppCompatActivity() {

    val promiseDataBase = PromiseDataBase(this@MainActivity)

    lateinit var defaultUser : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        defaultUser = promiseDataBase.createDefaultAccount()
        val label = findViewById<TextView>(R.id.test)
        val promesse = Promise(-1, "Promesse2 priorité", 5, State.DONE, true, "Desc", true, Date(System.currentTimeMillis()), Date(1611788399000), null)
        defaultUser.addPromise(promesse, promiseDataBase)
        val promesse1 = Promise(-1, "Promesse0", 5, State.TODO, false, "Desc", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)
        defaultUser.addPromise(promesse1, promiseDataBase)
        val promesse2 = Promise(-1, "Promesse1", 5, State.TODO, false, "Desc", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()+200), null)
        defaultUser.addPromise(promesse2, promiseDataBase)
        val promesse3 = Promise(-1, "Promesse3 priorité", 5, State.TODO, true, "Desc", true, Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), null)
        defaultUser.addPromise(promesse3, promiseDataBase)
        var s = ""
        for(p in promiseDataBase.getAllPromisesNameLike("Promesse3")){
            s+=(p.dateTodo.toString()+" "+p.title+"\n")
        }
        label.setText(s)
    }
}