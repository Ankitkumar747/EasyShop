package com.example.easyshop.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.components.CartItemView
import com.example.easyshop.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun CartPage(modifier: Modifier = Modifier) {

    val userModel = remember{ mutableStateOf(UserModel()) }

    LaunchedEffect(key1 = true){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid ?: ""

        if (uid.isNotBlank()) {
            Firebase.firestore.collection("users")
                .document(uid)
                .get().addOnCompleteListener {
                    if(it.isSuccessful){
                        val result = it.result?.toObject(UserModel::class.java)
                        if(result != null){
                            userModel.value = result.copy(
                                uid = uid,
                                name = result.name.ifEmpty { currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "" },
                                email = result.email.ifEmpty { currentUser?.email ?: "" }
                            )
                        } else {
                            userModel.value = UserModel(
                                uid = uid,
                                name = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "",
                                email = currentUser?.email ?: ""
                            )
                        }
                    }
                }
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Cart",
            style = TextStyle(fontSize = 24.sp,
            fontWeight = FontWeight.Bold)
        )

        LazyColumn {
            items(userModel.value.cartItems.toList()) { (productId, qty) ->
                CartItemView(modifier = Modifier, productId = productId, qty = qty)
            }
        }
    }
}
