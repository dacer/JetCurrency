package im.dacer.jetcurrency.data

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceHelper @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sp = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)

    var currencyUpdatedAt: Long
        get() = sp.getLong(CURRENCY_UPDATED_AT, 0L)
        set(value) {
            sp.edit { putLong(CURRENCY_UPDATED_AT, value) }
        }

    companion object {
        const val PREFERENCE_FILE_NAME = "pref"

        const val CURRENCY_UPDATED_AT = "CURRENCY_UPDATED_AT"
    }
}
