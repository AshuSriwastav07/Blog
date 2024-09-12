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
import com.TLC_Developer.functions.functionsManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date

class WriteBlogPage : AppCompatActivity() {
    private lateinit var binding: ActivityWriteBlogPageBinding
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri: Uri? = null
    private lateinit var BGImageUrl: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteBlogPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Get the current user's information
        val userID = FirebaseAuth.getInstance().currentUser?.email.toString()
        val profilePictureUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
        var blogWriterName:String= FirebaseAuth.getInstance().currentUser?.displayName.toString()

        //getUserName from userData Collections

        val docRef = firebaseFirestore.collection("usersDetails").document(userID)
        docRef.get()
            .addOnSuccessListener { document ->
                blogWriterName= document.getString("userName").toString()
            }
            .addOnFailureListener { exception ->
                Log.d("functionManagerLogs-showDataInOnlyViewProfile",exception.toString())
            }

        // Initialize views and Firebase
        val TitleTextView: EditText = findViewById(R.id.BlogTitleEditText)
        val BodyTextView = findViewById<TextView>(R.id.BlogBodyEditText)
        val TagsTextView = findViewById<TextView>(R.id.BlogHashTagsEditText)
        val progressBar = findViewById<ProgressBar>(R.id.BlogWriteprogressBar)
        BGImageUrl = findViewById(R.id.EnterBGImageUrl)

        //empty Array
        val commentAndLikesArray:ArrayList<String> = arrayListOf(

        )



        // Set up the publish button to trigger blog publishing
        binding.publishBlogButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            // Prepare the blog data to be uploaded
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
                "BlogDateAndTime" to currentDate,
                "BlogUserProfileUrl" to profilePictureUrl,
                "userName" to blogWriterName,
                "comments" to commentAndLikesArray,
                "likedBy" to commentAndLikesArray
            )

            // Call the function to upload the blog data
            uploadBlogData(blogData)
        }

        // Initialize Firebase storage and Firestore references
        initVars()

        // Set up the image uploader
        binding.imageUploder.setOnClickListener {
            resultLauncher.launch("image/*")
        }
    }

    // Function to initialize Firebase Storage and Firestore references
    private fun initVars() {
        storageRef = FirebaseStorage.getInstance().reference.child("BlogImages")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    // Result launcher to handle image selection from the gallery
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.imageUploder.setImageURI(it) // Set the selected image to the ImageView
    }

    // Function to upload blog data (including handling image upload if applicable)
    private fun uploadBlogData(blogData: HashMap<String, Any>) {

        // Check if an image was selected
        if (imageUri != null) {
            // User selected an image, upload it first
            storageRef = storageRef.child(System.currentTimeMillis().toString())
            storageRef.putFile(imageUri!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the download URL of the uploaded image
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Add the image URL to the blog data
                        blogData["BlogImageURL"] = uri.toString()

                        // Upload the blog data to Firestore
                        uploadBlogToFirestore(blogData)
                    }
                } else {
                    // Handle image upload failure
                    showUploadFailure()
                }
            }
        } else if (BGImageUrl.text?.isNotEmpty() == true) {
            // No image selected but user provided an image URL
            blogData["BlogImageURL"] = BGImageUrl.text.toString()

            // Upload the blog data to Firestore
            uploadBlogToFirestore(blogData)
        } else {
            // No image or URL provided, upload the blog data without an image URL
            uploadBlogToFirestore(blogData)
        }
    }

    // Function to upload the blog data to FireStore
    private fun uploadBlogToFirestore(blogData: HashMap<String, Any>) {
        if(functionsManager().containsRestrictedWords(blogData["body"].toString())){
            Toast.makeText(this,"Your blog contains content that violates our community guidelines.",Toast.LENGTH_LONG).show()
            binding.BlogWriteprogressBar.visibility = View.GONE

        }else{

        firebaseFirestore.collection("BlogsData")
            .add(blogData)
            .addOnCompleteListener { blogDataStatus ->
                if (blogDataStatus.isSuccessful) {
                    clearUIAfterSuccess()
                    Toast.makeText(this, "Blog is Published", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    showUploadFailure()
                }
            }
            .addOnFailureListener {
                showUploadFailure()
            }
        }
    }

    // Function to clear UI elements after successful upload
    private fun clearUIAfterSuccess() {
        binding.imageUploder.setImageResource(R.mipmap.imageupload)
        binding.BlogBodyEditText.text?.clear()
        binding.BlogTitleEditText.text?.clear()
        binding.BlogHashTagsEditText.text?.clear()
        binding.BlogWriteprogressBar.visibility = View.GONE
    }

    // Function to handle upload failures
    private fun showUploadFailure() {
        Toast.makeText(this, "Blog is not Published", Toast.LENGTH_LONG).show()
        binding.BlogWriteprogressBar.visibility = View.GONE
    }
}
