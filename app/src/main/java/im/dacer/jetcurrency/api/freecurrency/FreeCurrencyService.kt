package im.dacer.jetcurrency.api.freecurrency

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * https://github.com/fawazahmed0/currency-api
 */
interface FreeCurrencyService {

    @GET("gh/fawazahmed0/currency-api@1/latest/currencies/usd.min.json")
    suspend fun live(): Response<FreeCurrencyUsdBasedLiveResponse>

    @GET("gh/fawazahmed0/currency-api@1/latest/currencies.min.json")
    suspend fun list(): Response<FreeCurrencyListResponse>

    companion object {
        private const val BASE_URL = "https://cdn.jsdelivr.net"

        fun create(): FreeCurrencyService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(FreeCurrencyService::class.java)
        }
    }
}
