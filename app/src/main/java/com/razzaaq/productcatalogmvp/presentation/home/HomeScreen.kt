package com.razzaaq.productcatalogmvp.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AssistChip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import com.razzaaq.productcatalogmvp.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.razzaaq.productcatalogmvp.presentation.UiState
import com.razzaaq.productcatalogmvp.presentation.components.ProductTile
import com.razzaaq.productcatalogmvp.ui.theme.Dimensions

@Composable
fun HomeScreen(
    onProductClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedSort by remember { mutableStateOf(R.string.sort_featured) }
    var topRatedOnly by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val categories = listOf(
        null to R.string.all_products,
        "electronics" to R.string.category_electronics,
        "jewelery" to R.string.category_jewellery,
        "men's clothing" to R.string.category_mens_clothing,
        "women's clothing" to R.string.category_womens_clothing
    )

    androidx.compose.material3.Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimensions.screenPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = Dimensions.screenPadding,
                        bottom = Dimensions.screenPadding
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.featured_products),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                androidx.compose.material3.IconButton(onClick = onSearchClick) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                }
            }

            Text(
                text = stringResource(R.string.new_season).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(Dimensions.smallSpacing))
            Text(
                text = stringResource(R.string.discover_your_style),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(Dimensions.smallSpacing))
            Text(
                text = stringResource(R.string.home_supporting_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Dimensions.sectionSpacing))
            Text(
                text = stringResource(R.string.shop_by_category),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Dimensions.compactPadding))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.categoryChipSpacing),
                contentPadding = PaddingValues(bottom = Dimensions.sectionSpacing)
            ) {
                items(categories.size) { index ->
                    val (categoryId, labelResId) = categories[index]
                    FilterChip(
                        selected = selectedCategory == categoryId,
                        onClick = {
                            viewModel.selectCategory(categoryId)
                        },
                        label = { Text(stringResource(labelResId)) }
                    )
                }
            }

            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Success -> {
                    val filteredProducts = if (topRatedOnly) {
                        state.data.filter { it.rating.rate >= 4.0 }
                    } else {
                        state.data
                    }.let { products ->
                        when (selectedSort) {
                            R.string.sort_price_low -> products.sortedBy { it.price }
                            R.string.sort_price_high -> products.sortedByDescending { it.price }
                            R.string.sort_rating -> products.sortedByDescending { it.rating.rate }
                            else -> products
                        }
                    }
                    val categoryTopRated = if (selectedCategory == null) {
                        emptyList()
                    } else {
                        filteredProducts
                            .sortedByDescending { it.rating.rate }
                            .take(2)
                    }
                    val categoryTopRatedIds = categoryTopRated.map { it.id }.toSet()
                    val categoryAdditional = if (selectedCategory == null) {
                        emptyList()
                    } else {
                        filteredProducts.filterNot { it.id in categoryTopRatedIds }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.products_count, filteredProducts.size),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.compactPadding)) {
                            AssistChip(
                                onClick = { topRatedOnly = !topRatedOnly },
                                label = {
                                    Text(
                                        stringResource(
                                            if (topRatedOnly) R.string.top_rated_only else R.string.filter
                                        )
                                    )
                                }
                            )
                            Box {
                                AssistChip(
                                    onClick = { sortMenuExpanded = true },
                                    label = { Text(stringResource(selectedSort)) }
                                )
                                DropdownMenu(
                                    expanded = sortMenuExpanded,
                                    onDismissRequest = { sortMenuExpanded = false }
                                ) {
                                    listOf(
                                        R.string.sort_featured,
                                        R.string.sort_price_low,
                                        R.string.sort_price_high,
                                        R.string.sort_rating
                                    ).forEach { sortRes ->
                                        DropdownMenuItem(
                                            text = { Text(stringResource(sortRes)) },
                                            onClick = {
                                                selectedSort = sortRes
                                                sortMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimensions.compactPadding))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.gridSpacing),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.sectionSpacing),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = Dimensions.sectionSpacing)
                    ) {
                        if (selectedCategory == null) {
                            items(filteredProducts, key = { it.id }) { product ->
                                ProductTile(
                                    product = product,
                                    onClick = { onProductClick(product.id) }
                                )
                            }
                        } else {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Text(
                                    text = stringResource(R.string.category_top_rated),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(vertical = Dimensions.compactPadding)
                                )
                            }
                            items(categoryTopRated, key = { it.id }) { product ->
                                ProductTile(
                                    product = product,
                                    onClick = { onProductClick(product.id) }
                                )
                            }
                            if (categoryAdditional.isNotEmpty()) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Text(
                                        text = stringResource(R.string.category_more_products),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(vertical = Dimensions.compactPadding)
                                    )
                                }
                                items(categoryAdditional, key = { it.id }) { product ->
                                    ProductTile(
                                        product = product,
                                        onClick = { onProductClick(product.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}