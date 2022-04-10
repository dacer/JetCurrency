package im.dacer.jetcurrency.data

import im.dacer.jetcurrency.api.LiveResponse
import im.dacer.jetcurrency.api.freecurrency.FreeCurrencyService
import im.dacer.jetcurrency.di.IoDispatcher
import im.dacer.jetcurrency.di.MainDispatcher
import im.dacer.jetcurrency.model.Currency
import im.dacer.jetcurrency.utils.NetworkUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class FreeCurrencyRepository @Inject constructor(
    private val freeCurrencyService: FreeCurrencyService,
    private val currencyDao: CurrencyDao,
    preferenceHelper: PreferenceHelper,
    private val networkUtils: NetworkUtils,
    @IoDispatcher private val defaultIoDispatcher: CoroutineDispatcher,
    @MainDispatcher private val defaultMainDispatcher: CoroutineDispatcher
) : CurrencyRepository(currencyDao, preferenceHelper, defaultIoDispatcher, defaultMainDispatcher) {

    @Throws(NetworkRequestError::class)
    override suspend fun getLatestData(): List<Currency> = withContext(defaultIoDispatcher) {
        if (!networkUtils.isNetworkConnected()) {
            return@withContext getLocalData()
        }

        if (currencyDao.getRowCount() == 0) {
            fetchCurrencyNameList()
        }
        return@withContext fetchLiveCurrencyData()
    }

    /**
     * Fetch currency name list from Currencylayer.
     * Will replace local data when conflicted.
     */
    @Throws(NetworkRequestError::class)
    override suspend fun fetchCurrencyNameList() = withContext(defaultIoDispatcher) {
        val response = freeCurrencyService.list()
        val responseBody = response.body()
        if (!response.isSuccessful) throw NetworkRequestError("code ${response.code()}")
        if (responseBody == null) throw NetworkRequestError("api error")

        val remoteCurrencyNameList = responseBody.map {
            Currency(it.key.uppercase(), it.value, 0.0)
        }
        currencyDao.insertCurrencies(*remoteCurrencyNameList.toTypedArray())
    }

    override suspend fun getRemoteCurrencyData(): Response<out LiveResponse> =
        freeCurrencyService.live()
}
