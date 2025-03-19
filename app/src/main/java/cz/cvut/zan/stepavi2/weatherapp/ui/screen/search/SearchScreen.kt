package cz.cvut.zan.stepavi2.weatherapp.ui.screen.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared.SharedViewModel
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens
import cz.cvut.zan.stepavi2.weatherapp.util.ValidationUtil

@Composable
fun SearchScreen(
    onCitySelected: (String) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val sharedViewModel: SharedViewModel = viewModel()
    val searchQuery by sharedViewModel.searchQuery.collectAsState()
    var validationError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(paddingValues)
                .padding(Dimens.PaddingMedium)
        ) {
            Text(
                text = stringResource(R.string.search_city),
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                fontSize = Dimens.TextSizeLarge
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    sharedViewModel.updateSearchQuery(it)
                    validationError = ValidationUtil.getCityValidationError(it)
                },
                label = { Text(stringResource(R.string.enter_city_name)) },
                modifier = Modifier.padding(top = Dimens.PaddingMedium),
                isError = validationError != null
            )
            if (validationError != null) {
                Text(
                    text = validationError!!,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = Dimens.PaddingSmall),
                    fontSize = Dimens.TextSizeSmall
                )
            }
            Button(
                onClick = {
                    validationError = ValidationUtil.getCityValidationError(searchQuery)
                    if (validationError == null) {
                        onCitySelected(searchQuery)
                    }
                },
                modifier = Modifier.padding(top = Dimens.PaddingMedium),
                enabled = ValidationUtil.isValidCityName(searchQuery)
            ) {
                Text(
                    text = stringResource(R.string.search),
                    fontSize = Dimens.TextSizeMedium
                )
            }
        }
    }
}