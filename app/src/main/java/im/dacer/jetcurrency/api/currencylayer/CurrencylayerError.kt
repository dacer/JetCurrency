package im.dacer.jetcurrency.api.currencylayer

import com.squareup.moshi.Json

data class CurrencylayerError(
    @Json(name = "code") val code: Int,
    @Json(name = "type") val type: String,
    @Json(name = "info") val info: String,
)
