package im.dacer.jetcurrency.data

import im.dacer.jetcurrency.api.LiveResponse
import im.dacer.jetcurrency.api.currencylayer.CurrencylayerService
import im.dacer.jetcurrency.di.IoDispatcher
import im.dacer.jetcurrency.di.MainDispatcher
import im.dacer.jetcurrency.model.Currency
import im.dacer.jetcurrency.utils.NetworkUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class CurrencylayerRepository @Inject constructor(
    private val currencylayerService: CurrencylayerService,
    private val currencyDao: CurrencyDao,
    private val preferenceHelper: PreferenceHelper,
    private val networkUtils: NetworkUtils,
    @IoDispatcher private val defaultIoDispatcher: CoroutineDispatcher,
    @MainDispatcher private val defaultMainDispatcher: CoroutineDispatcher
) : CurrencyRepository(currencyDao, preferenceHelper, defaultIoDispatcher, defaultMainDispatcher) {

    /**
     * This function will only fetch data from the Currencylayer once in 30 minutes due to the API limit,
     * Repeat calls within 30 minutes will use local cache.
     */
    @Throws(NetworkRequestError::class)
    override suspend fun getLatestData(): List<Currency> = withContext(defaultIoDispatcher) {
        if (!networkUtils.isNetworkConnected() || !isDataNeedRefresh()) {
            delay(1000) // TODO: DELETE IT, it's added for simulating slow internet connection and showing loading animation.
            return@withContext getLocalData()
        }

        if (currencyDao.getRowCount() == 0) {
            fetchCurrencyNameList()
            delayBeforeNextRequest()
        }
        return@withContext fetchLiveCurrencyData { delayBeforeNextRequest() }
    }

    /**
     * Fetch currency name list from Currencylayer.
     * Will replace local data when conflicted.
     */
    @Throws(NetworkRequestError::class)
    override suspend fun fetchCurrencyNameList() = withContext(defaultIoDispatcher) {
        val response = currencylayerService.list()
        val responseBody = response.body()
        if (!response.isSuccessful) throw NetworkRequestError("code ${response.code()}")
        if (responseBody?.success != true) throw NetworkRequestError(
            response.body()?.error?.info ?: "api error"
        )

        val remoteCurrencyNameList = responseBody.currencies.map {
            Currency(it.key, it.value, 0.0)
        }
        currencyDao.insertCurrencies(*remoteCurrencyNameList.toTypedArray())
    }

    override suspend fun getRemoteCurrencyData(): Response<out LiveResponse> =
        currencylayerService.live()

    /**
     * Currencylayer will return 106 error If call a request immediately after a request.
     * So we need to wait before next request.
     */
    private suspend fun delayBeforeNextRequest() {
        delay(2000)
    }

    private suspend fun isDataNeedRefresh(): Boolean {
        return withContext(defaultMainDispatcher) {
            System.currentTimeMillis() - preferenceHelper.currencyUpdatedAt >
                REFRESH_DURATION_IN_MILLIS
        }
    }

    companion object {
        const val REFRESH_DURATION_IN_MILLIS = 300 * 60 * 1000 // 300 minutes
    }
}
