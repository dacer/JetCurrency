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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import im.dacer.jetcurrency.model.Currency
import im.dacer.jetcurrency.ui.components.factory.CurrencyFactory
import im.dacer.jetcurrency.ui.main.MainUiState
import im.dacer.jetcurrency.ui.main.MainViewModel
import im.dacer.jetcurrency.utils.WindowSize
import im.dacer.jetcurrency.utils.WindowState

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    windowState: WindowState,
) {
    val uiState by viewModel.uiState.collectAsState()
    val shownCurrencyList by viewModel.shownCurrencyList.collectAsState()
    val currencyList by viewModel.currencyList.collectAsState()
    var showNotImplementedAlert by remember { mutableStateOf(false) }
    var showCurrencySelector by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val overlappingLength by remember { mutableStateOf(16.dp) }

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
        snackbarHostState = snackbarHostState,
        windowState = windowState,
        shownCurrencyList = shownCurrencyList,
        currencyList = currencyList,
        showNotImplementedAlert = showNotImplementedAlert,
        showCurrencySelector = showCurrencySelector,
        overlappingLength = overlappingLength,
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
    snackbarHostState: SnackbarHostState,
    windowState: WindowState,
    shownCurrencyList: List<Currency>,
    currencyList: List<Currency>,
    showNotImplementedAlert: Boolean,
    showCurrencySelector: Boolean,
    overlappingLength: Dp = 0.dp,
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
    Box {
        if (windowState.isLandscape && windowState.widthWindowSize > WindowSize.COMPACT) {
            LandscapeMainLayout(
                uiState,
                windowState,
                shownCurrencyList,
                overlappingLength,
                onFocusCurrencyItem,
                onClickCalculatorButton,
                onClickBackspace,
                onClickSettings,
                onClickRefresh,
                onClickFilterCurrency
            )
        } else {
            PortraitMainLayout(
                uiState,
                shownCurrencyList,
                overlappingLength,
                onFocusCurrencyItem,
                onClickCalculatorButton,
                onClickBackspace,
                onClickSettings,
                onClickRefresh,
                onClickFilterCurrency
            )
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
                isGridMode = windowState.widthWindowSize > WindowSize.COMPACT,
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

@Composable
private fun PortraitMainLayout(
    uiState: MainUiState,
    shownCurrencyList: List<Currency>,
    overlappingLength: Dp,
    onFocusCurrencyItem: (currencyCode: String) -> Unit,
    onClickCalculatorButton: (Char) -> Unit,
    onClickBackspace: () -> Unit,
    onClickSettings: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickFilterCurrency: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(-overlappingLength)
    ) {
        CurrencyList(
            uiState,
            modifier = Modifier.weight(1f),
            footerSpacerHeight = overlappingLength,
            shownCurrencyList = shownCurrencyList,
            onClickCurrencyItem = onFocusCurrencyItem
        )
        Surface(
            shadowElevation = 8.dp,
            tonalElevation = 8.dp,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
            ),
            modifier = Modifier.weight(1f),
        ) {
            CalculatorLayout(
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .navigationBarsPadding(start = false),
                onClickCalculatorButton = onClickCalculatorButton,
                onClickBackspace = onClickBackspace,
                onClickSettings = onClickSettings,
                onClickRefresh = onClickRefresh,
                onClickFilterCurrency = onClickFilterCurrency,
            )
        }
    }
}

@Composable
private fun LandscapeMainLayout(
    uiState: MainUiState,
    windowState: WindowState,
    shownCurrencyList: List<Currency>,
    overlappingLength: Dp,
    onFocusCurrencyItem: (currencyCode: String) -> Unit,
    onClickCalculatorButton: (Char) -> Unit,
    onClickBackspace: () -> Unit,
    onClickSettings: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickFilterCurrency: () -> Unit
) {
    Row(
        Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.spacedBy(-overlappingLength)
    ) {
        CurrencyList(
            uiState,
            modifier = Modifier.weight(1f),
            shownCurrencyList = shownCurrencyList,
            extraEndPadding = overlappingLength,
            onClickCurrencyItem = onFocusCurrencyItem
        )
        Surface(
            shadowElevation = 8.dp,
            tonalElevation = 8.dp,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                bottomStart = 16.dp,
            ),
            modifier = Modifier.weight(
                if (windowState.widthWindowSize == WindowSize.EXPANDED) 0.7f else 1f
            ),
        ) {
            CalculatorLayout(
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .statusBarsPadding()
                    .navigationBarsPadding(start = false),
                onClickCalculatorButton = onClickCalculatorButton,
                onClickBackspace = onClickBackspace,
                onClickSettings = onClickSettings,
                onClickRefresh = onClickRefresh,
                onClickFilterCurrency = onClickFilterCurrency,
            )
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Preview(
    "Main screen (Landscape)",
    device = Devices.AUTOMOTIVE_1024p,
    widthDp = 1024,
    heightDp = 360
)
@Composable
private fun PreviewLandscapeRoot() {
    PreviewRoot(isLandscape = true)
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Preview("Main screen")
@Preview(
    "Main screen (dark) (Nexus 7)",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.NEXUS_7_2013
)
@Preview("Home screen (Tablet)", device = Devices.PIXEL_C)
@Preview("Main screen (big font)", fontScale = 1.5f, device = Devices.NEXUS_7_2013)
@Composable
private fun PreviewRoot(
    widthWindowSize: WindowSize = WindowSize.MEDIUM,
    heightWindowSize: WindowSize = WindowSize.MEDIUM,
    isLandscape: Boolean = false,
) {
    MainScreen(
        uiState = MainUiState.HasData(
            isLoading = false,
            errorMessage = null,
            dataMap = CurrencyFactory.DataMap,
            focusedCurrencyCode = "USD",
        ),
        windowState = WindowState(widthWindowSize, heightWindowSize, isLandscape),
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
