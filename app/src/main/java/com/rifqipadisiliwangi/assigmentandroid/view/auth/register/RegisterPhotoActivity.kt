package com.rifqipadisiliwangi.assigmentandroid.view.auth.register

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.rifqipadisiliwangi.assigmentandroid.R
import com.rifqipadisiliwangi.assigmentandroid.databinding.ActivityRegisterPhotoBinding
import com.rifqipadisiliwangi.assigmentandroid.model.User
import com.rifqipadisiliwangi.assigmentandroid.utils.PreferencesClass
import com.rifqipadisiliwangi.assigmentandroid.view.home.HomeActivity
import java.util.*

class RegisterPhotoActivity : AppCompatActivity(), PermissionListener {

    private lateinit var binding : ActivityRegisterPhotoBinding

    val REQUEST_IMAGE_CAPTURE = 1
    var statusAdd: Boolean = false
    lateinit var filePath: Uri

    lateinit var storage : FirebaseStorage
    lateinit var storageReference : StorageReference
    lateinit var preferences : PreferencesClass

    //lateinit var user: User
    private lateinit var mFirebaseDatabase: DatabaseReference
    private lateinit var mFirebaseInstance: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = PreferencesClass(this)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()

        mFirebaseInstance = FirebaseDatabase.getInstance()
        mFirebaseDatabase = mFirebaseInstance.getReference("User")



//     tv_hello.text = "Selamat Datang\n"+intent.getParcelableExtra("data")
        binding.ivAdd.setOnClickListener{
            if (statusAdd) {
                statusAdd = false
                binding.btnSave.visibility = View.VISIBLE
                binding.ivAdd.setImageResource(R.drawable.ic_btn_upload)
                binding.ivProfile.setImageResource(R.drawable.ic_launcher_background)
            } else {

                ImagePicker.with(this)
                    .cameraOnly()
                    .start()
            }
        }

        binding.btnHome.setOnClickListener{
            finishAffinity()

            val goHome = Intent(this@RegisterPhotoActivity, HomeActivity::class.java)
            startActivity(goHome)
        }

        binding.btnSave.setOnClickListener{
            if (filePath != null){
                // membuat proggres dialog
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()

                // dengan folder yang ada di firebasenya
                val ref = storageReference.child("images/"+ UUID.randomUUID().toString())
                ref.putFile(filePath) // kasih filenya dengan uri tadi / filepath
                    .addOnSuccessListener {
                        // jika sukses matikan progress dialognya
                        progressDialog.dismiss()
                        Toast.makeText(this, "Uploaded", Toast.LENGTH_LONG).show()

                        // untuk url nya di save ke share preferences
                        ref.downloadUrl.addOnSuccessListener {
                            // preferences.setValue("url", it.toString())
                            saveToFirebase(it.toString())
                        }
                    }

                    // jika tidak sukses
                    .addOnFailureListener{ e ->
                        progressDialog.dismiss()
                        Toast.makeText(this, "Failed" + e.message, Toast.LENGTH_LONG).show()
                    }

                    // untuk menampilkan berapa persen yang sudah terupload
                    .addOnProgressListener {
                            taskSnapshot -> val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                        progressDialog.setMessage("Upload "+progress.toInt()+" %")
                    }

            }
        }

    }

    private fun saveToFirebase(url: String) {
        val user = User()
        mFirebaseDatabase.child(user.username!!).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user.url = url
                mFirebaseDatabase.child(user.username!!).setValue(user)
                preferences.setValue("user", user.username.toString())
                preferences.setValue("saldo", "")
                preferences.setValue("url", "")
                preferences.setValue("email", user.email.toString())
                preferences.setValue("status", "1")
                preferences.setValue("url", url)

                finishAffinity()
                val intent = Intent(this@RegisterPhotoActivity, HomeActivity::class.java)
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RegisterPhotoActivity, ""+error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    // kalau disetujui
    override fun onPermissionGranted(response: PermissionGrantedResponse?) {

        ImagePicker.with(this)
            .cameraOnly()	//User can only capture image using Camera
            .start()

    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(this, "Anda tidak bisa menambahkan photo profile", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: com.karumi.dexter.listener.PermissionRequest?,
        token: PermissionToken?
    ) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Tergesah? Klik tombol upload nanti aja", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            // Image Uri will not be null for RESULT_OK
            statusAdd = true // status digunakan untuk menganti icon
            filePath = data?.data!!

            Glide.with(this)
                .load(filePath)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfile)

            // Log.v("tamvan", "file uri upload"+filePath)

            binding.btnSave.visibility = View.VISIBLE
            binding.ivAdd.setImageResource(R.drawable.baseline_restore_from_trash_24)

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}