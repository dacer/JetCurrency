package im.dacer.jetcurrency.data

import im.dacer.jetcurrency.api.CurrencylayerService
import im.dacer.jetcurrency.di.IoDispatcher
import im.dacer.jetcurrency.di.MainDispatcher
import im.dacer.jetcurrency.model.Currency
import im.dacer.jetcurrency.utils.NetworkUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencylayerRepository @Inject constructor(
    private val currencylayerService: CurrencylayerService,
    private val currencyDao: CurrencyDao,
    private val preferenceHelper: PreferenceHelper,
    private val networkUtils: NetworkUtils,
    @IoDispatcher private val defaultIoDispatcher: CoroutineDispatcher,
    @MainDispatcher private val defaultMainDispatcher: CoroutineDispatcher
) : CurrencyRepository {

    override fun getShowingCurrencies(): Flow<List<Currency>> = currencyDao.getShowingCurrencies()

    override fun getAllCurrencies(): Flow<List<Currency>> = currencyDao.getAll()

    override suspend fun updateCurrencies(vararg currencies: Currency?) =
        withContext(defaultIoDispatcher) {
            currencyDao.updateCurrencies(*currencies.filterNotNull().toTypedArray())
        }

    override suspend fun refreshData(): Result<List<Currency>> = withContext(defaultIoDispatcher) {
        return@withContext try {
            Result.Success(getLatestData())
        } catch (e: CurrencyRepository.NetworkRequestError) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(RuntimeException("Unknown error"))
        }
    }

    /**
     * This function will only fetch data from the Currencylayer once in 30 minutes due to the API limit,
     * Repeat calls within 30 minutes will use local cache.
     */
    @Throws(CurrencyRepository.NetworkRequestError::class)
    suspend fun getLatestData(): List<Currency> = withContext(defaultIoDispatcher) {
        if (!networkUtils.isNetworkConnected() || !isDataNeedRefresh()) {
            delay(1000) // TODO: DELETE IT, it's added for simulating slow internet connection and showing loading animation.
            return@withContext getLocalData()
        }

        if (currencyDao.getRowCount() == 0) {
            fetchCurrencyNameList()
            delayBeforeNextRequest()
        }
        return@withContext fetchLiveCurrencyData()
    }

    private suspend fun getLocalData(): List<Currency> = withContext(defaultIoDispatcher) {
        currencyDao.getAll().first()
    }

    /**
     * Fetch currency name list from Currencylayer.
     * Will replace local data when conflicted.
     */
    @Throws(CurrencyRepository.NetworkRequestError::class)
    suspend fun fetchCurrencyNameList() = withContext(defaultIoDispatcher) {
        val response = currencylayerService.list()
        val responseBody = response.body()
        if (!response.isSuccessful) throw CurrencyRepository.NetworkRequestError("code ${response.code()}")
        if (responseBody?.success != true) throw CurrencyRepository.NetworkRequestError(
            response.body()?.error?.info ?: "api error"
        )

        val remoteCurrencyNameList = responseBody.currencies.map {
            Currency(it.key, it.value, 0.0)
        }
        currencyDao.insertCurrencies(*remoteCurrencyNameList.toTypedArray())
    }

    /**
     * Will re-fetch currency name list if the quantity of local currencies and remote currencies are not same.
     *
     * FIXME: This function wouldn't get latest currency list if remote currencies and same quantity with local currencies. I want a version code in Currencylayer list API...
     */
    private suspend fun fetchLiveCurrencyData() = withContext(defaultIoDispatcher) {
        val response = currencylayerService.live()
        val responseBody = response.body()
        if (!response.isSuccessful) throw CurrencyRepository.NetworkRequestError("code ${response.code()}")
        if (responseBody?.success != true) throw CurrencyRepository.NetworkRequestError(
            response.body()?.error?.info ?: "api error"
        )

        val remoteCurrencyList = responseBody.getCurrencyList()

        // Need to re-fetch currency name list
        if (remoteCurrencyList.size != currencyDao.getRowCount()) {
            delayBeforeNextRequest()
            currencyDao.deleteAll()
            fetchCurrencyNameList()
        }
        currencyDao.updateCurrencies(*remoteCurrencyList.toTypedArray())
        val localCurrencyList = getLocalData()

        updateCurrencyRefreshTime()
        initCurrencyDataIfNeed(localCurrencyList)
        localCurrencyList
    }

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

    private suspend fun updateCurrencyRefreshTime() {
        withContext(defaultMainDispatcher) {
            preferenceHelper.currencyUpdatedAt = System.currentTimeMillis()
        }
    }

    private fun initCurrencyDataIfNeed(data: List<Currency>) {
        if (data.none { it.isShowing }) {
            data.find { it.code == "JPY" }?.order = 0
            data.find { it.code == "USD" }?.order = 1
        }

        // if (data.none { it.isFocused }) {
        //     data.filterSortedShowing()
        //         .first()
        //         .setFocused(true)
        // }
    }

    companion object {
        const val REFRESH_DURATION_IN_MILLIS = 300 * 60 * 1000 // 300 minutes
    }
}
