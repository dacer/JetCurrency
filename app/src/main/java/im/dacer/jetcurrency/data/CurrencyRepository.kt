package im.dacer.jetcurrency.data

import im.dacer.jetcurrency.model.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    class NetworkRequestError(message: String) : Exception(message)

    fun getAllCurrencies(): Flow<List<Currency>>

    fun getShowingCurrencies(): Flow<List<Currency>>

    suspend fun updateCurrencies(vararg currencies: Currency?)

    /**
     * This function will only fetch data from the Api once in 30 minutes due to the API limit,
     */
    suspend fun refreshData(): Result<List<Currency>>
}
