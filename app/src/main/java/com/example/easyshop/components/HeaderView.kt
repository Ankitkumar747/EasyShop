package com.example.easyshop.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun HeaderView(modifier: Modifier = Modifier) {

    var name by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val authName = currentUser.displayName?.takeIf { it.isNotBlank() }
            if (authName != null) {
                name = authName
            } else if (!currentUser.email.isNullOrBlank()) {
                name = currentUser.email!!.substringBefore("@").replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
            }

            Firebase.firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val firestoreName = doc.getString("name")
                            ?: doc.getString("username")
                            ?: doc.getString("fullName")
                            ?: doc.getString("full_name")
                            ?: doc.getString("displayName")
                        if (!firestoreName.isNullOrBlank()) {
                            name = firestoreName
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("HeaderView", "Failed to fetch user profile from Firestore", e)
                }
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Welcome back")
            Text(
                text = name.ifEmpty { "User" },
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = Bold
                )
            )
        }

        IconButton(onClick = { }) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        }
    }
}
