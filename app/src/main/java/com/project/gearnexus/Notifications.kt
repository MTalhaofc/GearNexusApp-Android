package com.project.gearnexus

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.gearnexus.databinding.ActivityContactuspageBinding
import com.project.gearnexus.databinding.ActivityNotificationsBinding

class Notifications : AppCompatActivity() {
    private lateinit var binding : ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabClose.setOnClickListener{

            finish()
        }

    }
}