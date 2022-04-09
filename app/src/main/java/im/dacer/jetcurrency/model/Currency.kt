package im.dacer.jetcurrency.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import im.dacer.jetcurrency.utils.Calculator
import java.text.NumberFormat
import java.util.Locale

@Entity
data class Currency(
    @PrimaryKey val code: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    // Exchange rate from USD to this currency (Maybe it's better to use BigDecimal)
    @ColumnInfo(name = "exchange_rate_from_usd") val exchangeRateFromUsd: Double,
    var order: Int? = null,
) {

    val isShowing: Boolean
        get() {
            return order != null
        }

    fun setOrder(order: Int?): Currency {
        this.order = order
        return this
    }

    /**
     * return the amount in this currency converted from [amount] in [currency]
     */
    fun convertFrom(amount: Double, currency: Currency): Double {
        val currentUsdAmount = amount / currency.exchangeRateFromUsd
        return exchangeRateFromUsd * currentUsdAmount
    }

    /**
     * Used for storing data for displaying. Will not be saved in the database.
     */
    class Data(var value: Double = 0.0) {
        private val numberFormat: NumberFormat by lazy {
            // TODO show better format
            NumberFormat.getNumberInstance(Locale.getDefault())
        }

        /**
         * The expression for calculating the value.
         * Example: 1+3-2
         */
        private var expression: String = ""

        /**
         * The string that shown on the screen.
         * Example: 123.4 or 1+2/3
         */
        val displayValue: String
            get() {
                if (expression.isNotEmpty()) return expression
                return numberFormat.format(value)
            }

        fun generateExpressionIfNeed(): Data {
            if (expression.isEmpty()) expression = displayValue
            return this
        }

        fun update(value: Double, expression: String = ""): Data {
            this.value = value
            this.expression = expression
            return this
        }

        /**
         * calculate this.value according to $currency
         */
        fun convertValue(
            currency: Currency,
            fromCurrency: Currency,
            fromCurrencyValue: Double
        ): Data {
            return update(
                value = currency.convertFrom(fromCurrencyValue, fromCurrency)
            )
        }

        /**
         * add $str to this.expression and calculate the value
         */
        fun addToExpression(c: Char): Data {
            val calculateResult = Calculator.performAdd(expression, c)
            return update(
                expression = calculateResult.expression,
                value = calculateResult.value
            )
        }

        fun deleteLastStrInExpression(): Data {
            val calculateResult = Calculator.performBackspace(expression)
            return update(
                expression = calculateResult.expression,
                value = calculateResult.value
            )
        }

        companion object {
            fun Build(currency: Currency, fromCurrency: Currency, fromCurrencyValue: Double): Data {
                return Data().convertValue(currency, fromCurrency, fromCurrencyValue)
            }
        }
    }
}

fun List<Currency>.filterSortedShowing(): List<Currency> =
    this.filter { it.isShowing }.sortedBy { it.order }
