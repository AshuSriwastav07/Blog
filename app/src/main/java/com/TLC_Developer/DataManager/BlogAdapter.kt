package com.TLC_Developer.DataManager

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.Post.R
import com.TLC_Developer.functions.function
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

// Adapter class for displaying blog posts in a RecyclerView
class BlogAdapter(private var blogDataSet: ArrayList<DataClass>,private var context: Context) :
    RecyclerView.Adapter<BlogAdapter.ViewHolder>() {

    // ViewHolder class to hold and recycle views
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val blogTitle: TextView = view.findViewById(R.id.blogItemTitleTextview) // Title of the blog
        val blogDateAndTime: TextView = view.findViewById(R.id.blogItemDateTimeTextview) // Date and time of the blog
        val blogBgImageView: ImageView = view.findViewById(R.id.blogItemImageView) // Background image of the blog
        val userProfileImage: ImageView = view.findViewById(R.id.userBlogProfileImage) // User's profile image
        val userName: TextView = view.findViewById(R.id.blogWriterName) // Writer's name
        val blogTags: TextView = view.findViewById(R.id.blogTage) // Tags associated with the blog
        val blogWriterMiniProfileCardView:CardView=view.findViewById(R.id.blogWriterMiniProfileCardview)
    }

    // Create new ViewHolder instances
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for each blog item in the RecyclerView
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.blog_item_view, viewGroup, false)
        return ViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the blog data for the current position
        val blog = blogDataSet[position]
        // Set the blog title and writer's name
        viewHolder.blogTitle.text = blog.BlogTitle
        viewHolder.userName.text = blog.BlogWriterName
        viewHolder.blogTags.text = blog.BlogTags
        // Format and set the date and time
        stringToDate(blog.BlogDateAndTime, viewHolder)
        // Load the blog's background image and user's profile image using Picasso

        if(blog.BlogImageURL==""){
            Picasso.get().load("https://cdn.vectorstock.com/i/500p/82/99/no-image-available-like-missing-picture-vector-43938299.jpg").into(viewHolder.blogBgImageView)

        }else{
            Picasso.get().load(blog.BlogImageURL).into(viewHolder.blogBgImageView)
        }

        Picasso.get().load(blog.BlogUserProfileUrl).into(viewHolder.userProfileImage)

        viewHolder.blogWriterMiniProfileCardView.setOnClickListener {
            function().openProfileSection(blog.BlogUserID,context)
        }

    }

    // Return the total number of items in the data set
    override fun getItemCount(): Int {
        return blogDataSet.size
    }

    // Format the date and time to a readable format
    @SuppressLint("SetTextI18n")
    private fun stringToDate(dateString: String, viewHolder: ViewHolder) {
        // Define the date format used in the blog's date string
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


}
