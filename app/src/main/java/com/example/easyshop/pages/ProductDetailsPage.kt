package com.example.easyshop.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.easyshop.components.ProductDetailsView

@Composable
fun ProductDetailsPage(modifier: Modifier = Modifier, productId: String) {
    ProductDetailsView(modifier = modifier, productId = productId)
}
