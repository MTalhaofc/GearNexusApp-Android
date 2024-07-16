package com.project.gearnexus.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.project.gearnexus.Contactuspage
import com.project.gearnexus.Notifications
import com.project.gearnexus.adapters.PostAdapter
import com.project.gearnexus.databinding.FragmentHomeBinding
import com.project.gearnexus.models.Post

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var reference: DatabaseReference
    private lateinit var eventListener: ValueEventListener
    private lateinit var postList: MutableList<Post>
    private lateinit var postAdapter: PostAdapter
    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        // Retrieve userId from arguments
        arguments?.let {
            currentUserId = it.getString("userId")
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("posts")
        reference = FirebaseDatabase.getInstance().getReference("users")
        val storageReference = FirebaseStorage.getInstance().reference.child("profile_images")

        binding.recyclerView.layoutManager = GridLayoutManager(context, 1)
binding.notificationIcon.setOnClickListener{

    val intent = Intent(activity, Notifications::class.java)
    startActivity(intent)


}
        postList = mutableListOf()
        postAdapter = PostAdapter(postList, requireContext(), showButtons = true)
        binding.recyclerView.adapter = postAdapter

        eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()

                for (itemSnapshot in snapshot.children) {
                    val post = itemSnapshot.getValue(Post::class.java)
                    post?.let {
                        it.postId = itemSnapshot.key.toString()
                        // Compare post's userId with currentUserId
                        if (it.postuserId == currentUserId) {
                            postList.add(it)
                        }
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
