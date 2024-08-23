package com.TLC_Developer.Post

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.TLC_Developer.Post.databinding.ActivityEditBlogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date

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

        // Initialize FirebaseFirestore
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Bind views to variables
        val titleTextview = findViewById<EditText>(R.id.EditBlogTitleEditText)
        val bodyTextview = findViewById<EditText>(R.id.EditBlogBodyEditText)
        val tagsTextview = findViewById<EditText>(R.id.EditBlogHashTagsEditText)
        val imageViewForBG = findViewById<ImageView>(R.id.EditImageUpload)
        val updateBlogButton = findViewById<Button>(R.id.publishEditBlogButton)

        // Get the current user's ID and profile information
        val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val profilePictureUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()

        // Get Document ID passed via Intent (Used to identify the blog post to edit)
        documentID = intent?.extras?.getString("documentID_forDataToEdit").toString()
        Log.d(TAG, documentID)

        // Fetch existing blog data to pre-fill the form for editing
        val docRef = firebaseFirestore.collection("BlogsData").document(documentID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Set existing data into the respective UI elements
                    titleTextview.setText(document.getString("title"))
                    bodyTextview.setText(document.getString("body"))
                    tagsTextview.setText(document.getString("tags"))
                    // Load the existing image using Picasso
                    Picasso.get().load(document.getString("BlogImageURL")).into(imageViewForBG)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        // Set the action for the update button
        updateBlogButton.setOnClickListener {
            // Prepare the updated blog data
            val title: String = titleTextview.text.toString()
            val body: String = bodyTextview.text.toString()
            val tags: String = tagsTextview.text.toString()
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
            val currentDate = sdf.format(Date())

            // Create a map to store the updated data
            blogData = hashMapOf(
                "title" to title,
                "body" to body,
                "tags" to tags,
                "userID" to userID,
                "BlogDateAndTime" to currentDate,
                "BlogUserProfileUrl" to profilePictureUrl,
                "documentID" to documentID
            )

            // Call the function to upload the updated data
            uploadBlogData(blogData, documentID)
        }

        // Initialize Firebase Storage reference
        initVars()

        // Set the action for the image upload button
        binding.EditImageUpload.setOnClickListener {
            resultLauncher.launch("image/*")
        }
    }

    // Function to initialize Firebase Storage references
    private fun initVars() {
        storageRef = FirebaseStorage.getInstance().reference.child("BlogImages")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    // Result launcher for image selection from the gallery
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.EditImageUpload.setImageURI(it)
    }

    // Function to upload blog data (including handling image upload if applicable)
    private fun uploadBlogData(blogData: HashMap<String, Any>, documentID: String) {
        val databaseRef = firebaseFirestore

        if (imageUri != null) {
            // User selected a new image, upload it first
            storageRef = storageRef.child(System.currentTimeMillis().toString())
            storageRef.putFile(imageUri!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Add the new image URL to the blog data
                        blogData["BlogImageURL"] = uri.toString()
                        // Update the document in Firestore
                        updateBlogInFirestore(databaseRef, blogData, documentID)
                    }
                } else {
                    // Handle image upload failure
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_LONG).show()
                    binding.BlogWriteprogressBar.visibility = View.GONE
                }
            }
        } else {
            // No new image selected, just update the document
            updateBlogInFirestore(databaseRef, blogData, documentID)
        }
    }

    // Helper function to update the blog data in Firestore
    private fun updateBlogInFirestore(databaseRef: FirebaseFirestore, blogData: HashMap<String, Any>, documentID: String) {
        databaseRef.collection("BlogsData").document(documentID)
            .update(blogData)
            .addOnCompleteListener { blogDataStatus ->
                if (blogDataStatus.isSuccessful) {
                    // Clear the UI after successful update
                   clearUIAfterSuccess()

                    // Notify the user of success
                    Toast.makeText(this, "Blog is Updated", Toast.LENGTH_LONG).show()
                    binding.BlogWriteprogressBar.visibility = View.GONE
                    finish()
                } else {
                    // Notify the user of failure
                    Toast.makeText(this, "Blog is not Updated", Toast.LENGTH_LONG).show()
                    binding.BlogWriteprogressBar.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                // Handle the failure case
                Toast.makeText(this, "Blog is not Updated", Toast.LENGTH_LONG).show()
                binding.BlogWriteprogressBar.visibility = View.GONE
            }
    }

    // Function to clear UI elements after successful upload
    private fun clearUIAfterSuccess() {
        binding.EditImageUpload.setImageResource(R.mipmap.imageupload)
        binding.EditBlogBodyEditText.text?.clear()
        binding.EditBlogTitleEditText.text?.clear()
        binding.EditBlogHashTagsEditText.text?.clear()
        binding.BlogWriteprogressBar.visibility = View.GONE
    }
}
