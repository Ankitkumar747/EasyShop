package com.example.easyshop.model

data class ProductModel(
    val id: String = "",
    val title: String = "",
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val actualPrice: String = "",
    val images: List<String> = emptyList(),
    val imageUrl: String = "",
    val category: String = ""
)
