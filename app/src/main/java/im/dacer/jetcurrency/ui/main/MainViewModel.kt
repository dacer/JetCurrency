package im.dacer.jetcurrency.ui.main

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import im.dacer.jetcurrency.R
import im.dacer.jetcurrency.data.CurrencyRepository
import im.dacer.jetcurrency.data.Result
import im.dacer.jetcurrency.di.MainDispatcher
import im.dacer.jetcurrency.model.Currency
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: CurrencyRepository,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MainViewModelState(isLoading = true))

    private val _shownCurrencyList = MutableStateFlow(listOf<Currency>())
    val shownCurrencyList = _shownCurrencyList.asStateFlow()

    val currencyList = repository
        .getAllCurrencies()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            listOf()
        )

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch(mainDispatcher) {
            repository.getShowingCurrencies().collect {
                _shownCurrencyList.value = it
            }
        }
    }

    fun setFocusedCurrency(currencyCode: String) {
        viewModelState.update {
            it.copy(
                focusedCurrencyCode = currencyCode,
                dataMap = it.dataMap.mapValues { (code, data) ->
                    if (code == currencyCode) {
                        return@mapValues data.generateExpressionIfNeed()
                    }
                    data
                }.toMutableStateMap()
            )
        }
    }

    fun onErrorMessageDismissed() {
        viewModelState.update { it.copy(errorMessage = null) }
    }

    /**
     * char can be a number or a symbol like +, *, -, /
     */
    fun addCharToCurrentAmount(c: Char) {
        viewModelState.update { currentUiState ->
            val currencyCode = currentUiState.focusedCurrencyCode ?: return
            val currentData = currentUiState.dataMap[currencyCode] ?: return

            currentUiState.copy(
                dataMap = updateDataMap(
                    currentUiState.dataMap,
                    currencyCode,
                    currentData.addToExpression(c),
                    currencyList.value,
                )
            )
        }
    }

    // NOTE: It only support adjacent positions.
    fun onSelectedCurrencyListReordered(from: Int, to: Int) {
        if (abs(from - to) != 1) return
        val fromCurrency = shownCurrencyList.value[from]
        val toCurrency = shownCurrencyList.value[to]
        val fromOrder = fromCurrency.order
        fromCurrency.order = toCurrency.order
        toCurrency.order = fromOrder

        // We update _shownCurrencyList immediately to meet reorderable's requirement
        _shownCurrencyList.update { list ->
            val temp = list.toMutableList()
            temp[from] = fromCurrency
            temp[to] = toCurrency
            temp.sortBy { it.order }
            temp
        }
        viewModelScope.launch(mainDispatcher) {
            repository.updateCurrencies(
                fromCurrency, toCurrency
            )
        }
    }

    fun onClickBackspace() {
        viewModelState.update { currentUiState ->
            val currencyCode = currentUiState.focusedCurrencyCode ?: return
            val currentData = currentUiState.dataMap[currencyCode] ?: return
            currentUiState.copy(
                dataMap = updateDataMap(
                    currentUiState.dataMap,
                    currencyCode,
                    currentData.deleteLastStrInExpression(),
                    currencyList.value,
                )
            )
        }
    }

    fun onLongClickBackspace() {
        viewModelState.update { currentUiState ->
            val currencyCode = currentUiState.focusedCurrencyCode ?: return
            currentUiState.copy(
                dataMap = updateDataMap(
                    currentUiState.dataMap,
                    currencyCode,
                    Currency.Data.empty(),
                    currencyList.value,
                )
            )
        }
    }

    fun onCurrencyInSelectorClicked(currencyCode: String) = viewModelScope.launch(mainDispatcher) {
        val clickedCurrency = currencyList.value.find { it.code == currencyCode } ?: return@launch
        if (clickedCurrency.isShowing) {
            repository.updateCurrencies(clickedCurrency.setOrder(null))
        } else {
            val maxOrder = currencyList.value.maxByOrNull { it.order ?: -1 }?.order ?: 0
            repository.updateCurrencies(clickedCurrency.setOrder(maxOrder + 1))
        }
    }

    fun refreshCurrencyData() = viewModelScope.launch(mainDispatcher) {
        viewModelState.update { it.copy(isLoading = true) }
        var errorMessage: String? = null

        try {
            val result = repository.refreshData()
            when (result) {
                is Result.Error -> {
                    errorMessage = context.getString(
                        R.string.currencylayer_api_request_error_message,
                        result.exception.message
                    )
                }
                is Result.Success -> {
                    // initialize shownCurrencyList if need
                    if (shownCurrencyList.value.isEmpty()) {
                        repository.updateCurrencies(
                            currencyList.value.find { it.code == DEFAULT_CURRENCY_1 }?.setOrder(0),
                            currencyList.value.find { it.code == DEFAULT_CURRENCY_2 }?.setOrder(1),
                        )
                    }
                    // initialize DataMap if need
                    if (viewModelState.value.dataMap.isEmpty()) {
                        viewModelState.update {
                            it.copy(
                                dataMap = updateDataMap(
                                    emptyMap<String, Currency.Data>().toMutableStateMap(),
                                    DEFAULT_CURRENCY_1,
                                    Currency.Data.empty(),
                                    currencyList.value,
                                )
                            )
                        }
                    }
                    // set focused currency if need
                    if (viewModelState.value.focusedCurrencyCode == null) {
                        viewModelState.update {
                            val focusedCode = if (shownCurrencyList.value.isEmpty()) {
                                DEFAULT_CURRENCY_1
                            } else {
                                shownCurrencyList.value.first().code
                            }
                            it.copy(focusedCurrencyCode = focusedCode)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            viewModelState.update { it.copy(isLoading = false, errorMessage = errorMessage) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelState.update { it.copy(searchQuery = query) }
    }

    /**
     * Update focused currency with focused data, also update other currencies data with latest data
     */
    private fun updateDataMap(
        dataMap: SnapshotStateMap<String, Currency.Data>,
        focusedCurrencyCode: String,
        focusedData: Currency.Data,
        currencyList: List<Currency>,
    ): SnapshotStateMap<String, Currency.Data> {
        val focusedCurrency = currencyList.find { it.code == focusedCurrencyCode } ?: return dataMap

        return currencyList.map {
            return@map when {
                it.code == focusedCurrencyCode -> {
                    Pair(it.code, focusedData)
                }
                it.isShowing -> {
                    Pair(it.code, Currency.Data.Build(it, focusedCurrency, focusedData.value))
                }
                else -> {
                    Pair(it.code, Currency.Data.Build(it, focusedCurrency, focusedData.value))
                    // Pair(it.code, dataMap[it.code] ?: Currency.Data())
                }
            }
        }.toMutableStateMap()
    }

    companion object {
        const val DEFAULT_CURRENCY_1 = "USD"
        const val DEFAULT_CURRENCY_2 = "JPY"
    }
}

/**
 * UI state for the Main Screen.
 */
private data class MainViewModelState(
    val dataMap: SnapshotStateMap<String, Currency.Data> = mutableStateMapOf(),
    val focusedCurrencyCode: String? = null,
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val searchQuery: String = "",
) {
    fun toUiState(): MainUiState =
        if (dataMap.isEmpty()) {
            MainUiState.NoData(
                isLoading = isLoading,
                errorMessage = errorMessage,
                searchQuery = searchQuery,
            )
        } else {
            MainUiState.HasData(
                isLoading = isLoading,
                errorMessage = errorMessage,
                dataMap = dataMap,
                focusedCurrencyCode = focusedCurrencyCode,
                searchQuery = searchQuery,
            )
        }
}

private fun <K, V> Map<out K, V>.toMutableStateMap(): SnapshotStateMap<K, V> =
    toList().toMutableStateMap()
