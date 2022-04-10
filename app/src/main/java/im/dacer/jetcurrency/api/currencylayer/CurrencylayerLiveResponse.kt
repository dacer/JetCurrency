package im.dacer.jetcurrency.api.currencylayer

import com.google.gson.annotations.SerializedName
import im.dacer.jetcurrency.api.LiveResponse
import im.dacer.jetcurrency.model.CurrencyWithoutFullName

/**
 * The most recent exchange rate data from Currencylayer.
 *
 * Example API Response:
 * {
 *     [...],
 *     "timestamp": 1430401802,
 *     "source": "USD",
 *     "quotes": {
 *         "USDAED": 3.672982,
 *         "USDAFN": 57.8936,
 *         "USDALL": 126.1652,
 *         [...]
 *     }
 * }
 *
 * Documentation:
 * https://currencylayer.com/documentation#api_response
 */
// TODO make a BaseResponse that include success and error
data class CurrencylayerLiveResponse(
    @field:SerializedName("success") val success: Boolean,
    @field:SerializedName("timestamp") val timestamp: Int,
    @field:SerializedName("source") val source: String,
    @field:SerializedName("quotes") val quotes: Map<String, Double>,
    @field:SerializedName("error") val error: CurrencylayerError?,
) : LiveResponse {

    /**
     * Return currency list based on source and quotes.
     */
    override fun getCurrencyList(): List<CurrencyWithoutFullName> {
        return quotes.map {
            CurrencyWithoutFullName(it.key.substring(source.length), it.value)
        }
    }
}
