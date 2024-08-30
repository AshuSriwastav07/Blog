package com.TLC_Developer.functions

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.TLC_Developer.Post.SetupProfilePageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ManageSignUpAndProfile {

    private val FirebaseDB=FirebaseFirestore.getInstance()
    val documentName = FirebaseAuth.getInstance().currentUser?.email.toString()
    // User email as document name

    fun checkUserProfileIsComplete(context: Context){

        FirebaseDB.collection("usersDetails").document(documentName).get()
            .addOnSuccessListener { data ->

                    Toast.makeText(context,"Complete Your Profile Now",Toast.LENGTH_SHORT).show()

                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            val intent= Intent(context,SetupProfilePageActivity::class.java)
                            context.startActivity(intent)
                        },
                        3000 // value in milliseconds
                    )
                }

            }
    }
