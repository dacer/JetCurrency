package im.dacer.jetcurrency.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import im.dacer.jetcurrency.api.CurrencylayerService
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideCurrencylayerService(): CurrencylayerService {
        return CurrencylayerService.create()
    }
}
