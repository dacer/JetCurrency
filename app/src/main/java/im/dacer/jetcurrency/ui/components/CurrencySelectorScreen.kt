package im.dacer.jetcurrency.ui.components

import android.view.KeyEvent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import im.dacer.jetcurrency.model.Currency
import im.dacer.jetcurrency.ui.components.factory.CurrencyFactory
import im.dacer.jetcurrency.ui.main.MainUiState
import im.dacer.jetcurrency.ui.theme.drawableResource
import im.dacer.jetcurrency.utils.WindowSize
import im.dacer.jetcurrency.utils.toWidthWindowSize

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun CurrencySelectorScreen(
    uiState: MainUiState,
    currencyList: List<Currency>,
    modifier: Modifier = Modifier,
    isGridMode: Boolean = false,
    onBackClicked: () -> Unit,
    onSearchQueryChanged: (query: String) -> Unit,
    onCurrencyClicked: (currencyCode: String) -> Unit,
) = BoxWithConstraints(modifier = modifier) {
    val widthWindowSize = maxWidth.toWidthWindowSize()

    Scaffold(
        modifier = Modifier
            .statusBarsPadding(),
        topBar = {
            CustomTopBar(
                uiState.searchQuery,
                onSearchQueryChanged,
                onBackClicked,
            )
        },
        content = { innerPadding ->
            when (uiState) {
                is MainUiState.HasData -> {
                    val filteredCurrencyList = currencyList.filter {
                        it.fullName.uppercase()
                            .contains(uiState.searchQuery.uppercase()) || it.code.uppercase()
                            .contains(
                                uiState.searchQuery.uppercase()
                            )
                    }
                    if (isGridMode) {
                        LandscapeCurrencySelector(
                            currencyList = filteredCurrencyList,
                            innerPadding = innerPadding,
                            onCurrencyClicked = onCurrencyClicked,
                        )
                    } else {
                        CurrencySelector(
                            currencyList = filteredCurrencyList,
                            innerPadding = innerPadding,
                            widthWindowSize = widthWindowSize,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTopBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onBackClicked: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = text,
        onValueChange = {
            onTextChanged(it)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                contentDescription = "Search Icon",
                modifier = Modifier.clickable { onBackClicked() }
            )
        },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = {
                    onTextChanged("")
                    focusManager.clearFocus()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        contentDescription = "Clear Icon"
                    )
                }
            }
        },
        textStyle = MaterialTheme.typography.headlineSmall,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        placeholder = {
            Text(
                "Search currencies",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background, shape = RectangleShape)
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                    focusManager.clearFocus()
                    true
                } else {
                    false
                }
            }
    )
}

@ExperimentalFoundationApi
@Composable
private fun CurrencySelector(
    currencyList: List<Currency>,
    innerPadding: PaddingValues,
    widthWindowSize: WindowSize,
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
                widthWindowSize = widthWindowSize,
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
    val list = currencyList.sortedBy { if (it.order != null) it.order else Int.MAX_VALUE }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = innerPadding
    ) {
        items(list.size) { index ->
            CurrencyRow(
                currency = list[index],
                modifier = Modifier.animateItemPlacement(),
            ) { onCurrencyClicked.invoke(list[index].code) }
        }
    }
}

@Composable
private fun CurrencyRow(
    modifier: Modifier = Modifier,
    currency: Currency,
    widthWindowSize: WindowSize = WindowSize.EXPANDED,
    onClicked: () -> Unit
) {
    val selected = currency.isShowing

    val mainFontSize = when (widthWindowSize) {
        WindowSize.TINY -> 14.sp
        WindowSize.COMPACT -> 22.sp
        WindowSize.MEDIUM -> 28.sp
        WindowSize.EXPANDED -> 32.sp
    }
    val imageSize = when (widthWindowSize) {
        WindowSize.TINY -> 0.dp
        WindowSize.COMPACT -> 22.dp
        WindowSize.MEDIUM -> 24.dp
        WindowSize.EXPANDED -> 26.dp
    }
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = drawableResource(name = currency.flagDrawableName)),
                contentDescription = "flag",
                modifier = Modifier
                    .size(imageSize)
                    .clip(RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = currency.code,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = mainFontSize),
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
        currency = Currency(
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
        currency = Currency(
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
            searchQuery = "",
        ),
        currencyList = listOf(),
        onBackClicked = {},
        onSearchQueryChanged = {},
        onCurrencyClicked = {},
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Preview("CurrencySelector")
@Composable
private fun PreviewCurrencySelector(
    isGridMode: Boolean = false,
    heightWindowSize: WindowSize = WindowSize.MEDIUM
) {
    CurrencySelectorScreen(
        uiState = MainUiState.HasData(
            isLoading = false,
            errorMessage = null,
            dataMap = mapOf(),
            focusedCurrencyCode = "USD",
            searchQuery = "",
        ),
        currencyList = listOf(
            CurrencyFactory.JPY.setOrder(0),
            CurrencyFactory.USD,
            CurrencyFactory.BTC,
            CurrencyFactory.HKD,
            CurrencyFactory.CNY,
        ),
        isGridMode = isGridMode,
        onBackClicked = {},
        onSearchQueryChanged = {},
        onCurrencyClicked = {},
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Preview("CurrencySelector")
@Composable
private fun PreviewFilteredCurrencySelector(
    isGridMode: Boolean = false,
    heightWindowSize: WindowSize = WindowSize.MEDIUM
) {
    CurrencySelectorScreen(
        uiState = MainUiState.HasData(
            isLoading = false,
            errorMessage = null,
            dataMap = mapOf(),
            focusedCurrencyCode = "USD",
            searchQuery = "Y",
        ),
        currencyList = listOf(
            CurrencyFactory.JPY.setOrder(0),
            CurrencyFactory.CNY,
        ),
        isGridMode = isGridMode,
        onBackClicked = {},
        onSearchQueryChanged = {},
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
    PreviewCurrencySelector(isGridMode = true)
}
