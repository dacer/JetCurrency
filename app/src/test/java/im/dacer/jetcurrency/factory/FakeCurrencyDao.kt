package im.dacer.jetcurrency.factory

import im.dacer.jetcurrency.data.CurrencyDao
import im.dacer.jetcurrency.model.Currency
import im.dacer.jetcurrency.model.CurrencyWithoutFullName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCurrencyDao : CurrencyDao {
    private var itemsFlow = MutableStateFlow(listOf<Currency>())

    override suspend fun getRowCount(): Int = itemsFlow.value.size

    override fun getAll(): Flow<List<Currency>> = itemsFlow

    override fun getShowingCurrencies(): Flow<List<Currency>> =
        itemsFlow.map { it.filter { c -> c.isShowing } }

    override suspend fun insertCurrencies(vararg currencies: Currency) {
        itemsFlow.update { currencies.toList() }
    }

    override suspend fun updateCurrencies(vararg currencies: CurrencyWithoutFullName) {
        itemsFlow.update { items ->
            items.map { item ->
                val c = currencies.find { it.code == item.code }
                c?.let {
                    return@map Currency(
                        code = c.code,
                        fullName = item.fullName,
                        exchangeRateFromUsd = c.exchangeRateFromUsd,
                        order = item.order
                    )
                }
                item
            }
        }
    }

    override suspend fun updateCurrencies(vararg currencies: Currency) {
        itemsFlow.update { items ->
            items.map { item ->
                val c = currencies.find { it.code == item.code }
                c?.let {
                    return@map Currency(
                        code = c.code,
                        fullName = c.fullName,
                        exchangeRateFromUsd = c.exchangeRateFromUsd,
                        order = c.order
                    )
                }
                item
            }
        }
    }

    override suspend fun deleteAll() {
        itemsFlow.update { emptyList() }
    }
}
