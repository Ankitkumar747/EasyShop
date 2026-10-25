package com.example.easyshop.model

data class UserModel(
    val uid : String = "",
    val name : String = "",
    val email : String = "",
    val cartItems : Map<String, Int> = emptyMap(),
    val address : String = "",
)
