package com.example.easyshop.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.components.ProductItemView
import com.example.easyshop.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay

@Composable
fun SearchPage(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    val productsList = remember { mutableStateOf<List<ProductModel>>(emptyList()) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            productsList.value = emptyList()
            return
        }
        Firebase.firestore.collection("data").document("stock").collection("products")
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val allProducts = task.result.documents.mapNotNull { doc ->
                        doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
                    }
                    val filtered = allProducts.filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.description.contains(query, ignoreCase = true) ||
                                it.category.contains(query, ignoreCase = true)
                    }
                    productsList.value = filtered
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                GlobalNavigation.navController.popBackStack()
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    searchProducts(it)
                },
                placeholder = { Text("Search products...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .focusRequester(focusRequester),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(productsList.value.chunked(2)) { item ->
                Row {
                    item.forEach { product ->
                        ProductItemView(product = product, modifier = Modifier.weight(1f))
                    }
                    if (item.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
