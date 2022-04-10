package im.dacer.jetcurrency.api.freecurrency

import com.google.gson.annotations.SerializedName
import im.dacer.jetcurrency.api.LiveResponse
import im.dacer.jetcurrency.model.CurrencyWithoutFullName

/**
 * The most recent exchange rate data from Free Currency Rates API.
 *
 * Example API Response:
 * {
 *   "date": "2022-04-09",
 *   "usd": {
 *     "1inch": 0.622661,
 *     "ada": 0.969325,
 *     [...]
 *   }
 * }
 *
 * Documentation:
 * https://github.com/fawazahmed0/currency-api
 */
data class FreeCurrencyUsdBasedLiveResponse(
    @field:SerializedName("date") val date: String,
    @field:SerializedName("usd") val quotes: Map<String, Double>,
) : LiveResponse {

    /**
     * Return currency list based on quotes.
     */
    override fun getCurrencyList(): List<CurrencyWithoutFullName> {
        return quotes.map {
            CurrencyWithoutFullName(it.key.uppercase(), it.value)
        }
    }
}
