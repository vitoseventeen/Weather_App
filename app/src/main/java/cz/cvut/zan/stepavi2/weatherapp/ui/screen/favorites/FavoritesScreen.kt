package cz.cvut.zan.stepavi2.weatherapp.ui.screen.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.data.database.AppDatabase
import cz.cvut.zan.stepavi2.weatherapp.data.repository.CityRepository
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared.SharedViewModel
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens
import cz.cvut.zan.stepavi2.weatherapp.util.ValidationUtil

@Composable
fun FavoritesScreen(
    onCityClick: (String) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cityDao = AppDatabase.getDatabase(context).cityDao()
    val cityRepository = CityRepository(cityDao)
    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(cityRepository)
    )
    val sharedViewModel: SharedViewModel = viewModel()
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())
    val cityInput by sharedViewModel.favoritesCityInput.collectAsState()
    var validationError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(paddingValues)
        ) {
            item {
                Text(
                    text = stringResource(R.string.favorites),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(Dimens.PaddingMedium),
                    fontSize = Dimens.TextSizeLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                OutlinedTextField(
                    value = cityInput,
                    onValueChange = { newValue ->
                        sharedViewModel.updateFavoritesCityInput(newValue)
                        validationError = ValidationUtil.getCityValidationError(newValue)
                    },
                    label = { Text(stringResource(R.string.enter_city_name)) },
                    modifier = Modifier
                        .padding(horizontal = Dimens.PaddingMedium)
                        .padding(bottom = Dimens.PaddingSmall),
                    isError = validationError != null
                )
                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(horizontal = Dimens.PaddingMedium)
                            .padding(bottom = Dimens.PaddingSmall),
                        fontSize = Dimens.TextSizeSmall
                    )
                }
                Button(
                    onClick = {
                        validationError = ValidationUtil.getCityValidationError(cityInput)
                        if (validationError == null) {
                            viewModel.addCity(cityInput)
                            sharedViewModel.clearFavoritesCityInput()
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = Dimens.PaddingMedium)
                        .padding(bottom = Dimens.PaddingMedium),
                    enabled = ValidationUtil.isValidCityName(cityInput)
                ) {
                    Text(
                        text = stringResource(R.string.add_city),
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            items(favorites) { city ->
                TextButton(
                    onClick = { onCityClick(city) },
                    modifier = Modifier.padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall)
                ) {
                    Text(
                        text = city,
                        fontSize = Dimens.TextSizeMedium,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Button(
                        onClick = { viewModel.removeCity(city) },
                        modifier = Modifier.padding(start = Dimens.PaddingSmall)
                    ) {
                        Text(
                            text = "Remove",
                            fontSize = Dimens.TextSizeMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}