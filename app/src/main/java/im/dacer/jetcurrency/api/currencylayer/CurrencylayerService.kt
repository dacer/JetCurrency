package im.dacer.jetcurrency.api.currencylayer

import im.dacer.jetcurrency.BuildConfig
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencylayerService {

    @GET("live")
    suspend fun live(
        @Query("access_key") accessKey: String = ACCESS_KEY
    ): Response<CurrencylayerLiveResponse>

    @GET("list")
    suspend fun list(
        @Query("access_key") accessKey: String = ACCESS_KEY
    ): Response<CurrencylayerListResponse>

    companion object {
        // Since free plan cannot use HTTPS encryption, we have to use http://
        private const val BASE_URL = "http://api.currencylayer.com"
        private const val ACCESS_KEY = BuildConfig.CURRENCYLAYER_ACCESS_KEY

        fun create(): CurrencylayerService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(CurrencylayerService::class.java)
        }
    }
}
