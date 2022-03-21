package im.dacer.jetcurrency.model

import androidx.room.ColumnInfo

data class CurrencyWithoutFullName(
    val code: String,
    @ColumnInfo(name = "exchange_rate_from_usd") val exchangeRateFromUsd: Double,
)
