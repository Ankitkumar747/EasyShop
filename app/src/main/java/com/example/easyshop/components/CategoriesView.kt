package com.example.easyshop.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.model.categoryModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CategoriesView(modifier: Modifier = Modifier) {
    val categoryList = remember {
        mutableStateOf<List<categoryModel>>(emptyList())
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("data").document("stock")
            .collection("categories")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val resultList = querySnapshot.documents.mapNotNull { doc ->
                    val category = doc.toObject(categoryModel::class.java)
                    val imageUrl = category?.imageUrl?.ifEmpty { null }
                        ?: doc.getString("image")
                        ?: doc.getString("image_url")
                        ?: doc.getString("url")
                        ?: doc.getString("icon")
                        ?: doc.getString("img")
                        ?: ""
                    val name = category?.name?.ifEmpty { null }
                        ?: doc.getString("name")
                        ?: doc.id
                    categoryModel(
                        id = doc.id,
                        name = name,
                        imageUrl = imageUrl
                    )
                }
                categoryList.value = resultList
            }
            .addOnFailureListener { exception ->
                Log.e("CategoriesView", "Error loading categories from Firestore", exception)
            }
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categoryList.value) { item ->
            CategoryItem(category = item)
        }
    }
}

@Composable
fun CategoryItem(category: categoryModel) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .clickable {
                try {
                    GlobalNavigation.navController.navigate("category_products/" + category.id)
                } catch (e: Exception) {
                    Log.e("CategoryItem", "Navigation error", e)
                }
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            val rawUrl = category.imageUrl.trim()
            val imageUrl = if (rawUrl.startsWith("http://")) "https://" + rawUrl.substring(7) else rawUrl

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = category.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(50.dp),
                onError = { error ->
                    Log.e("CategoryItem", "Failed to load category image: '$imageUrl'", error.result.throwable)
                },
                onSuccess = {
                    Log.d("CategoryItem", "Successfully loaded category image: '$imageUrl'")
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
