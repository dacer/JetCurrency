package im.dacer.jetcurrency.ui.theme

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import im.dacer.jetcurrency.R

/**
 * Load a string resource with formatting.
 *
 * @param id the resource identifier
 * @param formatArgs the format arguments
 * @return the string data associated with the resource
 */
@Composable
@ReadOnlyComposable
@DrawableRes
fun drawableResource(name: String): Int {
    val resources = resources()
    val context = LocalContext.current
    val result = resources.getIdentifier(name, "drawable", context.packageName)

    return if (result == 0) {
        R.drawable.flag_empty
    } else {
        result
    }
}

/**
 * A composable function that returns the [Resources]. It will be recomposed when [Configuration]
 * gets updated.
 */
@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
