package im.dacer.jetcurrency.factory

import im.dacer.jetcurrency.api.CurrencylayerListResponse
import im.dacer.jetcurrency.api.CurrencylayerLiveResponse
import im.dacer.jetcurrency.model.Currency
import retrofit2.Response

object CurrencylayerRepositoryFactory {
    private val mockRemoteCurrencyData = hashMapOf(
        "USDAFN" to 10.01,
        "USDALL" to 16.61,
        "USDUSD" to 1.0,
    )

    private val mockRemoteCurrencyNameList = hashMapOf(
        "AFN" to "Afghan Afghani",
        "ALL" to "Albanian Lek",
        "USD" to "United States Dollar",
    )

    private val mockRemoteChangedCurrencyData = hashMapOf(
        "USDAFN" to 11.01,
        "USDALL" to 17.61,
        "USDUSD" to 1.0,
        "USDJPY" to 170.61,
    )

    private val mockRemoteChangedCurrencyNameList = hashMapOf(
        "AFN" to "Afghan Afghani",
        "ALL" to "Albanian Lek",
        "USD" to "United States Dollar",
        "JPY" to "Japanese Yen"
    )

    val mockSyncResult
        get() = listOf(
            Currency("AFN", "Afghan Afghani", 10.01),
            Currency("ALL", "Albanian Lek", 16.61),
            Currency("USD", "United States Dollar", 1.0, order = 1),
        )

    val mockSyncResultAfterChanged
        get() = listOf(
            Currency("AFN", "Afghan Afghani", 11.01),
            Currency("ALL", "Albanian Lek", 17.61),
            Currency("USD", "United States Dollar", 1.0, order = 1),
            Currency("JPY", "Japanese Yen", 170.61, order = 0),
        )

    fun mockCurrencylayerLiveResponse(): Response<CurrencylayerLiveResponse> =
        Response.success(
            CurrencylayerLiveResponse(
                true,
                (System.currentTimeMillis() / 1000).toInt(),
                "USD",
                mockRemoteCurrencyData,
                null
            )
        )

    fun mockChangedCurrencylayerLiveResponse(): Response<CurrencylayerLiveResponse> =
        Response.success(
            CurrencylayerLiveResponse(
                true,
                (System.currentTimeMillis() / 1000).toInt(),
                "USD",
                mockRemoteChangedCurrencyData,
                null
            )
        )

    fun mockCurrencylayerListResponse(): Response<CurrencylayerListResponse> =
        Response.success(
            CurrencylayerListResponse(
                true,
                mockRemoteCurrencyNameList,
                null
            )
        )

    fun mockChangedCurrencylayerListResponse(): Response<CurrencylayerListResponse> =
        Response.success(
            CurrencylayerListResponse(
                true,
                mockRemoteChangedCurrencyNameList,
                null
            )
        )
}
