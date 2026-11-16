package com.razzaaq.productcatalogmvp.presentation.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun CategoryScreen(
    onProductClick: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = Dimensions.screenPadding)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Dimensions.screenPadding,
                    bottom = Dimensions.screenPadding
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                modifier = Modifier
                    .clickable { onNavigateBack() }
                    .padding(end = Dimensions.screenPadding)
            )
            Text(
                text = viewModel.categoryName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        when (val state = uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.gridSpacing),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.sectionSpacing),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = Dimensions.sectionSpacing)
                ) {
                    items(state.data) { product ->
                        ProductTile(
                            product = product,
                            onClick = { onProductClick(product.id) }
                        )
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