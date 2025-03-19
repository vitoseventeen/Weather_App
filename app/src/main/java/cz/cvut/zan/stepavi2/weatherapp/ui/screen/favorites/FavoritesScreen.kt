package cz.cvut.zan.stepavi2.weatherapp.ui.screen.favorites

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.ui.viewmodel.FavoritesViewModel
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens

@Composable
fun FavoritesScreen(
    onCityClick: (String) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = viewModel()
) {
    val favorites = viewModel.favorites.collectAsState().value

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
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(Dimens.PaddingMedium),
                    fontSize = Dimens.TextSizeLarge
                )
            }
            items(favorites) { city ->
                TextButton(
                    onClick = { onCityClick(city) },
                    modifier = Modifier.padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall)
                ) {
                    Text(
                        text = city,
                        fontSize = Dimens.TextSizeMedium
                    )
                }
            }
        }
    }
}