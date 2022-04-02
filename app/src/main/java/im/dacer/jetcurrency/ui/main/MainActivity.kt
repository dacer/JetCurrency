package im.dacer.jetcurrency.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import im.dacer.jetcurrency.ui.components.MainScreen
import im.dacer.jetcurrency.ui.theme.CurrencyTheme
import im.dacer.jetcurrency.utils.rememberWindowSize

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    @ExperimentalFoundationApi
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val (widthWindowSize, heightWindowSize) = rememberWindowSize()
            CurrencyTheme {
                ProvideWindowInsets {
                    MainScreen(viewModel, heightWindowSize)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCurrencyData()
    }
}
