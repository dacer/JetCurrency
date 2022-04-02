package im.dacer.jetcurrency.ui.components

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import im.dacer.jetcurrency.model.Currency
import im.dacer.jetcurrency.ui.components.factory.CurrencyFactory
import im.dacer.jetcurrency.ui.main.MainUiState
import im.dacer.jetcurrency.utils.WindowSize

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun CurrencySelectorScreen(
    uiState: MainUiState,
    currencyList: List<Currency>,
    modifier: Modifier = Modifier,
    isPortrait: Boolean = true,
    onBackClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onCurrencyClicked: (currencyCode: String) -> Unit,
) {
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }
    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .systemBarsPadding(top = false, bottom = false),
        topBar = {
            LargeTopAppBar(
                title = { Text("Choose currencies") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClicked) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.statusBarsPadding()
            )
        },
        content = { innerPadding ->
            when (uiState) {
                is MainUiState.HasData -> {
                    if (isPortrait) {
                        CurrencySelector(
                            currencyList = currencyList,
                            innerPadding = innerPadding,
                            onCurrencyClicked = onCurrencyClicked,
                        )
                    } else {
                        LandscapeCurrencySelector(
                            currencyList = currencyList,
                            innerPadding = innerPadding,
                            onCurrencyClicked = onCurrencyClicked,
                        )
                    }
                }
                is MainUiState.NoData -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "No Data",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    )
}

@ExperimentalFoundationApi
@Composable
private fun CurrencySelector(
    currencyList: List<Currency>,
    innerPadding: PaddingValues,
    onCurrencyClicked: (currencyCode: String) -> Unit,
) {
    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            currencyList.sortedBy { if (it.order != null) it.order else Int.MAX_VALUE },
            key = { it.code }
        ) { currency ->
            CurrencyRow(
                currency = currency,
                modifier = Modifier.animateItemPlacement(),
            ) { onCurrencyClicked.invoke(currency.code) }
        }
        item {
            Spacer(modifier = Modifier.systemBarsPadding(bottom = true))
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun LandscapeCurrencySelector(
    currencyList: List<Currency>,
    innerPadding: PaddingValues,
    onCurrencyClicked: (currencyCode: String) -> Unit,
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(3),
        contentPadding = innerPadding
    ) {
        items(
            currencyList.sortedBy { if (it.order != null) it.order else Int.MAX_VALUE },
            key = { it.code }
        ) { currency ->
            CurrencyRow(
                currency = currency,
                modifier = Modifier.animateItemPlacement(),
            ) { onCurrencyClicked.invoke(currency.code) }
        }
    }
}

@Composable
private fun CurrencyRow(currency: Currency, modifier: Modifier = Modifier, onClicked: () -> Unit) {
    val selected = currency.isShowing
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClicked.invoke() }
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
            .padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 16.dp)
    ) {
        val fontColor =
            if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground
        Column {
            Text(
                text = currency.code,
                style = MaterialTheme.typography.headlineLarge,
                color = fontColor,
                maxLines = 1,
            )
            Text(
                text = currency.fullName,
                style = MaterialTheme.typography.labelMedium,
                color = fontColor,
                maxLines = 1,
            )
        }
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCurrencyRow() {
    CurrencyRow(
        Currency(
            code = "JPY",
            fullName = "Japanese Yen",
            exchangeRateFromUsd = 115.22504,
        ),
        onClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSelectedCurrencyRow() {
    CurrencyRow(
        Currency(
            code = "JPY",
            fullName = "Japanese Yen",
            exchangeRateFromUsd = 115.22504,
            order = 1,
        ),
        onClicked = {}
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Preview("No data")
@Composable
private fun PreviewNoData(heightWindowSize: WindowSize = WindowSize.MEDIUM) {
    CurrencySelectorScreen(
        uiState = MainUiState.NoData(
            isLoading = false,
            errorMessage = null,
        ),
        currencyList = listOf(),
        onBackClicked = {},
        onSearchClicked = {},
        onCurrencyClicked = {},
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Preview("CurrencySelector")
@Composable
private fun PreviewCurrencySelector(
    isPortrait: Boolean = true,
    heightWindowSize: WindowSize = WindowSize.MEDIUM
) {
    CurrencySelectorScreen(
        uiState = MainUiState.HasData(
            isLoading = false,
            errorMessage = null,
            dataMap = mapOf(),
            focusedCurrencyCode = "USD",
        ),
        currencyList = listOf(
            CurrencyFactory.JPY.setOrder(0),
            CurrencyFactory.USD,
            CurrencyFactory.BTC,
            CurrencyFactory.HKD,
            CurrencyFactory.CNY,
        ),
        isPortrait = isPortrait,
        onBackClicked = {},
        onSearchClicked = {},
        onCurrencyClicked = {},
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Preview(
    "CurrencySelector (Landscape)",
    device = Devices.AUTOMOTIVE_1024p,
    widthDp = 1024,
    heightDp = 360
)
@Composable
private fun PreviewLandscapeCurrencySelector() {
    PreviewCurrencySelector(isPortrait = false)
}