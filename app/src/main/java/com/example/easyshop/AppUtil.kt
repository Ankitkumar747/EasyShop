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

    fun addItemToCart(context: Context, productId: String) {
        addToCart(context, productId)
    }

    fun addToCart(context: Context, productId : String){
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            showToast(context, "User not logged in")
            return
        }

        val userDoc = Firebase.firestore.collection("users")
            .document(currentUser.uid)

        userDoc.get().addOnCompleteListener {
            if(it.isSuccessful){
                @Suppress("UNCHECKED_CAST")
                val currentCart = it.result?.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[productId] ?: 0
                val updatedQuantity = currentQuantity + 1

                val updatedCart = mapOf("cartItems.$productId" to updatedQuantity)

                userDoc.set(updatedCart, SetOptions.merge())
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            showToast(context, "Item added to the cart")
                        }else{
                            showToast(context, "Failed adding item to the cart")
                        }
                    }
            } else {
                showToast(context, "Failed to fetch user data")
            }
        }
    }
}
