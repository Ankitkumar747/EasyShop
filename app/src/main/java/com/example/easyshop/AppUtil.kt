package com.example.easyshop

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

object AppUtil {
    fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun addItemToCart(context: Context, productId: String){
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            showToast(context, "User not logged in")
            return
        }

        val userDoc = Firebase.firestore.collection("users")
            .document(currentUser.uid)

        userDoc.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val docSnapshot = task.result
                val currentCart = if (docSnapshot != null && docSnapshot.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    docSnapshot.get("cartItems") as? Map<String, Long> ?: emptyMap()
                } else {
                    emptyMap()
                }

                val currentQuantity = currentCart[productId] ?: 0
                val updatedQuantity = currentQuantity + 1

                val updatedCart = mapOf("cartItems.$productId" to updatedQuantity)

                userDoc.update(updatedCart).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        showToast(context, "Item added to cart")
                    } else {
                        userDoc.set(mapOf("cartItems" to mapOf(productId to updatedQuantity)), SetOptions.merge())
                            .addOnCompleteListener { setTasks ->
                                if (setTasks.isSuccessful) {
                                    showToast(context, "Item added to cart")
                                } else {
                                    showToast(context, "Failed to add item to cart")
                                }
                            }
                    }
                }
            } else {
                showToast(context, "Failed to fetch user data")
            }
        }
    }
}
