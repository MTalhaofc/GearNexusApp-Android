package com.project.gearnexus.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.project.gearnexus.R
import com.project.gearnexus.adapters.UserAdapter
import com.project.gearnexus.models.User

class ChatsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var users: MutableList<User>
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        users = ArrayList()
        userAdapter = UserAdapter(requireContext(), users)
        recyclerView.adapter = userAdapter

        progressBar = view.findViewById(R.id.progress_bar)
        searchView = view.findViewById(R.id.search_view)

        readUsers()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                userAdapter.filter.filter(newText)
                return true
            }
        })

        return view
    }

    private fun readUsers() {
        val reference = FirebaseDatabase.getInstance().getReference("users")
        Log.d("ChatsFragment", "Reading users from database")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        Log.d("ChatsFragment", "User found: ${user.name}")
                        users.add(user)
                    }
                }
                userAdapter.setUserList(users)
                userAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ChatsFragment", "Database error: ${databaseError.message}")
                Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        })
    }
}
