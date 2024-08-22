package com.TLC_Developer.DataManager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.Post.EditBlogActivity
import com.TLC_Developer.Post.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList

// Adapter class for displaying blogs in the user's profile
class currentUserProfileBlogAdapter(private var blogDataSet: ArrayList<DataClass>) :
    RecyclerView.Adapter<currentUserProfileBlogAdapter.ViewHolder>() {

    // ViewHolder class to hold and recycle views
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userProfileImage: ImageView = view.findViewById(R.id.currentUser_userProfile)
        val userName: TextView = view.findViewById(R.id.currentUser_UserName)
        val blogTitle: TextView = view.findViewById(R.id.currentUser_ProfileBlogTitle)
        val blogDateAndTime: TextView = view.findViewById(R.id.currentUser_ProfileBlogDateTime)
        val blogEditAndReadButton: ImageButton = view.findViewById(R.id.currentUser_BlogEditButton)

        // Optional views commented out as they are not used
        /*val blogBgImageView: ImageView = view.findViewById(R.id.blogItemImageView)
        val blogTags: TextView = view.findViewById(R.id.blogTage)*/
    }

    // Create new ViewHolder instances
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for each item in the RecyclerView
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.blogitem_profile_version, viewGroup, false)
        return ViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the blog data for the current position
        val blog = blogDataSet[position]
        // Set the blog title and writer's name
        viewHolder.blogTitle.text = blog.BlogTitle
        viewHolder.userName.text = blog.BlogWriterName
        // Format and set the date and time
        stringToDate(blog.BlogDateAndTime, viewHolder)
        // Load the user's profile image using Picasso
        Picasso.get().load(blog.BlogUserProfileUrl).into(viewHolder.userProfileImage)

        // if current user is on profile or other user
        val user = Firebase.auth.currentUser

        if(user?.uid.toString()!=blog.BlogUserID){
            viewHolder.blogEditAndReadButton.visibility=View.GONE
        }

        viewHolder.blogEditAndReadButton.setOnClickListener {
            openBlogToEdit(blog.BlogDocumentID, viewHolder)
        }


    }

    // Return the total number of items in the data set
    override fun getItemCount(): Int {
        return blogDataSet.size
    }

    // Format the date and time to a readable format
    @SuppressLint("SetTextI18n")
    private fun stringToDate(dateString: String, viewHolder: ViewHolder) {
        // Define the date format
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        // Parse the date string to a Date object
        val publishDate: Date = formatter.parse(dateString) ?: Date()
        // Calculate the time difference between now and the publication date
        val diff = System.currentTimeMillis() - publishDate.time

        // Set the appropriate text based on the time difference
        viewHolder.blogDateAndTime.text = when {
            diff < DateUtils.DAY_IN_MILLIS -> "Recently" // Less than a day ago
            diff < 2 * DateUtils.DAY_IN_MILLIS -> "Yesterday" // Less than two days ago
            else -> dateString // Default to the original date string
        }
    }

    // Open the EditBlogActivity to edit the blog post
    private fun openBlogToEdit(blogDocumentID: String, holder: ViewHolder) {
        val context = holder.itemView.context
        val intent = Intent(context, EditBlogActivity::class.java)
        // Pass the blog document ID to the EditBlogActivity
        intent.putExtra("dataToEdit", blogDocumentID)
        context.startActivity(intent) // Start the EditBlogActivity
    }
}
