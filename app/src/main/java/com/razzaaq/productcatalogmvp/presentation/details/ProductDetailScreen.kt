package com.razzaaq.productcatalogmvp.presentation.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.razzaaq.productcatalogmvp.R
import coil3.compose.AsyncImage
import com.razzaaq.productcatalogmvp.presentation.UiState
import com.razzaaq.productcatalogmvp.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    onNavigateBack: () -> Unit,
    onEnquireClick: (Int) -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.product_details)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (uiState is UiState.Success) {
                val product = (uiState as UiState.Success).data
                Button(
                    onClick = { onEnquireClick(product.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimensions.screenPadding)
                        .height(Dimensions.primaryButtonHeight),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.enquire_now))
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UiState.Success -> {
                    val product = state.data
                    var descriptionExpanded by remember { mutableStateOf(false) }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(
                                start = Dimensions.screenPadding,
                                top = Dimensions.screenPadding,
                                end = Dimensions.screenPadding,
                                bottom = Dimensions.largeSectionSpacing
                            )
                    ) {
                        AsyncImage(
                            model = product.image,
                            contentDescription = stringResource(R.string.product_image, product.title),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimensions.productImageHeight),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(Dimensions.sectionSpacing))
                        Text(
                            text = stringResource(R.string.brand_label).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(Dimensions.smallSpacing))
                        Text(
                            text = product.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Dimensions.compactPadding))
                        Text(
                            text = stringResource(R.string.product_price, product.price),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(Dimensions.sectionSpacing))
                        Text(
                            text = stringResource(R.string.product_description),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(Dimensions.compactPadding))
                        Text(
                            text = if (descriptionExpanded) {
                                product.description
                            } else {
                                product.description.take(160).let { text ->
                                    if (product.description.length > 160) "$text…" else text
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (product.description.length > 160) {
                            TextButton(onClick = { descriptionExpanded = !descriptionExpanded }) {
                                Text(
                                    stringResource(
                                        if (descriptionExpanded) R.string.show_less else R.string.read_more
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(Dimensions.sectionSpacing))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Text(
                            text = stringResource(R.string.delivery_returns),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = Dimensions.sectionSpacing)
                        )
                        Text(
                            text = stringResource(R.string.delivery_returns_text),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}