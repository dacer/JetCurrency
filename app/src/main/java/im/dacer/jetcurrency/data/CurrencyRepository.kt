package im.dacer.jetcurrency.data

import im.dacer.jetcurrency.api.LiveResponse
import im.dacer.jetcurrency.di.IoDispatcher
import im.dacer.jetcurrency.di.MainDispatcher
import im.dacer.jetcurrency.model.Currency
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response

abstract class CurrencyRepository(
    private val currencyDao: CurrencyDao,
    private val preferenceHelper: PreferenceHelper,
    @IoDispatcher private val defaultIoDispatcher: CoroutineDispatcher,
    @MainDispatcher private val defaultMainDispatcher: CoroutineDispatcher
) {
    class NetworkRequestError(message: String) : Exception(message)

    fun getShowingCurrencies(): Flow<List<Currency>> = currencyDao.getShowingCurrencies()

    fun getAllCurrencies(): Flow<List<Currency>> = currencyDao.getAll()

    suspend fun updateCurrencies(vararg currencies: Currency?) =
        withContext(defaultIoDispatcher) {
            currencyDao.updateCurrencies(*currencies.filterNotNull().toTypedArray())
        }

    suspend fun refreshData(): Result<List<Currency>> = withContext(defaultIoDispatcher) {
        return@withContext try {
            Result.Success(getLatestData())
        } catch (e: NetworkRequestError) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(RuntimeException("Unknown error"))
        }
    }

    protected suspend fun getLocalData(): List<Currency> = withContext(defaultIoDispatcher) {
        currencyDao.getAll().first()
    }

    /**
     * Will re-fetch currency name list if the quantity of local currencies and remote currencies are not same.
     *
     * FIXME: This function wouldn't get latest currency list if remote currencies and same quantity with local currencies. I want a version code in Currencylayer list API...
     */
    protected suspend fun fetchLiveCurrencyData(
        doBeforeReFetchCurrencyName: suspend () -> Unit = {}
    ) = withContext(defaultIoDispatcher) {
        val response = getRemoteCurrencyData()
        val responseBody = response.body()
        if (!response.isSuccessful) throw NetworkRequestError("code ${response.code()}")
        if (responseBody == null) throw NetworkRequestError("api error")

        val remoteCurrencyList = responseBody.getCurrencyList()

        if (remoteCurrencyList.size != currencyDao.getRowCount()) {
            doBeforeReFetchCurrencyName.invoke()
            currencyDao.deleteAll()
            fetchCurrencyNameList()
        }
        currencyDao.updateCurrencies(*remoteCurrencyList.toTypedArray())

        val localCurrencyList = getLocalData()
        updateCurrencyRefreshTime()
        initCurrencyDataIfNeed(localCurrencyList)
        return@withContext localCurrencyList
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
    }

    @Throws(NetworkRequestError::class)
    abstract suspend fun getLatestData(): List<Currency>
    abstract suspend fun fetchCurrencyNameList()
    protected abstract suspend fun getRemoteCurrencyData(): Response<out LiveResponse>
}
