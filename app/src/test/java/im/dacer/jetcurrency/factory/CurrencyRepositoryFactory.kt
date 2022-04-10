package im.dacer.jetcurrency.factory

import im.dacer.jetcurrency.api.currencylayer.CurrencylayerListResponse
import im.dacer.jetcurrency.api.currencylayer.CurrencylayerLiveResponse
import im.dacer.jetcurrency.api.freecurrency.FreeCurrencyListResponse
import im.dacer.jetcurrency.api.freecurrency.FreeCurrencyUsdBasedLiveResponse
import im.dacer.jetcurrency.model.Currency
import retrofit2.Response

object CurrencyRepositoryFactory {
    private val mockRemoteCurrencyData = hashMapOf(
        "AFN" to 10.01,
        "ALL" to 16.61,
        "USD" to 1.0,
    )

    private val mockRemoteCurrencyNameList = hashMapOf(
        "AFN" to "Afghan Afghani",
        "ALL" to "Albanian Lek",
        "USD" to "United States Dollar",
    )

    private val mockRemoteChangedCurrencyData = hashMapOf(
        "AFN" to 11.01,
        "ALL" to 17.61,
        "USD" to 1.0,
        "JPY" to 170.61,
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

    object Currencylayer {

        fun mockLiveResponse(): Response<CurrencylayerLiveResponse> =
            Response.success(
                CurrencylayerLiveResponse(
                    true,
                    (System.currentTimeMillis() / 1000).toInt(),
                    "USD",
                    mockRemoteCurrencyData.mapKeys { "USD${it.key}" },
                    null
                )
            )

        fun mockChangedLiveResponse(): Response<CurrencylayerLiveResponse> =
            Response.success(
                CurrencylayerLiveResponse(
                    true,
                    (System.currentTimeMillis() / 1000).toInt(),
                    "USD",
                    mockRemoteChangedCurrencyData.mapKeys { "USD${it.key}" },
                    null
                )
            )

        fun mockListResponse(): Response<CurrencylayerListResponse> =
            Response.success(
                CurrencylayerListResponse(
                    true,
                    mockRemoteCurrencyNameList,
                    null
                )
            )

        fun mockChangedListResponse(): Response<CurrencylayerListResponse> =
            Response.success(
                CurrencylayerListResponse(
                    true,
                    mockRemoteChangedCurrencyNameList,
                    null
                )
            )
    }

    object FreeCurrency {

        fun mockLiveResponse(): Response<FreeCurrencyUsdBasedLiveResponse> =
            Response.success(
                FreeCurrencyUsdBasedLiveResponse(
                    "2022-04-09",
                    mockRemoteCurrencyData.mapKeys { it.key.lowercase() },
                )
            )

        fun mockChangedLiveResponse(): Response<FreeCurrencyUsdBasedLiveResponse> =
            Response.success(
                FreeCurrencyUsdBasedLiveResponse(
                    "2022-04-09",
                    mockRemoteChangedCurrencyData.mapKeys { it.key.lowercase() },
                )
            )

        fun mockListResponse(): Response<FreeCurrencyListResponse> =
            Response.success(
                FreeCurrencyListResponse(mockRemoteCurrencyNameList.mapKeys { it.key.lowercase() })
            )

        fun mockChangedListResponse(): Response<FreeCurrencyListResponse> =
            Response.success(
                FreeCurrencyListResponse(mockRemoteChangedCurrencyNameList.mapKeys { it.key.lowercase() })
            )
    }
}
