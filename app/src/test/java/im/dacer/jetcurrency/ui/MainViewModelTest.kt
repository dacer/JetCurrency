package im.dacer.jetcurrency.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import im.dacer.jetcurrency.MainCoroutineRule
import im.dacer.jetcurrency.api.FakeCurrencylayerService
import im.dacer.jetcurrency.data.CurrencylayerRepository
import im.dacer.jetcurrency.data.PreferenceHelper
import im.dacer.jetcurrency.factory.FakeCurrencyDao
import im.dacer.jetcurrency.ui.main.MainUiState
import im.dacer.jetcurrency.ui.main.MainViewModel
import im.dacer.jetcurrency.ui.main.MainViewModel.Companion.DEFAULT_CURRENCY_1
import im.dacer.jetcurrency.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var currencylayerRepository: CurrencylayerRepository
    private val mockNetworkUtils = mock<NetworkUtils> {
        on { isNetworkConnected() } doReturn true
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setupViewModel() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        currencylayerRepository = CurrencylayerRepository(
            FakeCurrencylayerService(),
            FakeCurrencyDao(),
            PreferenceHelper(context),
            mockNetworkUtils,
            Dispatchers.Unconfined,
            Dispatchers.Unconfined
        )
        mainViewModel = MainViewModel(
            context,
            currencylayerRepository,
            mainCoroutineRule.dispatcher
        )
    }

    @Test
    fun refreshCurrencyData_andMainViewModelStateUpdated() = mainCoroutineRule.runBlockingTest {
        mainViewModel.uiState.test(timeoutMs = 2000) {
            mainViewModel.refreshCurrencyData()

            awaitItem().let {
                assertThat(it.isLoading).isEqualTo(true)
                assertThat(it is MainUiState.NoData).isEqualTo(true)
            }
            awaitItem().let {
                assertThat(it.isLoading).isEqualTo(false)
                it as MainUiState.HasData
                assertThat(it.focusedCurrencyCode).isEqualTo(DEFAULT_CURRENCY_1)
                assertThat(it.dataMap.isEmpty()).isEqualTo(false)
            }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun refreshCurrencyData_andCurrencyListUpdated() = mainCoroutineRule.runBlockingTest {
        mainViewModel.currencyList.test(timeoutMs = 2000) {
            mainViewModel.refreshCurrencyData()

            assertThat(awaitItem().isEmpty()).isEqualTo(true)
            assertThat(awaitItem().isEmpty()).isEqualTo(false)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun refreshCurrencyData_afterInputData_andTheDataIsKept() = mainCoroutineRule.runBlockingTest {
        mainViewModel.uiState.test(timeoutMs = 2000) {
            mainViewModel.refreshCurrencyData()

            assertThat(awaitItem().isLoading).isEqualTo(true)
            awaitItem().let {
                assertThat(it.isLoading).isEqualTo(false)
                it as MainUiState.HasData
                assertThat(it.dataMap.isEmpty()).isEqualTo(false)
                assertThat(it.focusedCurrencyCode).isEqualTo("USD")
            }

            mainViewModel.setFocusedCurrency("JPY")
            assertThat((awaitItem() as MainUiState.HasData).focusedCurrencyCode).isEqualTo(
                "JPY"
            )

            mainViewModel.addCharToCurrentAmount('1')
            awaitItem().let { state ->
                assertThat(state.isLoading).isEqualTo(false)
                state as MainUiState.HasData
                assertThat(state.dataMap["JPY"]?.displayValue).isEqualTo(
                    "1"
                )
                assertThat(state.dataMap["JPY"]?.value).isEqualTo(
                    1.0
                )
            }

            mainViewModel.refreshCurrencyData()
            assertThat(awaitItem().isLoading).isEqualTo(true)
            awaitItem().let { state ->
                state as MainUiState.HasData
                assertThat(state.isLoading).isEqualTo(false)
                assertThat(state.focusedCurrencyCode).isEqualTo(
                    "JPY"
                )
                assertThat(state.dataMap["JPY"]?.displayValue).isEqualTo(
                    "1"
                )
                assertThat(state.dataMap["JPY"]?.value).isEqualTo(
                    1.0
                )
            }

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun setAfnToFocus_andGetFocused() = mainCoroutineRule.runBlockingTest {
        mainViewModel.uiState.test(timeoutMs = 2000) {
            mainViewModel.refreshCurrencyData()
            assertThat(awaitItem().isLoading).isEqualTo(true)

            awaitItem().let {
                assertThat(it.isLoading).isEqualTo(false)
                it as MainUiState.HasData
                assertThat(it.dataMap.isEmpty()).isEqualTo(false)
                assertThat(it.focusedCurrencyCode).isEqualTo("USD")
            }

            mainViewModel.setFocusedCurrency("JPY")
            assertThat((awaitItem() as MainUiState.HasData).focusedCurrencyCode).isEqualTo(
                "JPY"
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun clickBackspace_andTheDataChanged() = mainCoroutineRule.runBlockingTest {
        mainViewModel.uiState.test(timeoutMs = 2000) {
            mainViewModel.refreshCurrencyData()

            assertThat(awaitItem().isLoading).isEqualTo(true)
            awaitItem().let {
                assertThat(it.isLoading).isEqualTo(false)
                it as MainUiState.HasData
                assertThat(it.dataMap.isEmpty()).isEqualTo(false)
                assertThat(it.focusedCurrencyCode).isEqualTo("USD")
            }

            mainViewModel.addCharToCurrentAmount('1')
            awaitItem()
            mainViewModel.addCharToCurrentAmount('2')
            awaitItem().let { state ->
                state as MainUiState.HasData
                assertThat(state.dataMap["USD"]?.displayValue).isEqualTo(
                    "12"
                )
                assertThat(state.dataMap["USD"]?.value).isEqualTo(
                    12.0
                )
                assertThat(state.dataMap["JPY"]?.value).isEqualTo(
                    1382.70048
                )
            }

            mainViewModel.onClickBackspace()
            awaitItem().let { state ->
                state as MainUiState.HasData
                assertThat(state.dataMap["USD"]?.displayValue).isEqualTo(
                    "1"
                )
                assertThat(state.dataMap["USD"]?.value).isEqualTo(
                    1.0
                )
                assertThat(state.dataMap["JPY"]?.value).isEqualTo(
                    115.22504
                )
            }

            cancelAndConsumeRemainingEvents()
        }
    }
}
