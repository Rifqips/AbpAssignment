package com.rifqipadisiliwangi.assigmentandroid.view.kalkulator

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.rifqipadisiliwangi.assigmentandroid.R
import com.rifqipadisiliwangi.assigmentandroid.databinding.ActivityKalkulatorBinding
import com.rifqipadisiliwangi.assigmentandroid.room.DaoHistory
import com.rifqipadisiliwangi.assigmentandroid.room.DataHistory
import com.rifqipadisiliwangi.assigmentandroid.room.DatabaseHistory
import com.rifqipadisiliwangi.assigmentandroid.view.home.HomeActivity
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.*
import java.lang.ref.WeakReference
import java.util.*
class KalkulatorActivity : AppCompatActivity() {

    private lateinit var binding : ActivityKalkulatorBinding

    private val GALLERY_REQUEST_CODE = 1234
    private val WRITE_EXTERNAL_STORAGE_CODE = 1

    lateinit var activityResultLauncher:ActivityResultLauncher<Intent>

    lateinit var finalUri: Uri
    lateinit var finalUriSechond: Uri

    private var metodePerhitungan: String = ""

    private var dbHistory : DatabaseHistory? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKalkulatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHistory = DatabaseHistory.getInstance(this)

        checkPermission()
        requestPermission()
        firstNumber()
        setOnCheckedChangeListener()
        binding.btnHistory.setOnClickListener {

            if (true){
                historySetUp()
                Toast.makeText(this@KalkulatorActivity, "Berhasil Tambah History", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this@KalkulatorActivity, "Gagal Tambah History", Toast.LENGTH_SHORT).show()
            }
        }


