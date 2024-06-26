package com.project.gearnexus.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.project.gearnexus.adapters.PostAdapter
import com.project.gearnexus.databinding.FragmentSearchBinding
import com.project.gearnexus.models.Post

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventListener: ValueEventListener
    private lateinit var postList: MutableList<Post>
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root = binding.root

        databaseReference = FirebaseDatabase.getInstance().getReference("posts")
        postList = mutableListOf()
        postAdapter = PostAdapter(postList, requireContext(), showButtons = false)

        binding.recyclerView.layoutManager = GridLayoutManager(context, 1)
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

        // Setup SearchView listener for filtering
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText)
                return true
            }
        })

        return root
    }

    private fun filterPosts(query: String?) {
        val filteredList = mutableListOf<Post>()
        postList.forEach { post ->
            if (post.name?.toLowerCase()?.contains(query?.toLowerCase() ?: "") == true) {
                filteredList.add(post)
            }
        }
        postAdapter.updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        databaseReference.removeEventListener(eventListener)
    }
}
