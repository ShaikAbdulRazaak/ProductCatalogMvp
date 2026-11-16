package com.razzaaq.productcatalogmvp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.razzaaq.productcatalogmvp.R
import com.razzaaq.productcatalogmvp.domain.model.Product
import com.razzaaq.productcatalogmvp.ui.theme.Dimensions

@Composable
fun ProductTile(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.zeroElevation),
        border = BorderStroke(Dimensions.productCardBorderWidth, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(Dimensions.productTileImageRatio)
                    .padding(Dimensions.cardPadding),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = stringResource(R.string.product_image, product.title),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.cardPadding)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Dimensions.smallSpacing))
                Text(
                    text = stringResource(R.string.product_price, product.price),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
