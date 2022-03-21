package im.dacer.jetcurrency.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CurrencyTest {

    private val usdCurrency = Currency("USD", "United States Dollar", 1.0)
    private val jpyCurrency = Currency("JPY", "Japanese Yen", 113.666004)

    @Test
    fun `1 USD should be 113_666004 JPY`() {
        assertThat(jpyCurrency.convertFrom(1.0, usdCurrency))
            .isEqualTo(113.666004)
    }

    @Test
    fun `0 USD should be 0 JPY`() {
        assertThat(jpyCurrency.convertFrom(0.0, usdCurrency))
            .isEqualTo(0.0)
    }

    @Test
    fun `1 USD should be 1 USD`() {
        assertThat(usdCurrency.convertFrom(1.0, usdCurrency))
            .isEqualTo(1.0)
    }
}
