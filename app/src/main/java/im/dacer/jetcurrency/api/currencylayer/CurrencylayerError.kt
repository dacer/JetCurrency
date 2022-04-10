package im.dacer.jetcurrency.api.currencylayer

import com.google.gson.annotations.SerializedName

data class CurrencylayerError(
    @field:SerializedName("code") val code: Int,
    @field:SerializedName("type") val type: String,
    @field:SerializedName("info") val info: String,
)
