package com.project.gearnexus.fragments
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.project.gearnexus.Login_Page
import com.project.gearnexus.ProfileEdit
import com.project.gearnexus.R

class ProfileFragment : Fragment() {

    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileUsername: TextView
    private lateinit var profilePassword: TextView
    private lateinit var titleName: TextView
    private lateinit var titleUsername: TextView
    private lateinit var editProfile: Button
    private lateinit var logoutButton: Button

    private lateinit var reference: DatabaseReference
    private lateinit var checkUserDatabase: Query

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        profileName = root.findViewById(R.id.profileName)
        profileEmail = root.findViewById(R.id.profileEmail)
        profileUsername = root.findViewById(R.id.profileUsername)
        profilePassword = root.findViewById(R.id.profilePassword)
        titleName = root.findViewById(R.id.titleName)
        titleUsername = root.findViewById(R.id.titleUsername)
        editProfile = root.findViewById(R.id.editButton)
        logoutButton = root.findViewById(R.id.logoutButton)

        reference = FirebaseDatabase.getInstance().getReference("users")
        auth = FirebaseAuth.getInstance()

        showAllUserData()

        editProfile.setOnClickListener {
            val userNumber = profileUsername.text.toString().trim()
            passUserData(userNumber)
        }

        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return root
    }

    private fun showAllUserData() {
        val intent = activity?.intent
        val nameUser = intent?.getStringExtra("name")
        val emailUser = intent?.getStringExtra("email")
        val numberUser = intent?.getStringExtra("number")
        val passwordUser = intent?.getStringExtra("password")




        titleName.text = nameUser
        titleUsername.text = numberUser
        profileName.text = nameUser
        profileEmail.text = emailUser
        profileUsername.text = numberUser
        profilePassword.text = passwordUser
    }

    private fun passUserData(userNumber: String) {
        checkUserDatabase = reference.orderByChild("number").equalTo(userNumber)

        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val nameFromDB = snapshot.child(userNumber).child("name").getValue(String::class.java)
                    val emailFromDB = snapshot.child(userNumber).child("email").getValue(String::class.java)
                    val numberFromDB = snapshot.child(userNumber).child("number").getValue(String::class.java)
                    val passwordFromDB = snapshot.child(userNumber).child("password").getValue(String::class.java)

                    val intent = Intent(activity, ProfileEdit::class.java)
                    intent.putExtra("name", nameFromDB)
                    intent.putExtra("email", emailFromDB)
                    intent.putExtra("number", numberFromDB)
                    intent.putExtra("password", passwordFromDB)

                    startActivity(intent)
                } else {
                    Toast.makeText(activity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout_confirmation, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)

        dialogTitle.text = "Logout Confirmation"
        dialogMessage.text = "Are you sure you want to logout?"

        val dialogBuilder = AlertDialog.Builder(requireContext()).apply {
            setView(dialogView)
            setCancelable(false)
        }
        val alertDialog = dialogBuilder.create()

        btnYes.setOnClickListener {
            alertDialog.dismiss()
            auth.signOut()
            val intent = Intent(activity, Login_Page::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            activity?.finish()
        }

        btnNo.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
