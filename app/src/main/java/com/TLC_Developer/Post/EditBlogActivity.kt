package com.TLC_Developer.Post

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.TLC_Developer.Post.databinding.ActivityEditBlogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditBlogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBlogBinding
    private val TAG = "EditPageLogs"
    private lateinit var blogData: HashMap<String, Any>
    private var imageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var documentID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Get Document ID passed via Intent (Used to identify the blog post to edit)
        documentID = intent?.extras?.getString("documentID_forDataToEdit").toString()
        Log.d(TAG, "Editing document ID: $documentID")

        // Fetch existing blog data to pre-fill the form for editing
        fetchBlogData()

        // Initialize Firebase Storage reference
        initFirebaseStorage()

        // Set the action for the update button
        binding.publishEditBlogButton.setOnClickListener {
            updateBlog()
        }

        // Set the action for the image upload button
        binding.EditImageUpload.setOnClickListener {
            resultLauncher.launch("image/*")
        }
    }

    // Function to initialize Firebase Storage references
    private fun initFirebaseStorage() {
        storageRef = FirebaseStorage.getInstance().reference.child("BlogImages")
    }

    // Function to fetch blog data and populate UI
    private fun fetchBlogData() {
        val docRef = firebaseFirestore.collection("BlogsData").document(documentID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Set existing data into the respective UI elements
                    binding.EditBlogTitleEditText.setText(document.getString("title"))
                    binding.EditBlogBodyEditText.setText(document.getString("body"))
                    binding.EditBlogHashTagsEditText.setText(document.getString("tags"))

                    // Load the existing image using Picasso
                    Picasso.get()
                        .load(document.getString("BlogImageURL"))
                        .placeholder(R.drawable.baseline_person_24) // Placeholder while loading
                        .error(R.drawable.baseline_person_24) // Error image if load fails
                        .into(binding.EditImageUpload, object : Callback {
                            override fun onSuccess() {
                                // Handle success
                            }

                            override fun onError(e: Exception?) {
                                Log.e(TAG, "Picasso image loading failed: ${e?.message}")
                            }
                        })
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Failed to fetch document: ", exception)
            }
    }

    // Function to handle blog update
    private fun updateBlog() {
        val title = binding.EditBlogTitleEditText.text.toString().trim()
        val body = binding.EditBlogBodyEditText.text.toString().trim()
        val tags = binding.EditBlogHashTagsEditText.text.toString().trim()

        if (title.isEmpty() || body.isEmpty() || tags.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Show the progress bar during the upload process
        binding.BlogWriteprogressBar.visibility = View.VISIBLE

        // Prepare blog data
        val currentDate = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault()).format(Date())
        val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val profilePictureUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()

        blogData = hashMapOf(
            "title" to title,
            "body" to body,
            "tags" to tags,
            "userID" to userID,
            "BlogDateAndTime" to currentDate,
            "BlogUserProfileUrl" to profilePictureUrl,
            "documentID" to documentID
        )

        if (imageUri != null) {
            // User selected a new image, upload it first
            uploadImageAndSaveBlog()
        } else {
            // No new image selected, just update the document
            updateBlogInFirestore(blogData)
        }
    }

    // Function to upload the image and then save the blog data
    private fun uploadImageAndSaveBlog() {
        storageRef = storageRef.child(System.currentTimeMillis().toString())
        storageRef.putFile(imageUri!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        blogData["BlogImageURL"] = uri.toString()
                        updateBlogInFirestore(blogData)
                    }
                } else {
                    showToastAndHideProgressBar("Image upload failed")
                }
            }
    }

    // Helper function to update the blog data in Firestore
    private fun updateBlogInFirestore(blogData: HashMap<String, Any>) {
        firebaseFirestore.collection("BlogsData").document(documentID)
            .update(blogData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToastAndHideProgressBar("Blog updated successfully")
                    clearUIAfterSuccess()
                    finish() // Close the activity
                } else {
                    showToastAndHideProgressBar("Failed to update blog")
                }
            }
            .addOnFailureListener {
                showToastAndHideProgressBar("Failed to update blog")
            }
    }

    // Function to clear UI elements after successful update
    private fun clearUIAfterSuccess() {
        binding.EditImageUpload.setImageResource(R.mipmap.imageupload)
        binding.EditBlogBodyEditText.text?.clear()
        binding.EditBlogTitleEditText.text?.clear()
        binding.EditBlogHashTagsEditText.text?.clear()
    }

    // Helper function to show a toast message and hide the progress bar
    private fun showToastAndHideProgressBar(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        binding.BlogWriteprogressBar.visibility = View.GONE
    }

    // Result launcher for image selection from the gallery
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        binding.EditImageUpload.setImageURI(uri)
    }
}
