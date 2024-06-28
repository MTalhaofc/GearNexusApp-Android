package com.project.gearnexus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.project.gearnexus.adapters.PostAdapter
import com.project.gearnexus.databinding.FragmentHomeBinding
import com.project.gearnexus.models.Post

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventListener: ValueEventListener
    private lateinit var postList: MutableList<Post>
    private lateinit var postAdapter: PostAdapter
    private lateinit var auth: FirebaseAuth
    private var currentUserEmail: String? = null

    override fun onCreateView(
                inflater : LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ):
            View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("posts")

        binding.recyclerView.layoutManager = GridLayoutManager(context, 1)

        postList = mutableListOf()
        postAdapter = PostAdapter(postList, requireContext(), showButtons = true)
        binding.recyclerView.adapter = postAdapter

        eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for (itemSnapshot in snapshot.children) {
                    val post = itemSnapshot.getValue(Post::class.java)
                    post?.let {
                        it.postId = itemSnapshot.key
                        postList.add(it)
                    }
                }
                postAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        }
        databaseReference.addValueEventListener(eventListener)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        databaseReference.removeEventListener(eventListener)
    }
}

