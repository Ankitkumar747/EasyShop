package com.example.easyshop

import android.widget.Toast
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
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

        userDoc.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val doc = task.result
                
                @Suppress("UNCHECKED_CAST")
                val existingCart = doc?.get("cartItems") as? Map<String, Any> ?: emptyMap()
                val mutableCart = existingCart.toMutableMap()

                val currentQuantity = when (val value = mutableCart[productId]) {
                    is Number -> value.toLong()
                    is String -> value.toLongOrNull() ?: 0L
                    else -> 0L
                }

                val updatedQuantity = currentQuantity + 1
                mutableCart[productId] = updatedQuantity

                userDoc.set(mapOf("cartItems" to mutableCart), SetOptions.merge()).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        showToast(context, "Item added to cart")
                    } else {
                        showToast(context, "Failed to add item to cart")
                    }
                }
            } else {
                showToast(context, "Failed to fetch user data")
            }
        }
    }
}
