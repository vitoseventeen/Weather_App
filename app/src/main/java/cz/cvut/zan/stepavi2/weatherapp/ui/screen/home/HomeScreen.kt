package cz.cvut.zan.stepavi2.weatherapp.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens

@Composable
fun HomeScreen(
    onCityClick: (String) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val weatherState = viewModel.weather.collectAsState().value

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(paddingValues)
                .padding(Dimens.PaddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.current_weather),
                style = MaterialTheme.typography.titleLarge,
                fontSize = Dimens.TextSizeLarge
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
            Box(
                modifier = Modifier.size(Dimens.IconSizeLarge),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_sunny),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(Dimens.IconSizeMedium)
                )
            }
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
            Text(
                text = weatherState.city ?: stringResource(R.string.loading),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = Dimens.TextSizeMedium
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
            Text(
                text = stringResource(R.string.temperature, weatherState.temperature?.toString() ?: "--"),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = Dimens.TextSizeMedium
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
            Button(
                onClick = { viewModel.loadWeather("Prague") },
                modifier = Modifier.padding(top = Dimens.PaddingMedium)
            ) {
                Text(
                    text = stringResource(R.string.refresh_weather),
                    fontSize = Dimens.TextSizeMedium
                )
            }
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
            Button(
                onClick = { onCityClick(weatherState.city ?: "Prague") },
                modifier = Modifier.padding(top = Dimens.PaddingSmall)
            ) {
                Text(
                    text = stringResource(R.string.see_details),
                    fontSize = Dimens.TextSizeMedium
                )
            }
        }
    }
}