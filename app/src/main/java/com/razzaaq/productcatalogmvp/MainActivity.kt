package com.razzaaq.productcatalogmvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.razzaaq.productcatalogmvp.presentation.Screen
import com.razzaaq.productcatalogmvp.presentation.details.ProductDetailScreen
import com.razzaaq.productcatalogmvp.presentation.enquiry.EnquiryScreen
import com.razzaaq.productcatalogmvp.presentation.home.HomeScreen
import com.razzaaq.productcatalogmvp.presentation.search.SearchScreen
import com.razzaaq.productcatalogmvp.ui.theme.ProductCatalogMvpTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductCatalogMvpTheme {
                StoreNavGraph()
            }
        }
    }
}

@Composable
fun StoreNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ProductDetails.route) {
            ProductDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onEnquireClick = { productId ->
                    navController.navigate(Screen.Enquiry.createRoute(productId))
                }
            )
        }
        composable(Screen.Enquiry.route) {
            EnquiryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
