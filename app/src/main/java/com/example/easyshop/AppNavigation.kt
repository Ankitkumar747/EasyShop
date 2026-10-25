package com.example.easyshop

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.easyshop.pages.CategoryProductsPage
import com.example.easyshop.pages.CheckoutPage
import com.example.easyshop.pages.OrdersPage
import com.example.easyshop.pages.ProductDetailsPage
import com.example.easyshop.pages.SearchPage
import com.example.easyshop.screen.AuthScreen
import com.example.easyshop.screen.HomeScreen
import com.example.easyshop.screen.LoginScreen
import com.example.easyshop.screen.SignupScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    GlobalNavigation.navController = navController

    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val startDestination = if (isUserLoggedIn) "home" else "auth"

    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {

        composable("auth") {
            AuthScreen(modifier, navController)
        }

        composable("login") {
            LoginScreen(modifier, navController)
        }

        composable("signup") {
            SignupScreen(modifier, navController)
        }

        composable("home") {
            HomeScreen(modifier, navController)
        }

        composable("category_products/{categoryId}") {
            val categoryId = it.arguments?.getString("categoryId")
            CategoryProductsPage(modifier, categoryId ?: "")
        }

        composable("products_details/{productId}") {
            val categoryId = it.arguments?.getString("productId")
            ProductDetailsPage(modifier, categoryId ?: "")
        }

        composable("checkout") {
            CheckoutPage(modifier)
        }

        composable("orders") {
            OrdersPage(modifier)
        }

        composable("search") {
            SearchPage(modifier)
        }

    }
}

object GlobalNavigation {
    lateinit var navController: NavHostController
}
