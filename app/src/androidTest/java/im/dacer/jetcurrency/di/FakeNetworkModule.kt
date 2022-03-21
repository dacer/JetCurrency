package im.dacer.jetcurrency.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import im.dacer.jetcurrency.api.CurrencylayerService
import im.dacer.jetcurrency.api.FakeCurrencylayerService
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
@Module
object FakeNetworkModule {

    @Singleton
    @Provides
    fun provideCurrencylayerService(): CurrencylayerService {
        return FakeCurrencylayerService()
    }
}
