package im.dacer.jetcurrency.api.currencylayer

import com.google.gson.annotations.SerializedName

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
data class CurrencylayerListResponse(
    @field:SerializedName("success") val success: Boolean,
    @field:SerializedName("currencies") val currencies: HashMap<String, String>,
    @field:SerializedName("error") val error: CurrencylayerError?,
)
