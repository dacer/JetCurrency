package im.dacer.jetcurrency.api

import im.dacer.jetcurrency.model.CurrencyWithoutFullName

interface LiveResponse {
    fun getCurrencyList(): List<CurrencyWithoutFullName>
}