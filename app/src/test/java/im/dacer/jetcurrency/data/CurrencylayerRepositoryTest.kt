package im.dacer.jetcurrency.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import im.dacer.jetcurrency.MainCoroutineRule
import im.dacer.jetcurrency.api.currencylayer.CurrencylayerService
import im.dacer.jetcurrency.data.CurrencylayerRepository.Companion.REFRESH_DURATION_IN_MILLIS
import im.dacer.jetcurrency.factory.CurrencyRepositoryFactory
import im.dacer.jetcurrency.factory.FakeCurrencyDao
import im.dacer.jetcurrency.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class CurrencylayerRepositoryTest {
    private val currencyDao: CurrencyDao = FakeCurrencyDao()
    private lateinit var currencylayerRepository: CurrencylayerRepository
    private lateinit var preferenceHelper: PreferenceHelper

    private val mockCurrencylayerService = mock<CurrencylayerService> {}
    private val mockNetworkUtils = mock<NetworkUtils> {
        on { isNetworkConnected() } doReturn true
    }

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        preferenceHelper = PreferenceHelper(context)
        currencylayerRepository = CurrencylayerRepository(
            mockCurrencylayerService,
            currencyDao,
            preferenceHelper,
            mockNetworkUtils,
            Dispatchers.Unconfined,
            Dispatchers.Unconfined
        )
        mainCoroutineRule.runBlockingTest {
            whenever(mockCurrencylayerService.live()).thenReturn(
                CurrencyRepositoryFactory.Currencylayer.mockLiveResponse()
            )
            whenever(mockCurrencylayerService.list()).thenReturn(
                CurrencyRepositoryFactory.Currencylayer.mockListResponse()
            )
        }
    }

    @After
    fun reset() = mainCoroutineRule.runBlockingTest {
        currencyDao.deleteAll()
    }

    @Test
    fun getLatestDataFromApi_AndSaveToDatabase() = mainCoroutineRule.runBlockingTest {
        val lastUpdateAt = System.currentTimeMillis() - (REFRESH_DURATION_IN_MILLIS + 10)
        preferenceHelper.currencyUpdatedAt = lastUpdateAt
        currencylayerRepository.getLatestData()

        verify(mockCurrencylayerService, times(1)).live()
        Truth.assertThat(currencylayerRepository.getLatestData()).containsExactlyElementsIn(
            CurrencyRepositoryFactory.mockSyncResult
        )
        Truth.assertThat(preferenceHelper.currencyUpdatedAt).isGreaterThan(lastUpdateAt)
    }

    @Test
    fun doNotCallRemoteApi_whenGetDataAgainIn30Minutes() = mainCoroutineRule.runBlockingTest {
        currencyDao.insertCurrencies(
            *CurrencyRepositoryFactory.mockSyncResult.toTypedArray()
        )
        val lastUpdateAt = System.currentTimeMillis() - (REFRESH_DURATION_IN_MILLIS - 10)
        preferenceHelper.currencyUpdatedAt = lastUpdateAt

        verify(mockCurrencylayerService, never()).live()
        Truth.assertThat(currencylayerRepository.getLatestData()).containsExactlyElementsIn(
            CurrencyRepositoryFactory.mockSyncResult
        )
        Truth.assertThat(preferenceHelper.currencyUpdatedAt).isEqualTo(lastUpdateAt)
    }

    @Test
    fun doNotCallRemoteApi_whenOffline() = mainCoroutineRule.runBlockingTest {
        whenever(mockNetworkUtils.isNetworkConnected()).thenReturn(false)
        currencyDao.insertCurrencies(
            *CurrencyRepositoryFactory.mockSyncResult.toTypedArray()
        )

        verify(mockCurrencylayerService, never()).live()
        Truth.assertThat(currencylayerRepository.getLatestData()).containsExactlyElementsIn(
            CurrencyRepositoryFactory.mockSyncResult
        )
    }

    @Test
    fun reFetchCurrencyNameList_whenRemoteCurrencyNameListChanged() = mainCoroutineRule.runBlockingTest {
        currencyDao.insertCurrencies(
            *CurrencyRepositoryFactory.mockSyncResult.toTypedArray()
        )
        whenever(mockCurrencylayerService.live()).thenReturn(
            CurrencyRepositoryFactory.Currencylayer.mockChangedLiveResponse()
        )
        whenever(mockCurrencylayerService.list()).thenReturn(
            CurrencyRepositoryFactory.Currencylayer.mockChangedListResponse()
        )
        val lastUpdateAt = System.currentTimeMillis() - (REFRESH_DURATION_IN_MILLIS + 10)
        preferenceHelper.currencyUpdatedAt = lastUpdateAt
        currencylayerRepository.getLatestData()

        verify(mockCurrencylayerService, times(1)).live()
        Truth.assertThat(currencylayerRepository.getLatestData()).containsExactlyElementsIn(
            CurrencyRepositoryFactory.mockSyncResultAfterChanged
        )
        Truth.assertThat(preferenceHelper.currencyUpdatedAt).isGreaterThan(lastUpdateAt)
    }
}
