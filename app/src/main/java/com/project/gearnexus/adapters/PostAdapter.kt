package com.project.gearnexus.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.project.gearnexus.R
import com.project.gearnexus.Update_Ad
import com.project.gearnexus.View_Ad
import com.project.gearnexus.databinding.ItemPostBinding
import com.project.gearnexus.models.Post

class PostAdapter(
    private var postList: List<Post>,
    private val context: Context,
    private val showButtons: Boolean = false  // Add a flag to determine button visibility
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = postList.size

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Initialize click listeners and other setup
            binding.updateButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val post = postList[position]
                    // Start Update_AdActivity with postId and currentUserEmail
                    val intent = Intent(context, Update_Ad::class.java).apply {
                        putExtra("postId", post.postId)
                    }
                    context.startActivity(intent)
                }
            }

            binding.deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val post = postList[position]
                    if (post.postId != null) {
                        showCustomDeleteConfirmationDialog(post)
                    } else {
                        Toast.makeText(context, "Post ID is null", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            binding.viewButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val post = postList[position]
                    // Start ViewAdActivity with postId
                    val intent = Intent(context, View_Ad::class.java).apply {
                        putExtra("postId", post.postId)
                    }
                    context.startActivity(intent)
                }
            }
        }

        private fun showCustomDeleteConfirmationDialog(post: Post) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_confirmation, null)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
            val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
            val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
            val btnNo = dialogView.findViewById<Button>(R.id.btnNo)

            dialogTitle.text = "Delete Post"
            dialogMessage.text = "Are you sure you want to delete this AD?"

            val dialogBuilder = AlertDialog.Builder(context).apply {
                setView(dialogView)
                setCancelable(false)
            }
            val alertDialog = dialogBuilder.create()

            btnYes.setOnClickListener {
                alertDialog.dismiss()
                if (post.postId != null) {
                    deletePost(post.postId!!)
                } else {
                    Toast.makeText(context, "Post ID is null", Toast.LENGTH_SHORT).show()
                }
            }

            btnNo.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        private fun deletePost(postId: String) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("posts").child(postId)
            databaseReference.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show()
                }
        }

        fun bind(post: Post) {
            binding.apply {
                postTitle.text = post.name
                postPrice.text = post.price
                postDetails.text = post.details
                postLocation.text = post.location
                postContactNumber.text = post.contactNumber
                Glide.with(root.context).load(post.imageUrl).into(postImage)
                if (showButtons) {
                    updateButton.visibility = View.VISIBLE
                    deleteButton.visibility = View.VISIBLE
                } else {
                    updateButton.visibility = View.GONE
                    deleteButton.visibility = View.GONE
                }
            }
        }
    }

    // Method to update the list of posts
    fun updateList(newList: List<Post>) {
        postList = newList
        notifyDataSetChanged()
    }
}
