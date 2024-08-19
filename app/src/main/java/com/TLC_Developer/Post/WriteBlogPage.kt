package com.TLC_Developer.Post

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.TLC_Developer.Post.databinding.ActivityWriteBlogPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date

class WriteBlogPage : AppCompatActivity() {
    private lateinit var binding: ActivityWriteBlogPageBinding
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteBlogPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val userName = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        val databaseRef = Firebase.firestore

        val TitleTextView: EditText = findViewById(R.id.BlogTitleEditText)
        val BodyTextView = findViewById<TextView>(R.id.BlogBodyEditText)
        val TagsTextView = findViewById<TextView>(R.id.BlogHashTagsEditText)
        val progressBar=findViewById<ProgressBar>(R.id.BlogWriteprogressBar)


        binding.publishBlogButton.setOnClickListener {
            progressBar.visibility=View.VISIBLE

            Log.d("DataToStore", TitleTextView.text.toString())

            val title: String = TitleTextView.text.toString()
            val body: String = BodyTextView.text.toString()
            val tags: String = TagsTextView.text.toString()
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
            val currentDate = sdf.format(Date())
            val blogData: HashMap<String, Any> = hashMapOf(
                "title" to title,
                "body" to body,
                "tags" to tags,
                "userID" to userID,
                "writerName" to userName,
                "BlogDateAndTime" to currentDate,

            )

            UploadImage(blogData)

        }

        //code for Image Upload
        initVars()

        binding.imageUploder.setOnClickListener {
            resultLauncher.launch("image/*")
        }

    }

    //Code for Image Upload

    private fun initVars() {
        storageRef = FirebaseStorage.getInstance().reference.child("BlogImages")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.imageUploder.setImageURI(it)
        binding.imageUploder.scaleY=2.0f

    }

    private fun UploadImage(blogData: HashMap<String, Any>) {
        val databaseRef=firebaseFirestore
        storageRef = storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let {
            storageRef.putFile(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        blogData["BlogImageURL"]=uri.toString()
                        databaseRef.collection("BlogsData")
                            .add(blogData)
                            .addOnCompleteListener { blogDataStatus ->
                                if(blogDataStatus.isSuccessful) {

                                    binding.imageUploder.setImageResource(R.mipmap.imageupload)
                                    binding.BlogBodyEditText.text?.clear()
                                    binding.BlogTitleEditText.text?.clear()
                                    binding.BlogHashTagsEditText.text?.clear()

                                    Toast.makeText(this, "Blog is Published", Toast.LENGTH_LONG)
                                        .show()
                                    binding.BlogWriteprogressBar.visibility=View.GONE

                                }else{
                                    Toast.makeText(this, "Blog is not Published", Toast.LENGTH_LONG).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Blog is not Published", Toast.LENGTH_LONG).show()
                            }

                    }

                }


            }
        }
    }
}