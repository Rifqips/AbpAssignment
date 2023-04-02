package com.rifqipadisiliwangi.assigmentandroid.view.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.ActionBar
import com.rifqipadisiliwangi.assigmentandroid.databinding.ActivitySplashBinding
import com.rifqipadisiliwangi.assigmentandroid.utils.PreferencesClass
import com.rifqipadisiliwangi.assigmentandroid.view.auth.login.LoginActivity
import com.rifqipadisiliwangi.assigmentandroid.view.home.HomeActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private lateinit var preferences: PreferencesClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)

        preferences = PreferencesClass(this)

        Handler(Looper.myLooper()!!).postDelayed({
            if (preferences.sharedPref.getString("username","") == "" && preferences.sharedPref.getString("password", "") == "") {
                val intentLogin = Intent(this, LoginActivity::class.java)
                startActivity(intentLogin)
            } else {
                val intent = Intent(this,HomeActivity::class.java)
                intent.putExtra("username", preferences.sharedPref.getString("username",""))
                startActivity(intent)
            }
        },3000)
        setContentView(binding.root)
    }
}