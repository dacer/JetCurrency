package im.dacer.jetcurrency.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import im.dacer.jetcurrency.MainCoroutineRule
import im.dacer.jetcurrency.api.freecurrency.FreeCurrencyService
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
class FreeCurrencyRepositoryTest {
    private val currencyDao: CurrencyDao = FakeCurrencyDao()
    private lateinit var freeCurrencyRepository: FreeCurrencyRepository
    private lateinit var preferenceHelper: PreferenceHelper

    private val mockFreeCurrencyService = mock<FreeCurrencyService> {}
    private val mockNetworkUtils = mock<NetworkUtils> {
        on { isNetworkConnected() } doReturn true
    }

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        preferenceHelper = PreferenceHelper(context)
        freeCurrencyRepository = FreeCurrencyRepository(
            mockFreeCurrencyService,
            currencyDao,
            preferenceHelper,
            mockNetworkUtils,
            Dispatchers.Unconfined,
            Dispatchers.Unconfined
        )
        mainCoroutineRule.runBlockingTest {
            whenever(mockFreeCurrencyService.live()).thenReturn(
                CurrencyRepositoryFactory.FreeCurrency.mockLiveResponse()
            )
            whenever(mockFreeCurrencyService.list()).thenReturn(
                CurrencyRepositoryFactory.FreeCurrency.mockListResponse()
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
        freeCurrencyRepository.getLatestData()

        verify(mockFreeCurrencyService, times(1)).live()
        Truth.assertThat(freeCurrencyRepository.getLatestData()).containsExactlyElementsIn(
            CurrencyRepositoryFactory.mockSyncResult
        )
        Truth.assertThat(preferenceHelper.currencyUpdatedAt).isGreaterThan(lastUpdateAt)
    }

    @Test
    fun doNotCallRemoteApi_whenOffline() = mainCoroutineRule.runBlockingTest {
        whenever(mockNetworkUtils.isNetworkConnected()).thenReturn(false)
        currencyDao.insertCurrencies(
            *CurrencyRepositoryFactory.mockSyncResult.toTypedArray()
        )

        verify(mockFreeCurrencyService, never()).live()
        Truth.assertThat(freeCurrencyRepository.getLatestData()).containsExactlyElementsIn(
            CurrencyRepositoryFactory.mockSyncResult
        )
    }

    @Test
    fun reFetchCurrencyNameList_whenRemoteCurrencyNameListChanged() =
        mainCoroutineRule.runBlockingTest {
            currencyDao.insertCurrencies(
                *CurrencyRepositoryFactory.mockSyncResult.toTypedArray()
            )
            whenever(mockFreeCurrencyService.live()).thenReturn(
                CurrencyRepositoryFactory.FreeCurrency.mockChangedLiveResponse()
            )
            whenever(mockFreeCurrencyService.list()).thenReturn(
                CurrencyRepositoryFactory.FreeCurrency.mockChangedListResponse()
            )
            val lastUpdateAt = System.currentTimeMillis() - (REFRESH_DURATION_IN_MILLIS + 10)
            preferenceHelper.currencyUpdatedAt = lastUpdateAt
            freeCurrencyRepository.getLatestData()

            verify(mockFreeCurrencyService, times(1)).live()
            Truth.assertThat(freeCurrencyRepository.getLatestData()).containsExactlyElementsIn(
                CurrencyRepositoryFactory.mockSyncResultAfterChanged
            )
            Truth.assertThat(preferenceHelper.currencyUpdatedAt).isGreaterThan(lastUpdateAt)
        }
}
