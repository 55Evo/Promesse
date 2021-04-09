
package fr.gof.promesse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Notification
import java.util.HashSet

/**
 * Notification adapter
 *
 * @property context
 * @property listNotifications
 * @property listener
 * @property database
 *
 * On créé un adapter pour la liste des notifications que peut recevoir un utilisateur
 * En effet un utilisateur peut réaliser une promesse pour un autre utilisateur et celui ci pourra
 * voir la liste des promesse réalisées à son encontre dans son profil
 */
class NotificationAdapter(var context: Context, var listNotifications: HashSet<Notification>, val database : PromiseDataBase) :RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return MyViewHolder(itemView)
    }

    /**
     * On bind view holder
     *
     * @param holder
     * @param position
     *
     * Lors du rechargement de la vue on récupère la notification et on set les différents éléments
     * à afficher
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.notification = listNotifications.elementAt(position)
        holder.textViewAuthor.text = holder.notification.author
        holder.textViewPromiseName.text = holder.notification.title
        holder.textViewNotificationDate.text = holder.notification.getDateToString()
    }

    /**
     * Get item count
     *
     * @return
     */
    override fun getItemCount(): Int {
        return listNotifications.size
    }

    /**
     * My view holder
     *
     * @constructor
     *
     * @param itemView
     *
     * Classe interne qui permet de récupérer les différentes vues affichées dans le profil de
     * l'utilisateur (notifications réalisées à son encontre)
     */
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var notification: Notification
        var textViewAuthor: TextView = itemView.findViewById(R.id.textViewAuthor)
        var textViewPromiseName: TextView = itemView.findViewById(R.id.textViewPromiseName)
        var textViewNotificationDate: TextView = itemView.findViewById(R.id.textViewNotificationDate)
    }
}
