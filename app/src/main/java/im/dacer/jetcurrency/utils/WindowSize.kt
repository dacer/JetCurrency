package im.dacer.jetcurrency.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator

/**
 * The order of this enum is IMPORTANT! It must be from small to big.
 */
enum class WindowSize { TINY, COMPACT, MEDIUM, EXPANDED }
data class WindowState(
    val widthWindowSize: WindowSize,
    val heightWindowSize: WindowSize,
    val isLandscape: Boolean
)

/**
 * Remembers the [WindowState] class for the window corresponding to the current window metrics.
 */
@Composable
fun Activity.rememberWindowSize(): WindowState {
    val configuration = LocalConfiguration.current
    val windowMetrics = remember(configuration) {
        WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)
    }
    val windowDpSize = with(LocalDensity.current) {
        windowMetrics.bounds.toComposeRect().size.toDpSize()
    }

    return WindowState(
        getWidthWindowSize(windowDpSize),
        getHeightWindowSize(windowDpSize),
        windowDpSize.width > windowDpSize.height
    )
}

private fun getWidthWindowSize(windowDpSize: DpSize): WindowSize = windowDpSize.width.toWidthWindowSize()

private fun getHeightWindowSize(windowDpSize: DpSize): WindowSize = windowDpSize.height.toHeightWindowSize()

fun Dp.toWidthWindowSize(): WindowSize = when {
    this < 0.dp -> throw IllegalArgumentException("Dp value cannot be negative")
    this < 240.dp -> WindowSize.TINY
    this < 600.dp -> WindowSize.COMPACT
    this < 840.dp -> WindowSize.MEDIUM
    else -> WindowSize.EXPANDED
}

fun Dp.toHeightWindowSize(): WindowSize = when {
    this < 0.dp -> throw IllegalArgumentException("Dp value cannot be negative")
    this < 240.dp -> WindowSize.TINY
    this < 480.dp -> WindowSize.COMPACT
    this < 900.dp -> WindowSize.MEDIUM
    else -> WindowSize.EXPANDED
}
