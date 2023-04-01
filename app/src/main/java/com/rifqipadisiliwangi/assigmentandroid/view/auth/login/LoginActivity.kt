package com.rifqipadisiliwangi.assigmentandroid.view.auth.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import com.rifqipadisiliwangi.assigmentandroid.databinding.ActivityLoginBinding
import com.rifqipadisiliwangi.assigmentandroid.model.User
import com.rifqipadisiliwangi.assigmentandroid.room.DataHistory
import com.rifqipadisiliwangi.assigmentandroid.utils.PreferencesClass
import com.rifqipadisiliwangi.assigmentandroid.view.auth.register.RegisterActivity
import com.rifqipadisiliwangi.assigmentandroid.view.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    lateinit var mDatabase : DatabaseReference
    lateinit var preference : PreferencesClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDatabase = FirebaseDatabase.getInstance().getReference("user")
        preference = PreferencesClass(this)


        binding.btnLogin.setOnClickListener{
            preference.setValue("username",binding.etUsername.text.toString())
            if ( binding.etUsername.equals("")){
                binding.etUsername.error = "Silakan tulis username Anda"
                binding.etUsername.requestFocus() // agar cursor fokus ke username
            }else if ( binding.etPassword.equals("")){
                binding.etPassword.error = "Silakan tulis password Anda"
                binding.etPassword.requestFocus() // agar cursor fokus ke username
            } else{

                var statusUsername = binding.etUsername.text.indexOf(".")
                if (statusUsername >=0) {
                    binding.etUsername.error = "Silahkan tulis Username Anda tanpa ."
                    binding.etUsername.requestFocus()
                } else {
                    pushLogin(binding.etUsername.text.toString(), binding.etPassword.text.toString())
                }


            }
        }

        binding.btnRegister.setOnClickListener{
            var goSignup = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(goSignup)
        }
    }

    private fun pushLogin(iUsername: String, iPassword: String) {
        mDatabase.child(iUsername).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val user = dataSnapshot.getValue(User::class.java)
                if (user == null){
                    Toast.makeText(this@LoginActivity, "User tidak ditemukan", Toast.LENGTH_LONG).show()
                }
                else {

                    if(user.password.equals(iPassword)){

                        preference.setValue("user", user.username.toString())
                        preference.setValue("email", user.email.toString())
                        preference.setValue("status", "1")

                        finishAffinity()

                        var intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, "Password Anda salah", Toast.LENGTH_LONG).show()
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@LoginActivity, databaseError.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}