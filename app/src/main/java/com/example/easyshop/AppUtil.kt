package com.example.easyshop

import android.app.Activity
import android.widget.Toast
import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.core.content.edit
import com.example.easyshop.model.OrderModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.razorpay.Checkout
import org.json.JSONObject
import java.util.Locale
import java.util.UUID

object AppUtil {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun addToCart(context: Context, productId: String) {

        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[productId] ?: 0
                val updatedQuantity = currentQuantity + 1

                val updatedCart = mapOf("cartItems.$productId" to updatedQuantity)

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showToast(context, "Item added to the cart")
                        } else {
                            showToast(context, "Failed adding item to the cart")
                        }
                    }
            }
        }
    }

    fun removeFromCart(context: Context, productId: String, removeAll: Boolean = false) {

        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[productId] ?: 0
                val updatedQuantity = currentQuantity - 1

                val updatedCart = if (updatedQuantity <= 0 || removeAll) {
                    mapOf("cartItems.$productId" to FieldValue.delete())
                } else {
                    mapOf("cartItems.$productId" to updatedQuantity)
                }

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showToast(context, "Item remove to the cart")
                        } else {
                            showToast(context, "Failed removing item to the cart")
                        }
                    }
            }
        }
    }

    fun clearCartAndAddToOrder() {
        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val order = OrderModel(
                    id = "ORD_" + UUID.randomUUID().toString().replace("-", "").take(10)
                        .uppercase(),
                    userId = FirebaseAuth.getInstance().currentUser?.uid!!,
                    date = Timestamp.now(),
                    items = currentCart,
                    status = "ORDERED",
                    address = it.result.get("address") as String
                )

                Firebase.firestore.collection("orders").document(order.id).set(order)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            userDoc.update("cartItems", FieldValue.delete())
                        }
                    }
            }
        }
    }

    fun getDiscountPercentage(): Float {
        return 10.0f
    }

    fun getTaxPercentage(): Float {
        return 13.0f
    }

    fun razorpayApiKey(): String {
        return "rzp_test_TrZxr9JM9gdTIH"
    }

    fun startPayment(amount: Float) {
        val checkout = Checkout()
        checkout.setKeyID(razorpayApiKey())

        val options = JSONObject()
        options.put("name", "EasyShop")
        options.put("description", "")
        options.put("amount", (amount * 100).toInt())
        options.put("currency", "INR")

        checkout.open(GlobalNavigation.navController.context as Activity, options)
    }

    fun formatDate(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm:a", Locale.getDefault())
        return sdf.format(timestamp.toDate().time)
    }

    private const val PREF_NAME = "favourite_pref"
    private const val KEY_FAVOURITES = "favourite_list"

    fun addOrRemoveFromFavourite(context: Context, productId: String) {
        val list = getFavouriteList(context).toMutableSet()
        if (list.contains(productId)) {
            list.remove(productId)
            showToast(context, "Removed from favourites")
        } else {
            list.add(productId)
            showToast(context, "Added to favourites")
        }

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putStringSet(KEY_FAVOURITES, list)
        }
    }

    fun checkFavourite(context: Context, productId: String): Boolean {
        if(getFavouriteList(context).contains(productId)){
            return true
        }

        return false
    }

    fun getFavouriteList(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_FAVOURITES, emptySet()) ?: emptySet()
    }
}
