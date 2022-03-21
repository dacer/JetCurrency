package im.dacer.jetcurrency.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import im.dacer.jetcurrency.data.CurrencyRepository
import im.dacer.jetcurrency.data.CurrencylayerRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun provideCurrencyRepository(
        repository: CurrencylayerRepository
    ): CurrencyRepository
}
