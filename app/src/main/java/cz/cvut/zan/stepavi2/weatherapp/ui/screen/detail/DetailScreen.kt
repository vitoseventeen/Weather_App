package cz.cvut.zan.stepavi2.weatherapp.ui.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.ui.viewmodel.DetailViewModel
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens

@Composable
fun DetailScreen(
    city: String,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.Factory(city))
) {
    val weatherState = viewModel.weather.collectAsState().value

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
                text = stringResource(R.string.weather_in, city),
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                fontSize = Dimens.TextSizeLarge
            )
            Text(
                text = stringResource(R.string.condition, weatherState.condition ?: stringResource(R.string.unknown)),
                modifier = Modifier.padding(top = Dimens.PaddingSmall),
                fontSize = Dimens.TextSizeMedium
            )
            Text(
                text = stringResource(R.string.temperature, weatherState.temperature?.toString() ?: "--"),
                modifier = Modifier.padding(top = Dimens.PaddingSmall),
                fontSize = Dimens.TextSizeMedium
            )
        }
    }
}