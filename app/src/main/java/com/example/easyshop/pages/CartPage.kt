package com.example.easyshop.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.components.CartItemView
import com.example.easyshop.model.ProductModel
import com.example.easyshop.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun CartPage(modifier: Modifier = Modifier) {

    var userModel = remember { mutableStateOf(UserModel()) }
    val productsMap = remember { mutableStateOf<Map<String, ProductModel>>(emptyMap()) }

    DisposableEffect(key1 = true) {
        val listener = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!).addSnapshotListener { it, _ ->
                val result = it?.toObject(UserModel::class.java)
                if (result != null) {
                    userModel.value = result

                    if (result.cartItems.isNotEmpty()) {
                        Firebase.firestore.collection("data").document("stock")
                            .collection("products")
                            .whereIn("id", result.cartItems.keys.take(10).toList())
                            .get().addOnSuccessListener { task ->
                                val map = task.documents.associate { doc ->
                                    val prod = doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
                                    doc.id to (prod ?: ProductModel())
                                }
                                productsMap.value = map
                            }
                    }
                }
            }
        onDispose {
            listener.remove()
        }
    }

    val subtotal = userModel.value.cartItems.entries.sumOf { (id, qty) ->
        val product = productsMap.value[id]
        val price = product?.actualPrice?.toFloatOrNull() ?: 0f
        (price * qty).toDouble()
    }.toFloat()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Cart", style = TextStyle(
                fontSize = 24.sp, fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (userModel.value.cartItems.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = userModel.value.cartItems.entries.sortedBy { it.key }.toList(),
                    key = { it.key }) { (productId, qty) ->
                    CartItemView(modifier = Modifier, productId = productId, qty = qty)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Amount", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(text = "₹${subtotal.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            GlobalNavigation.navController.navigate("checkout")
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Proceed to Checkout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your cart is empty", fontSize = 16.sp
                )
            }
        }
    }
}