        binding.ivBack.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
        }

        activityResultLauncher  =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode== RESULT_OK) {
                    var extras: Bundle? = result.data?.extras
                    var imageUri: Uri
                    var imageBitmap = extras?.get("data") as Bitmap
                    var imageResult: WeakReference<Bitmap> = WeakReference(
                        Bitmap.createScaledBitmap(
                            imageBitmap, imageBitmap.width, imageBitmap.height, false
                        ).copy(
                            Bitmap.Config.RGB_565, true
                        )
                    )
                    var bm = imageResult.get()
                    imageUri = saveImage(bm, this)
                    launchImageCrop(imageUri)

                }

                else{
                    Toast.makeText(this,"Gagal OCR", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun historySetUp(){

        GlobalScope.async {

        val valueSatu = binding.ocrResultEt.text.toString()
        val valueDua = binding.ocrResultEtSechond.text.toString()
        val hasilHistory = binding.tvHasil.text.toString()
        val metode = metodePerhitungan.toString()

        dbHistory!!.HistoryItem().addHistory(DataHistory(hasilHistory,valueSatu,valueDua, metode))


        }

    }

    private fun setOnCheckedChangeListener() {


        binding.rgMetode.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            metodePerhitungan = radio.hint.toString()

            val valA = binding.ocrResultEt.text.toString().toInt()
            val valB = binding.ocrResultEtSechond.text.toString().toInt()

            when (checkedId) {
                R.id.rbKali -> {
                    val hasil = valA * valB
                    binding.tvHasil.text = hasil.toString()
                }
                R.id.rbBagi -> {
                    val hasil = valA / valB
                    binding.tvHasil.text = hasil.toString()
                }
                R.id.rbTambah -> {
                    val hasil = valA + valB
                    binding.tvHasil.text = hasil.toString()
                }
                R.id.rbKurang -> {
                    val hasil = valA - valB
                    binding.tvHasil.text = hasil.toString()
                }
                else -> {
                    Toast.makeText(this, "Masukkan Field Terlebih Dahulu", Toast.LENGTH_SHORT).show()
                }
            }

            Log.i(ContentValues.TAG, "onCheckedChangeListener: $metodePerhitungan")

        }

    }
    private fun firstNumber(){
        binding.gallery.setOnClickListener {
            if (checkPermission()) {

                pickFromGallery()
            }
            else{
                Toast.makeText(this, "Allow all permissions", Toast.LENGTH_SHORT).show()
                requestPermission()
            }
        }

        binding.camera.setOnClickListener {
            if (checkPermission()) {
                pickFromCamera()
            }
            else{
                Toast.makeText(this, "Allow all permissions", Toast.LENGTH_SHORT).show()
                requestPermission()
            }

        }

        binding.save.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission,WRITE_EXTERNAL_STORAGE_CODE)
                }
                else{
                    saveEditedImage()
                }
            }

        }

        binding.cancel.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Kamu Ingin Mengganti Foto?")
            builder.setPositiveButton("Iya") { dialog, which ->

                binding.image.visibility=View.VISIBLE
                binding.cancel.visibility=View.GONE
                binding.save.visibility=View.GONE
                binding.camera.visibility=View.VISIBLE
                binding.gallery.visibility=View.VISIBLE

            }
            builder.setNegativeButton("Tidak")
            { dialog, which -> }
            val alertDialog = builder.create()
            alertDialog.window?.setGravity(Gravity.BOTTOM)
            alertDialog.show()
        }

        binding.processImageBtn.setOnClickListener {
            processImage(binding.processImageBtn)
        }
        binding.processImageBtnSechond.setOnClickListener {
            processImageSechond(binding.processImageBtnSechond)
        }
    }



    //  first image proses
    fun processImage(v: View) {
        if (binding.image.drawable != null) {
            //  binding.ocrResultEt.setText("")
            v.isEnabled = false
            val bitmap = (binding.image.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    v.isEnabled = true
                    processResultText(firebaseVisionText)
                }
                .addOnFailureListener {
                    v.isEnabled = true
                    binding.ocrResultEt.text = "Failed"
                }
        } else {
            Toast.makeText(this, "Pilih Foto Terlebih Dahulu", Toast.LENGTH_LONG).show()
        }

    }

    // first choice
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }

                else{
                    Toast.makeText(this, "Gagal Crop Foto", Toast.LENGTH_SHORT).show()
                }
            }

        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri :Uri ?= UCrop.getOutput(data!!)

            setImage(resultUri!!)
            finalUri=resultUri

            binding.image.visibility= View.VISIBLE
            binding.save.visibility=View.VISIBLE
            binding.cancel.visibility=View.VISIBLE
            binding.camera.visibility=View.GONE
            binding.gallery.visibility=View.GONE


        }

    }

    // first number process ocr
    @SuppressLint("SetTextI18n")
    private fun processResultText(resultText: FirebaseVisionText) {
        if (resultText.textBlocks.size == 0) {
            binding.ocrResultEt.text = "Teks Tidak Ditemukan"
            return
        }
        for (block in resultText.textBlocks) {
            val blockText = block.text.trim()
            binding.ocrResultEt.text = blockText
        }
    }

    //  sechond image proses
    fun processImageSechond(v: View) {
        if (binding.image.drawable != null) {
            //  binding.ocrResultEt.setText("")
            v.isEnabled = false
            val bitmap = (binding.image.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    v.isEnabled = true
                    processResultTextSechond(firebaseVisionText)
                }
                .addOnFailureListener {
                    v.isEnabled = true
                    binding.ocrResultEtSechond.text = "Failed"
                }
        } else {
            Toast.makeText(this, "Pilih Foto Terlebih Dahulu", Toast.LENGTH_LONG).show()
        }

    }

    // sechond number process ocr
    @SuppressLint("SetTextI18n")
    private fun processResultTextSechond(resultText: FirebaseVisionText) {
        if (resultText.textBlocks.size == 0) {
            binding.ocrResultEt.text = "Teks Tidak Ditemukan"
            return
        }
        for (block in resultText.textBlocks) {
            val blockText = block.text.trim()
            binding.ocrResultEtSechond.text = blockText
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {

            WRITE_EXTERNAL_STORAGE_CODE -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    Toast.makeText(this, "Enable permissions", Toast.LENGTH_SHORT).show()
                }

            }



        }

    }


    private fun saveEditedImage() {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, finalUri)
        saveMediaToStorage(bitmap)

    }

    private fun saveEditedImageSechond() {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, finalUriSechond)
        saveMediaToStorage(bitmap)

    }


    private fun saveImage(image: Bitmap?, context: Context): Uri {
        var imageFolder=File(context.cacheDir,"images")
        var uri: Uri? = null
        try {
            imageFolder.mkdirs()
            var file:File= File(imageFolder,"captured_image.png")
            var stream:FileOutputStream= FileOutputStream(file)
            image?.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
            uri=FileProvider.getUriForFile(context.applicationContext,"com.rifqipadisiliwangi.assigmentandroid"+".provider",file)


        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }

        return uri!!

    }

    private fun pickFromGallery() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    private fun pickFromCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activityResultLauncher.launch(intent)
    }


    private fun launchImageCrop(uri: Uri) {
        var destination:String=StringBuilder(UUID.randomUUID().toString()).toString()
        var options:UCrop.Options=UCrop.Options()

        UCrop.of(Uri.parse(uri.toString()), Uri.fromFile(File(cacheDir,destination)))
            .withOptions(options)
            .withAspectRatio(0F, 0F)
            .useSourceImageAspectRatio()
            .withMaxResultSize(2000, 2000)
            .start(this)

    }

    private fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(binding.image)
    }


    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            100
        )
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved to Photos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}