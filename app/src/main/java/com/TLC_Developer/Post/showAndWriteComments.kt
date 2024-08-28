package com.TLC_Developer.Post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.TLC_Developer.functions.functionsManager

class showAndWriteComments : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_show_and_write_comments, container, false)
        val commentListView:ListView=view.findViewById(R.id.commentListView)
        val enterComment:EditText=view.findViewById(R.id.typeComment)
        val blogId = arguments?.getString("blogId").toString()
        val userName = arguments?.getString("blogUserName").toString()
        val submitComment:Button=view.findViewById(R.id.submitComment)

        functionsManager().getComments(requireContext(),blogId,commentListView,enterComment,submitComment,userName)

        return view
    }

}
