package com.example.easyshop.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.easyshop.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun CartPage(modifier: Modifier = Modifier) {

    val userModel = remember{ mutableStateOf(UserModel()) }

    LaunchedEffect(key1 = true){
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Firebase.firestore.collection("users")
                .document(currentUser.uid)
                .get().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val doc = task.result
                        val result = doc?.toObject(UserModel::class.java)
                        val name = result?.name?.takeIf { it.isNotBlank() }
                            ?: currentUser.displayName
                            ?: currentUser.email?.substringBefore("@")
                            ?: ""
                        val email = result?.email?.takeIf { it.isNotBlank() }
                            ?: currentUser.email
                            ?: ""
                        val cartItems = result?.cartItems ?: emptyMap()

                        userModel.value = UserModel(
                            uid = currentUser.uid,
                            name = name,
                            email = email,
                            cartItems = cartItems
                        )
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

        Text(
            text = userModel.value.toString()
        )
    }
}
