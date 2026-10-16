package com.example.easyshop.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "Please fill in all fields")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    fun signup(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            onResult(false, "Please fill in all fields")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userMap = hashMapOf(
                            "name" to name,
                            "email" to email
                        )
                        Firebase.firestore.collection("users").document(userId).set(userMap)
                            .addOnCompleteListener { firestoreTask ->
                                if (firestoreTask.isSuccessful) {
                                    onResult(true, null)
                                } else {
                                    onResult(false, firestoreTask.exception?.localizedMessage ?: "Failed to save user details")
                                }
                            }
                    } else {
                        onResult(true, null)
                    }
                } else {
                    onResult(false, task.exception?.localizedMessage ?: "Signup failed")
                }
            }
    }

}
