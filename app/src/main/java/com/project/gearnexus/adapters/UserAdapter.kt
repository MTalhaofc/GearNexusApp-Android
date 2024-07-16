package com.project.gearnexus.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.project.gearnexus.R
import com.project.gearnexus.models.User
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter(private val context: Context, private var users: List<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>(), Filterable {

    private var userListFull: List<User> = ArrayList(users)

    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.username.text = user.name
        holder.userNumber.text = user.number // Assuming User model has a field 'phoneNumber'

        if (user.profileImageUrl == "default") {
            holder.profileImage.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(context)
                .load(user.profileImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profileImage)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(user)
        }

        holder.cardView.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${user.number}")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.username)
        var userNumber: TextView = itemView.findViewById(R.id.user_number)
        var profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        var cardView: CardView = itemView.findViewById(R.id.card_view)
    }

    override fun getFilter(): Filter {
        return userFilter
    }

    fun setUserList(userList: List<User>) {
        userListFull = ArrayList(userList)
        users = userListFull
        notifyDataSetChanged()
    }

    private val userFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: MutableList<User> = ArrayList()

            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(userListFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim()
                for (user in userListFull) {
                    if (user.name.toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(user)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            users = results?.values as List<User>
            notifyDataSetChanged()
        }
    }
}
