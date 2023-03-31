package com.rifqipadisiliwangi.assigmentandroid.view.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.ActionBar
import com.rifqipadisiliwangi.assigmentandroid.databinding.ActivitySplashBinding
import com.rifqipadisiliwangi.assigmentandroid.view.home.HomeActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        },3000)
        setContentView(binding.root)
    }
}