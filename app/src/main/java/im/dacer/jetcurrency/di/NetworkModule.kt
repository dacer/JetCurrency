package im.dacer.jetcurrency.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import im.dacer.jetcurrency.api.currencylayer.CurrencylayerService
import im.dacer.jetcurrency.api.freecurrency.FreeCurrencyService
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideCurrencylayerService(): CurrencylayerService {
        return CurrencylayerService.create()
    }

    @Singleton
    @Provides
    fun provideFreeCurrencyService(): FreeCurrencyService {
        return FreeCurrencyService.create()
    }
}
