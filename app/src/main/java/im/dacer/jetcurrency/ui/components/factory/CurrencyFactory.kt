package im.dacer.jetcurrency.ui.components.factory

import im.dacer.jetcurrency.model.Currency

// For compose preview
class CurrencyFactory {

    companion object {
        val JPY = Currency(
            code = "JPY",
            fullName = "Japanese Yen",
            exchangeRateFromUsd = 115.22504,
        )

        val USD = Currency(
            code = "USD",
            fullName = "United States Dollar",
            exchangeRateFromUsd = 1.0,
        )

        val DataMap: Map<String, Currency.Data> = mapOf(
            "JPY" to Currency.Data(230.45008),
            "USD" to Currency.Data(2.0)
        )
    }
}
