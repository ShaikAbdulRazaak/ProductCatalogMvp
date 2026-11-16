package com.razzaaq.productcatalogmvp.presentation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    
    // Routes with arguments
    data object Category : Screen("category/{categoryId}") {
        fun createRoute(categoryId: String) = "category/$categoryId"
    }
    data object ProductDetails : Screen("product_details/{productId}") {
        fun createRoute(productId: Int) = "product_details/$productId"
    }
    data object Enquiry : Screen("enquiry/{productId}") {
        fun createRoute(productId: Int) = "enquiry/$productId"
    }
}