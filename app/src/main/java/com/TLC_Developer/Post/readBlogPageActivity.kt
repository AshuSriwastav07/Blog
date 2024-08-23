package com.TLC_Developer.Post

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.TLC_Developer.Post.databinding.ActivityReadBlogPageBinding
import com.squareup.picasso.Picasso

class readBlogPageActivity : AppCompatActivity() {

    // Declare the binding variable
    private lateinit var binding: ActivityReadBlogPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding
        binding = ActivityReadBlogPageBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onStart() {
        super.onStart()
// Retrieve the blog data passed through the intent
        val blogData: ArrayList<String>? = intent?.getStringArrayListExtra("blogData")

        // Use binding to access the views and set the data if needed
        if (blogData != null) {
            binding.BlogReadingTitle.text=blogData.get(0)
            binding.BlogReadingDateTime.text=blogData.get(1)
            Picasso.get().load(blogData.get(2)).into(binding.BlogReadingImageView)
            val result = Html.fromHtml(blogData.get(3));
            binding.BlogReadingBody.text=result
            binding.BlogReadingWriterName.text=blogData.get(5)
            Picasso.get()
                .load(blogData.get(4))
                .error(R.mipmap.profileicon)
                .placeholder(R.mipmap.profileicon)
                .into(binding.readBlogUserProfileImage)


//            Log.d("BlogAdapterLogs",blogData.get(4))



        }

    }
}
