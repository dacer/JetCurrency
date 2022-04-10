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

        val BTC = Currency(
            code = "BTC",
            fullName = "Bitcoin",
            exchangeRateFromUsd = 0.000022,
        )

        val HKD = Currency(
            code = "HKD",
            fullName = "Hong Kong dollar",
            exchangeRateFromUsd = 7.833148,
        )

        val CNY = Currency(
            code = "CNY",
            fullName = "Chinese Yuan",
            exchangeRateFromUsd = 6.339821,
        )

        val DataMap: Map<String, Currency.Data> = mapOf(
            "JPY" to Currency.Data.Build(230.45008),
            "USD" to Currency.Data.Build(2.0)
        )
    }
}
