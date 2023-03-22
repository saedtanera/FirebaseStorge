package com.example.firebasestorage

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION_CODES.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {


    lateinit var storageReference: StorageReference
    lateinit var databaseReference:DatabaseReference
    lateinit var ref:StorageReference
    lateinit var ett: EditText
    var FileUri: Uri? =null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        ett=findViewById(R.id.editTextTextPersonName)
        storageReference =FirebaseStorage.getInstance().reference
        databaseReference=FirebaseDatabase.getInstance().getReference("Uploads")

        findViewById<Button>(R.id.button).setOnClickListener {

            if(!ett.text.isEmpty()) {
                selectFiles()
            }else {
                ett.setError("Please put your fiel name")
            }
        }
        findViewById<Button>(R.id.button2).setOnClickListener {
            download()
        }

    }

    private fun download() {
        ett=findViewById<EditText>(R.id.editTextTextPersonName)
        var Fname=ett.text.toString()
        storageReference= FirebaseStorage.getInstance().getReference()
        ref=storageReference.child("$Fname.pdf")
        ref.downloadUrl.addOnCompleteListener {
            var url =it.result.toString()
            downloadFile(this,"$Fname",".pdf",DIRECTORY_DOWNLOADS,url)
            Toast.makeText(this,"Download Succeded",Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
        }

    }

    private fun downloadFile(context: Context, fileName:String, fileExtention:String, distansDirctory:String, url:String) {
        var downloadmanager =context.applicationContext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        var uri = Uri.parse(url)
        var request = DownloadManager.Request(uri)

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir (context, distansDirctory, fileName + fileExtention)

        downloadmanager.enqueue(request)
    }

    private fun selectFiles() {
        var intent = Intent()
        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT

        galleryIntent.type = "application/pdf"
        startActivityForResult(galleryIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==1&&resultCode== RESULT_OK&&data!=null&&data.data!=null){
            var dial= ProgressDialog(this)
            dial.setMessage("Uploading..")
            dial.show()
            FileUri=data.data!!
            Toast.makeText(this, FileUri.toString(), Toast.LENGTH_SHORT).show()
            val filepath =storageReference.child(
                findViewById<EditText>(R.id.editTextTextPersonName).text.toString()
                    + "." + "pdf")

            Toast.makeText(this, filepath.getName(), Toast.LENGTH_SHORT).show()
            filepath.putFile(FileUri!!).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }

                }

                storageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    dial.dismiss()
                    val uri = task.result
                    val myurl: String
                    myurl = uri.toString()
                    Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    dial.dismiss()
                    Toast.makeText(this, "UploadedFailed", Toast.LENGTH_SHORT).show()

                }
            }


        }
    }
}














































//
//
//        val storage = Firebase.storage
//        val ref = storage.reference
//        val Choose = findViewById<Button>(R.id.button1)
//        val Upload = findViewById<Button>(R.id.button2)
//
//        Choose.setOnClickListener {
//            val intent =  Intent(Intent.ACTION_CREATE_DOCUMENT, MediaStore.Downloads.EXTERNAL_CONTENT_URI)
//
//            startActivityForResult(intent, reqCode)
//            intent.setType(application/pdf)
//
//
//
//
//
//        }
//
//    }
//
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == reqCode) {
//            filePath = data!!.data!!
//            val bitHap = MediaStore.Files.FileColumns.getBitmap(contentResolver, filePath)
//
//
//        }

