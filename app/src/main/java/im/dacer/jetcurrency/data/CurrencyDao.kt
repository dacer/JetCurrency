package im.dacer.jetcurrency.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import im.dacer.jetcurrency.model.Currency
import im.dacer.jetcurrency.model.CurrencyWithoutFullName
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Query("SELECT COUNT(code) FROM currency")
    suspend fun getRowCount(): Int

    @Query("SELECT * FROM currency ORDER BY code")
    fun getAll(): Flow<List<Currency>>

    @Query("SELECT * FROM currency WHERE `order` IS NOT NULL ORDER BY `order` ASC")
    fun getShowingCurrencies(): Flow<List<Currency>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(vararg currencies: Currency)

    @Update(entity = Currency::class)
    suspend fun updateCurrencies(vararg currencies: CurrencyWithoutFullName)

    @Update
    suspend fun updateCurrencies(vararg currencies: Currency)

    @Query("DELETE FROM currency")
    suspend fun deleteAll()
}
