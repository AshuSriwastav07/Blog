package com.TLC_Developer.DataManager

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.Post.R
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class currentUserProfileBlogAdapter(private var blogDataSet: ArrayList<DataClass>) :
    RecyclerView.Adapter<currentUserProfileBlogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userProfileImage: ImageView = view.findViewById(R.id.currentUser_userProfile)
        val userName: TextView = view.findViewById(R.id.currentUser_UserName)
        val blogTitle: TextView = view.findViewById(R.id.currentUser_ProfileBlogTitle)
        val blogDateAndTime: TextView = view.findViewById(R.id.currentUser_ProfileBlogDateTime)
        /*val blogBgImageView: ImageView = view.findViewById(R.id.blogItemImageView)
        val blogTags: TextView = view.findViewById(R.id.blogTage)*/
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.blogitem_profile_version, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val blog = blogDataSet[position]
        viewHolder.blogTitle.text = blog.BlogTitle
        viewHolder.userName.text = blog.BlogWriterName
//        viewHolder.blogTags.text = blog.BlogTags
        stringToDate(blog.BlogDateAndTime, viewHolder)
//        Picasso.get().load(blog.BlogImageURL).into(viewHolder.blogBgImageView)
        Picasso.get().load(blog.BlogUserProfileUrl).into(viewHolder.userProfileImage)
    }


    override fun getItemCount(): Int {
        return blogDataSet.size
    }

    @SuppressLint("SetTextI18n")
    private fun stringToDate(dateString: String, viewHolder: ViewHolder) {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val publishDate: Date = formatter.parse(dateString) ?: Date()
        val diff = System.currentTimeMillis() - publishDate.time

        viewHolder.blogDateAndTime.text = when {
            diff < DateUtils.DAY_IN_MILLIS -> "Recently"
            diff < 2 * DateUtils.DAY_IN_MILLIS -> "Yesterday"
            else -> dateString
        }
    }
}