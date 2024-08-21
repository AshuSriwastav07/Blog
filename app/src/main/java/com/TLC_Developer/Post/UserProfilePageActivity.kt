package com.TLC_Developer.Post

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.DataManager.DataClass
import com.TLC_Developer.DataManager.currentUserProfileBlogAdapter
import com.TLC_Developer.Post.databinding.ActivityUserProfilePageBinding
import com.TLC_Developer.functions.function
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class UserProfilePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfilePageBinding
    private val firebaseAuth = Firebase.auth.currentUser
    private val currentUserID = firebaseAuth?.uid.toString()
    private var db = Firebase.firestore
    private var userBlogListData: ArrayList<DataClass> = ArrayList()
    private val TAG = "UserProfileLogs"
    private lateinit var blogRecyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: currentUserProfileBlogAdapter
    private val functionCalls=function()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUserName = firebaseAuth?.displayName.toString()
        val currentUserEmail = firebaseAuth?.email.toString()
        val currentUserProfileImage = firebaseAuth?.photoUrl.toString()
        val profileImageImageView: ImageView = findViewById(R.id.ProfilePageProfileImage)

        binding.ProfilePageUserName.text = currentUserName
        binding.ProfilePageUserEmail.text = currentUserEmail
        Picasso.get().load(currentUserProfileImage).into(profileImageImageView)

        blogRecyclerView = findViewById(R.id.currentUserProfileBlogsRecyclerview)
        val viewManager = LinearLayoutManager(this)

        // Adapter Connect
       recyclerViewAdapter = currentUserProfileBlogAdapter(userBlogListData)
        blogRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = recyclerViewAdapter // Use the instance you created
        }



        // Load data
        userProfileBlogData()
    }

    private fun userProfileBlogData() {
        db.collection("BlogsData")
            .whereEqualTo("userID", currentUserID) // Updated to match the field
            .get()
            .addOnSuccessListener { documents ->
                userBlogListData.clear()  // Clear ArrayList
                for (document in documents) {
                    val dataModel = DataClass(
                        BlogTitle = document.getString("title") ?: "",
                        BlogBody = document.getString("body") ?: "",
                        BlogTags = document.getString("tags") ?: "",
                        BlogUserID = document.getString("userID") ?: "",
                        BlogDateAndTime = document.getString("BlogDateAndTime") ?: "",
                        BlogImageURL = document.getString("BlogImageURL") ?: "",
                        BlogWriterName = document.getString("writerName") ?: "",
                        BlogUserProfileUrl = document.getString("BlogUserProfileUrl") ?: ""
                    )
                    userBlogListData.add(dataModel)  // Add data to ArrayList

//                    Log.d(TAG, currentUserID)
//                    Log.d(TAG, dataModel.BlogTitle)
                    // Sort the blogs by date

                    userBlogListData.sortByDescending {
                        functionCalls.convertStringToDate(it.BlogDateAndTime)?.time
                    }
                }
                recyclerViewAdapter.notifyDataSetChanged() // Notify the adapter of data changes
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}
