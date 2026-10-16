package com.example.easyshop.pages

import com.example.easyshop.components.CategoriesView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.components.BannerView
import com.example.easyshop.components.HeaderView

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        HeaderView(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(20.dp))
        BannerView(modifier = Modifier.height(180.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Categories", style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        CategoriesView(modifier = Modifier.fillMaxWidth())
    }
}