package com.rifqipadisiliwangi.assigmentandroid.view.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rifqipadisiliwangi.assigmentandroid.R
import com.rifqipadisiliwangi.assigmentandroid.databinding.ActivityProfileBinding
import com.rifqipadisiliwangi.assigmentandroid.utils.PreferencesClass
import com.rifqipadisiliwangi.assigmentandroid.view.auth.login.LoginActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var preferences: PreferencesClass

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = PreferencesClass(this)

        Glide.with(this)
            .load(preferences.getValue("url"))
            .apply(RequestOptions.circleCropTransform())
            .into(binding.ivUser)

        binding.tvUsername.text = preferences.getValue("username")
        binding.tvEmail.text = preferences.getValue("email")

        binding.btnLogout.setOnClickListener {
            val mEditor = preferences.sharedPref.edit()
            mEditor.remove("username").apply()
            mEditor.remove("email").apply()
            mEditor.clear()
            alert()
        }
    }

    private fun alert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.title)
        builder.setMessage(R.string.dialogMsg)
        builder.setNegativeButton("Cancel"){dialogInterface, which ->
            Toast.makeText(this,"Cancel", Toast.LENGTH_LONG).show()
        }

        builder.setPositiveButton("Yes"){dialogInterface, which->
            val intentLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
            finishAffinity()
            Toast.makeText(this,"Anda Logout", Toast.LENGTH_LONG).show()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()


    }
}