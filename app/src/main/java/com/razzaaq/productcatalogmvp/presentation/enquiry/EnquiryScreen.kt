package com.razzaaq.productcatalogmvp.presentation.enquiry

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.razzaaq.productcatalogmvp.R
import coil3.compose.AsyncImage
import com.razzaaq.productcatalogmvp.presentation.UiState
import com.razzaaq.productcatalogmvp.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnquiryScreen(
    onNavigateBack: () -> Unit,
    viewModel: EnquiryViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()
    val productState by viewModel.productState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadProduct()
    }

    LaunchedEffect(submitState) {
        if (submitState is SubmitState.Success) {
            Toast.makeText(context, context.getString(R.string.enquiry_submitted), Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onNavigateBack()
        } else if (submitState is SubmitState.Error) {
            Toast.makeText(context, (submitState as SubmitState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.product_enquiry)) },
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
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimensions.screenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimensions.sectionSpacing)
        ) {
            Text(
                text = stringResource(R.string.enquiry_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (submitState is SubmitState.Error) {
                Text(
                    text = (submitState as SubmitState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (productState is UiState.Success) {
                val product = (productState as UiState.Success).data
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.sectionSpacing)
                ) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = stringResource(R.string.product_image, product.title),
                        modifier = Modifier.size(Dimensions.enquiryThumbnailSize)
                    )
                    Column {
                        Text(product.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(R.string.product_price, product.price),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = submitState is SubmitState.Error && name.isBlank()
            )
            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = submitState is SubmitState.Error &&
                    !EMAIL_PATTERN.matches(email)
            )
            OutlinedTextField(
                value = message,
                onValueChange = viewModel::onMessageChange,
                label = { Text(stringResource(R.string.message)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.enquiryMessageHeight),
                minLines = 4,
                isError = submitState is SubmitState.Error && message.isBlank()
            )

            Button(
                onClick = viewModel::submitEnquiry,
                modifier = Modifier.fillMaxWidth(),
                enabled = submitState !is SubmitState.Loading
            ) {
                if (submitState is SubmitState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimensions.progressIndicatorSize),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.submit_enquiry))
                }
            }
        }
    }
}