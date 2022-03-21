package im.dacer.jetcurrency.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import im.dacer.jetcurrency.R

private val calculatorColumns = listOf(
    listOf('7', '4', '1', '0'),
    listOf('8', '5', '2', '.'),
    listOf('9', '6', '3', '='),
    listOf('÷', 'x', '-', '+'),
)

private val buttonHeight = 55.dp

@Composable
fun CalculatorLayout(
    isLoading: Boolean,
    onClickCalculatorButton: (Char) -> Unit,
    onClickBackspace: () -> Unit,
    onClickSettings: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickFilterCurrency: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .systemBarsPadding(top = false)
            .padding(top = 18.dp, bottom = 18.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            SettingsButton(onClickSettings = onClickSettings, modifier = Modifier.weight(1f))
            RefreshButton(
                isLoading,
                onClickRefresh = onClickRefresh,
                modifier = Modifier.weight(1f)
            )
            FilterCurrencyButton(
                onClickFilterCurrency = onClickFilterCurrency,
                modifier = Modifier.weight(1f)
            )
            BackspaceButton(
                onClickBackspace = onClickBackspace,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            calculatorColumns.forEach { column ->
                Column(modifier = Modifier.weight(1f)) {
                    column.forEach { text ->
                        CalculatorButton(text) { onClickCalculatorButton.invoke(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorButton(
    text: Char,
    onClick: (Char) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(
            onClick = { onClick.invoke(text) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .height(buttonHeight)
                    .wrapContentHeight(),
            )
        }
    }
}

@Composable
private fun RefreshButton(
    isLoading: Boolean,
    onClickRefresh: () -> Unit,
    modifier: Modifier
) {
    IconButton(
        onClick = onClickRefresh,
        modifier = modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val rotateDegrees by infiniteTransition.animateFloat(
            initialValue = 0F,
            targetValue = 359F,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        var iconModifier = Modifier
            .height(buttonHeight)
        if (isLoading) {
            iconModifier = iconModifier.then(Modifier.rotate(rotateDegrees))
        }

        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = "Refresh",
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = iconModifier
        )
    }
}

@Composable
private fun SettingsButton(
    onClickSettings: () -> Unit,
    modifier: Modifier
) {
    IconButton(
        onClick = onClickSettings,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Settings",
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.height(buttonHeight)
        )
    }
}

@Composable
private fun FilterCurrencyButton(
    onClickFilterCurrency: () -> Unit,
    modifier: Modifier
) {
    IconButton(
        onClick = onClickFilterCurrency,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_filter_list_24),
            contentDescription = "Filter",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer),
            modifier = Modifier
                .height(buttonHeight)
                .wrapContentWidth()
        )
    }
}

@Composable
private fun BackspaceButton(
    onClickBackspace: (() -> Unit),
    modifier: Modifier
) {
    IconButton(
        onClick = onClickBackspace,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_backspace_24),
            contentDescription = "Backspace",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer),
            modifier = Modifier
                .height(buttonHeight)
                .wrapContentWidth()
        )
    }
}
