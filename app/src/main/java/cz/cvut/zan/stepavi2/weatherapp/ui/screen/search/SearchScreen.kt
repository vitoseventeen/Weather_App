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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens

@Composable
fun SearchScreen(
    onCitySelected: (String) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

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
                onValueChange = { searchQuery = it },
                label = { Text(stringResource(R.string.enter_city_name)) },
                modifier = Modifier.padding(top = Dimens.PaddingMedium)
            )
            Button(
                onClick = { if (searchQuery.isNotEmpty()) onCitySelected(searchQuery) },
                modifier = Modifier.padding(top = Dimens.PaddingMedium)
            ) {
                Text(
                    text = stringResource(R.string.search),
                    fontSize = Dimens.TextSizeMedium
                )
            }
        }
    }
}