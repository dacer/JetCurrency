package im.dacer.jetcurrency.ui.main

import im.dacer.jetcurrency.model.Currency

sealed interface MainUiState {

    val isLoading: Boolean
    val errorMessage: String?

    data class NoData(
        override val isLoading: Boolean,
        override val errorMessage: String?,
    ) : MainUiState

    data class HasData(
        override val isLoading: Boolean,
        override val errorMessage: String?,
        val dataMap: Map<String, Currency.Data>,
        val focusedCurrencyCode: String?,
    ) : MainUiState
}
