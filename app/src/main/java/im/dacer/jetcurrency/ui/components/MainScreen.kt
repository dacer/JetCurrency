package im.dacer.jetcurrency.ui.components

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import im.dacer.jetcurrency.model.Currency
import im.dacer.jetcurrency.ui.components.factory.CurrencyFactory
import im.dacer.jetcurrency.ui.main.MainUiState
import im.dacer.jetcurrency.ui.main.MainViewModel
import im.dacer.jetcurrency.ui.theme.CurrencyTheme

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val shownCurrencyList by viewModel.shownCurrencyList.collectAsState()
    val currencyList by viewModel.currencyList.collectAsState()
    var showNotImplementedAlert by remember { mutableStateOf(false) }
    var showCurrencySelector by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = it,
                withDismissAction = false,
                duration = SnackbarDuration.Long
            )
            if (snackbarResult == SnackbarResult.Dismissed) {
                viewModel.onErrorMessageDismissed()
            }
        }
    }

    BackHandler(showCurrencySelector) {
        showCurrencySelector = false
    }

    MainScreen(
        uiState = uiState,
        shownCurrencyList = shownCurrencyList,
        currencyList = currencyList,
        showNotImplementedAlert = showNotImplementedAlert,
        showCurrencySelector = showCurrencySelector,
        snackbarHostState = snackbarHostState,
        onClickCalculatorButton = viewModel::addCharToCurrentAmount,
        onClickBackspace = viewModel::onClickBackspace,
        onClickSettings = { showNotImplementedAlert = true },
        onClickRefresh = viewModel::refreshCurrencyData,
        onDismissNotImplementedAlert = { showNotImplementedAlert = false },
        onFocusCurrencyItem = viewModel::setFocusedCurrency,
        onClickFilterCurrency = { showCurrencySelector = true },
        onCurrencySelectorBack = { showCurrencySelector = false },
        onCurrencySelectorSearch = { showNotImplementedAlert = true },
        onCurrencySelectorClicked = viewModel::onCurrencyInSelectorClicked,
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
private fun MainScreen(
    uiState: MainUiState,
    shownCurrencyList: List<Currency>,
    currencyList: List<Currency>,
    showNotImplementedAlert: Boolean,
    showCurrencySelector: Boolean,
    snackbarHostState: SnackbarHostState,
    onClickCalculatorButton: (Char) -> Unit,
    onClickBackspace: () -> Unit,
    onClickSettings: () -> Unit,
    onClickRefresh: () -> Unit,
    onDismissNotImplementedAlert: () -> Unit,
    onFocusCurrencyItem: (currencyCode: String) -> Unit,
    onClickFilterCurrency: () -> Unit,
    onCurrencySelectorBack: () -> Unit,
    onCurrencySelectorSearch: () -> Unit,
    onCurrencySelectorClicked: (currencyCode: String) -> Unit,
) {
    CurrencyTheme {
        Box {
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .fillMaxHeight()
            ) {
                CurrencyList(
                    uiState,
                    shownCurrencyList = shownCurrencyList,
                    modifier = Modifier.weight(1f),
                    onClickCurrencyItem = onFocusCurrencyItem
                )
                Surface(
                    shadowElevation = 8.dp,
                    tonalElevation = 8.dp,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                    )
                ) {
                    CalculatorLayout(
                        uiState.isLoading,
                        onClickCalculatorButton = onClickCalculatorButton,
                        onClickBackspace = onClickBackspace,
                        onClickSettings = onClickSettings,
                        onClickRefresh = onClickRefresh,
                        onClickFilterCurrency = onClickFilterCurrency,
                    )
                }
            }
            AnimatedVisibility(
                visible = showCurrencySelector,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                ),
                exit = slideOutVertically() + shrinkVertically() + fadeOut(),
            ) {
                CurrencySelectorScreen(
                    uiState = uiState,
                    currencyList = currencyList,
                    modifier = Modifier.fillMaxHeight(),
                    onBackClicked = onCurrencySelectorBack,
                    onSearchClicked = onCurrencySelectorSearch,
                    onCurrencyClicked = onCurrencySelectorClicked,
                )
            }
            if (showNotImplementedAlert) {
                NotImplementedAlert(onDismiss = onDismissNotImplementedAlert)
            }
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Preview("Main screen")
@Preview(
    "Main screen (dark) (Nexus 7)",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.NEXUS_7_2013
)
@Preview("Main screen (big font)", fontScale = 1.5f, device = Devices.NEXUS_7_2013)
@Composable
private fun PreviewRoot() {
    MainScreen(
        uiState = MainUiState.HasData(
            isLoading = false,
            errorMessage = null,
            dataMap = CurrencyFactory.DataMap,
            focusedCurrencyCode = "USD",
        ),
        shownCurrencyList = listOf(
            CurrencyFactory.USD,
            CurrencyFactory.JPY,
        ),
        currencyList = listOf(),
        showNotImplementedAlert = false,
        showCurrencySelector = false,
        snackbarHostState = SnackbarHostState(),
        onClickCalculatorButton = {},
        onClickBackspace = {},
        onClickSettings = {},
        onClickRefresh = {},
        onDismissNotImplementedAlert = {},
        onFocusCurrencyItem = {},
        onClickFilterCurrency = {},
        onCurrencySelectorBack = {},
        onCurrencySelectorSearch = {},
        onCurrencySelectorClicked = {},
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Preview("Currency selector screen")
@Composable
private fun PreviewCurrencySelector() {
    MainScreen(
        uiState = MainUiState.HasData(
            isLoading = false,
            errorMessage = null,
            dataMap = mapOf(),
            focusedCurrencyCode = "USD",
        ),
        shownCurrencyList = listOf(
            CurrencyFactory.USD,
            CurrencyFactory.JPY,
        ),
        currencyList = listOf(),
        showNotImplementedAlert = false,
        showCurrencySelector = true,
        snackbarHostState = SnackbarHostState(),
        onClickCalculatorButton = {},
        onClickBackspace = {},
        onClickSettings = {},
        onClickRefresh = {},
        onDismissNotImplementedAlert = {},
        onFocusCurrencyItem = {},
        onClickFilterCurrency = {},
        onCurrencySelectorBack = {},
        onCurrencySelectorSearch = {},
        onCurrencySelectorClicked = {},
    )
}
