package com.example.easyshop

import android.widget.Toast
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

object AppUtil {
    fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun addItemToCart(context: Context, productId : String){
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            showToast(context, "User not logged in")
            return
        }

        val userDoc = Firebase.firestore.collection("users")
            .document(currentUser.uid)

        val updates = mapOf("cartItems.$productId" to FieldValue.increment(1))

        userDoc.set(updates, SetOptions.merge()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast(context, "Item added to cart")
            } else {
                showToast(context, "Failed to add item to cart")
            }
        }
    }
}
