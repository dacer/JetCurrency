package im.dacer.jetcurrency.data

import im.dacer.jetcurrency.model.Currency
import kotlinx.coroutines.flow.Flow

class MockCurrencyRepository : CurrencyRepository {
    private var mockLatestData: List<Currency> = emptyList()

    fun mockLatestData(mockData: List<Currency>) {
        mockLatestData = mockData
    }

    override fun getAllCurrencies(): Flow<List<Currency>> {
        TODO("Not yet implemented")
    }

    override fun getShowingCurrencies(): Flow<List<Currency>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCurrencies(vararg currencies: Currency?) {
        TODO("Not yet implemented")
    }

    override suspend fun refreshData(): Result<List<Currency>> {
        TODO("Not yet implemented")
    }
}
