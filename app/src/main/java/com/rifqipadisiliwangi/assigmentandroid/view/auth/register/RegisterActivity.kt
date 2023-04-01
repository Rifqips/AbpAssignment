package com.rifqipadisiliwangi.assigmentandroid.view.auth.register

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.rifqipadisiliwangi.assigmentandroid.R
import com.rifqipadisiliwangi.assigmentandroid.databinding.ActivityRegisterBinding
import com.rifqipadisiliwangi.assigmentandroid.model.User
import com.rifqipadisiliwangi.assigmentandroid.utils.PreferencesClass
import com.rifqipadisiliwangi.assigmentandroid.view.auth.login.LoginActivity
import com.rifqipadisiliwangi.assigmentandroid.view.home.HomeActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class RegisterActivity : AppCompatActivity(), PermissionListener {

    private lateinit var binding : ActivityRegisterBinding


    var statusAdd: Boolean = false
    lateinit var filePath: Uri

    private lateinit var mFirebaseDatabase: DatabaseReference
    private lateinit var mFirebaseInstance : FirebaseDatabase
    lateinit var storageReference : StorageReference
    lateinit var preferences : PreferencesClass


    lateinit var sUsername : String
    lateinit var sPassword : String
    lateinit var sEmail : String
    lateinit var sUrl : String


    private var imageMultiPart: MultipartBody.Part? = null
    private var imageFile: File? = null
    private val PICK_IMAGE_PERMISSIONS_REQUEST_CODE = 100
    private var imageUri: Uri? = Uri.EMPTY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = PreferencesClass(this)
        storageReference = Firebase.storage.reference
        mFirebaseInstance = FirebaseDatabase.getInstance()
        mFirebaseDatabase = mFirebaseInstance.getReference("user")

        binding.imageView4.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }



        binding.btnLanjutkan.setOnClickListener{

            sUsername = binding.etUsername.text.toString()
            sPassword = binding.etPassword.text.toString()
            sEmail = binding.etEmail.text.toString()
            sUrl = binding.ivProfile.toString()

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

                    // untuk url nya di save ke share preferences
                    ref.downloadUrl.addOnSuccessListener {
                        preferences.setValue("url", it.toString())
                        // preferences.setValue("url", it.toString())
//                        saveToFirebase(it.toString())
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
            if ( binding.etUsername.equals("")){
                binding.etUsername.error = "Silakan isi username Anda"
                binding.etUsername.requestFocus()
            } else if (binding.etPassword.equals("")){
                binding.etPassword.error = "Silakan isi password Anda"
                binding.etPassword.requestFocus()
            }else if (binding.etEmail.equals("")){
                binding.etEmail.error = "Silakan isi nama Anda"
                binding.etEmail.requestFocus()
            } else {
                val statusUsername = binding.etUsername.text.indexOf(".")
                if (statusUsername >=0) {
                    binding.etUsername.error = "Silahkan tulis Username Anda tanpa ."
                    binding.etUsername.requestFocus()
                } else {
                    saveUsername(sUsername, sPassword, sEmail, sUrl)
                }
            }

        }

        binding.ivAdd.setOnClickListener {
            openGallery()
        }
    }

    fun openGallery(){
        getContent.launch("image/*")
    }
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val contentResolver: ContentResolver = this!!.contentResolver
                val type = contentResolver.getType(it)
                imageUri = it

                val fileNameimg = "${System.currentTimeMillis()}.png"

                val imageView = binding.ivProfile
                imageView.setImageURI(it)

                Toast.makeText(this, "$imageUri", Toast.LENGTH_SHORT).show()

                val tempFile = File.createTempFile("assignment-1", fileNameimg, null)
                imageFile = tempFile
                val inputstream = contentResolver.openInputStream(uri)
                tempFile.outputStream().use    { result ->
                    inputstream?.copyTo(result)
                }
                val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
                imageMultiPart = MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
            }
        }

    private fun saveUsername(sUsername: String, sPassword: String,  sEmail: String, sUrl: String) {
        val user = User()
        user.username = sUsername
        user.password = sPassword
        user.email = sEmail
        user.url = sUrl

        if (sUsername != null){
            checkingUsername(sUsername, user)
        }
    }

    private fun checkingUsername(sUsername: String, data: User) {
        mFirebaseDatabase.child(sUsername).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user == null){
                    mFirebaseDatabase.child(sUsername).setValue(data)

                    val intent = Intent(this@RegisterActivity,
                        HomeActivity::class.java).putExtra("data", data.username)
                    startActivity(intent)

                } else {
                    Toast.makeText(this@RegisterActivity, "User sudah digunakan", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@RegisterActivity, ""+databaseError.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        openGallery()
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(this, "Anda tidak bisa menambahkan photo profile", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest?,
        token: PermissionToken?
    ) {
        TODO("Not yet implemented")
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

            binding.ivAdd.setImageResource(R.drawable.baseline_restore_from_trash_24)

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


}