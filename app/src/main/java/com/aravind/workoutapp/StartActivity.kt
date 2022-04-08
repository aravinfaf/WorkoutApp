package com.aravind.workoutapp

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aravind.workoutapp.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    private var binding : ActivityStartBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.startFrameLayout?.setOnClickListener {
           val intent = Intent(this,ExcerciseActivity::class.java)
            startActivity(intent)
        }
    }
}