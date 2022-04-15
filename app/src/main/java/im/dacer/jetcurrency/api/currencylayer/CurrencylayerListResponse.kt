package im.dacer.jetcurrency.api.currencylayer

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A full list of supported currencies from currencylayer.
 *
 * Example API Response:
 * {
 *     [...],
 *     "currencies": {
 *         "AED": "United Arab Emirates Dirham",
 *         "AFN": "Afghan Afghani",
 *         "ALL": "Albanian Lek",
 *         "AMD": "Armenian Dram",
 *         "ANG": "Netherlands Antillean Guilder",
 *         [...]
 *     }
 * }
 *
 * Documentation:
 * https://currencylayer.com/documentation#supported_currencies
 */
// TODO make a BaseResponse that include success and error
@JsonClass(generateAdapter = true)
data class CurrencylayerListResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "currencies") val currencies: Map<String, String>,
    @Json(name = "error") val error: CurrencylayerError?,
)
