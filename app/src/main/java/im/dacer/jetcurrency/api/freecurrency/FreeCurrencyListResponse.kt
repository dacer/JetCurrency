package im.dacer.jetcurrency.api.freecurrency

/**
 * A full list of supported currencies from Free Currency Rates API.
 *
 * Example API Response:
 * {
 *   "1inch": "1inch Network",
 *   "ada": "Cardano",
 *   "aed": "United Arab Emirates Dirham",
 *   "afn": "Afghan afghani",
 *   "algo": "Algorand",
 *   "all": "Albanian lek",
 *   "amd": "Armenian dram",
 *   [...]
 * }
 *
 * Documentation:
 * https://github.com/fawazahmed0/currency-api
 */
typealias FreeCurrencyListResponse = Map<String, String>