package com.example.easyshop.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType

@Composable
fun BannerView(modifier: Modifier = Modifier) {

    var bannerList by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data").document("banners").get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    @Suppress("UNCHECKED_CAST") val urls =
                        (doc.get("urls") as? List<String>) ?: (doc.get("images") as? List<String>)
                        ?: (doc.get("banners") as? List<String>)
                    if (!urls.isNullOrEmpty()) {
                        bannerList = urls
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("BannerView", "Error loading banners from Firestore", exception)
            }
    }

    if (bannerList.isNotEmpty()) {
        Column(
            modifier = modifier
        ) {

            val pagerState = rememberPagerState(0) {
                bannerList.size
            }
            HorizontalPager(
                state = pagerState, pageSpacing = 24.dp
            ) {

                AsyncImage(
                    model = bannerList.get(it),
                    contentDescription = "banner image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            DotsIndicator(
                dotCount = bannerList.size, type = ShiftIndicatorType(
                    dotsGraphic = DotGraphic(
                        color = MaterialTheme.colorScheme.primary, size = 6.dp
                    )
                ), pagerState = pagerState
            )
        }
    }
}
