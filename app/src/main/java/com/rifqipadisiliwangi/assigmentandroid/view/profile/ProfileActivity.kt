package com.rifqipadisiliwangi.assigmentandroid.view.profile

import android.content.ContentResolver
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.rifqipadisiliwangi.assigmentandroid.databinding.ActivityProfileBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private var imageMultiPart: MultipartBody.Part? = null
    private var imageFile: File? = null
    private val PICK_IMAGE_PERMISSIONS_REQUEST_CODE = 100
    private var imageUri: Uri? = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectImageBtn.setOnClickListener {
            openGallery()
        }

        binding.processImageBtn.setOnClickListener {
//            processImage(binding.processImageBtn)
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

                val imageView = binding.ocrImageView
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

//    fun processImage(v: View) {
//        if (binding.ocrImageView.drawable != null) {
//            binding.ocrResultEt.setText("")
//            v.isEnabled = false
//            val bitmap = (binding.ocrImageView.drawable as BitmapDrawable).bitmap
//            val image = FirebaseVisionImage.fromBitmap(bitmap)
//            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
//
//            detector.processImage(image)
//                .addOnSuccessListener { firebaseVisionText ->
//                    v.isEnabled = true
//                    processResultText(firebaseVisionText)
//                }
//                .addOnFailureListener {
//                    v.isEnabled = true
//                    binding.ocrResultEt.setText("Failed")
//                }
//        } else {
//            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show()
//        }
//
//    }


//    private fun processResultText(resultText: FirebaseVisionText) {
//        if (resultText.textBlocks.size == 0) {
//            binding.ocrResultEt.setText("No Text Found")
//            return
//        }
//        for (block in resultText.textBlocks) {
//            val blockText = block.text
//            binding.ocrResultEt.append(blockText + "\n")
//        }
//    }
}