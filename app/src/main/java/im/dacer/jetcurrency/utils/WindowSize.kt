package im.dacer.jetcurrency.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator

enum class WindowSize { COMPACT, MEDIUM, EXPANDED }

/**
 * Remembers the [WindowSize] class for the window corresponding to the current window metrics.
 * return Pair(widthWindowSize, heightWindowSize)
 */
@Composable
fun Activity.rememberWindowSize(): Pair<WindowSize, WindowSize> {
    val configuration = LocalConfiguration.current
    val windowMetrics = remember(configuration) {
        WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)
    }
    val windowDpSize = with(LocalDensity.current) {
        windowMetrics.bounds.toComposeRect().size.toDpSize()
    }

    return Pair(getWidthWindowSize(windowDpSize), getHeightWindowSize(windowDpSize))
}

fun getWidthWindowSize(windowDpSize: DpSize): WindowSize = when {
    windowDpSize.width < 0.dp -> throw IllegalArgumentException("Dp value cannot be negative")
    windowDpSize.width < 600.dp -> WindowSize.COMPACT
    windowDpSize.width < 840.dp -> WindowSize.MEDIUM
    else -> WindowSize.EXPANDED
}

fun getHeightWindowSize(windowDpSize: DpSize): WindowSize = when {
    windowDpSize.width < 0.dp -> throw IllegalArgumentException("Dp value cannot be negative")
    windowDpSize.height < 480.dp -> WindowSize.COMPACT
    windowDpSize.height < 900.dp -> WindowSize.MEDIUM
    else -> WindowSize.EXPANDED
}
