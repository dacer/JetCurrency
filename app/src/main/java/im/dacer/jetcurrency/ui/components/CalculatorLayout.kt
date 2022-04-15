package im.dacer.jetcurrency.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import im.dacer.jetcurrency.R
import im.dacer.jetcurrency.utils.WindowSize
import im.dacer.jetcurrency.utils.toHeightWindowSize
import im.dacer.jetcurrency.utils.toWidthWindowSize

private val calculatorRows = listOf(
    listOf('7', '8', '9', '÷'),
    listOf('4', '5', '6', 'x'),
    listOf('1', '2', '3', '-'),
    listOf('0', '.', '=', '+'),
)

@Composable
fun CalculatorLayout(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onClickCalculatorButton: (Char) -> Unit,
    onClickBackspace: () -> Unit,
    onLongClickBackspace: () -> Unit,
    onClickSettings: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickFilterCurrency: () -> Unit,
) = BoxWithConstraints(modifier = modifier) {

    val heightWindowSize = maxHeight.toHeightWindowSize()
    val verticalPadding = when (heightWindowSize) {
        WindowSize.TINY -> 8.dp
        WindowSize.COMPACT -> 18.dp
        WindowSize.MEDIUM -> 120.dp
        WindowSize.EXPANDED -> 120.dp
    }

    val horizontalPadding = when (maxWidth.toWidthWindowSize()) {
        WindowSize.MEDIUM -> 40.dp
        WindowSize.EXPANDED -> 120.dp
        else -> 0.dp
    }

    val fontSize = when (heightWindowSize) {
        WindowSize.TINY -> 12.sp
        WindowSize.COMPACT -> 22.sp
        WindowSize.MEDIUM -> 26.sp
        WindowSize.EXPANDED -> 32.sp
    }

    val iconSize = when (heightWindowSize) {
        WindowSize.TINY -> 12.dp
        WindowSize.COMPACT -> 22.dp
        WindowSize.MEDIUM -> 26.dp
        WindowSize.EXPANDED -> 32.dp
    }

    Column(
        modifier = Modifier
            .padding(vertical = verticalPadding, horizontal = horizontalPadding)
            .align(Alignment.Center),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val btnModifier = Modifier.weight(1f)
            SettingsButton(
                size = iconSize,
                onClickSettings = onClickSettings,
                modifier = btnModifier
            )
            RefreshButton(
                size = iconSize,
                isLoading,
                onClickRefresh = onClickRefresh,
                modifier = btnModifier
            )
            FilterCurrencyButton(
                size = iconSize,
                onClickFilterCurrency = onClickFilterCurrency,
                modifier = btnModifier
            )
            BackspaceButton(
                size = iconSize,
                onClickBackspace = onClickBackspace,
                onLongClickBackspace = onLongClickBackspace,
                modifier = btnModifier
            )
        }

        calculatorRows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                row.forEach { text ->
                    CalculatorButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        text = text,
                        fontSize = fontSize,
                    ) { onClickCalculatorButton.invoke(it) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalculatorButton(
    modifier: Modifier,
    text: Char,
    fontSize: TextUnit = 22.sp,
    onClick: (Char) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        TextButton(
            onClick = { onClick.invoke(text) },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text.toString(),
                style = MaterialTheme.typography.labelLarge.copy(fontSize = fontSize),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun RefreshButton(
    size: Dp,
    isLoading: Boolean,
    onClickRefresh: () -> Unit,
    modifier: Modifier
) {
    CustomIconButton(
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
            .fillMaxHeight()
        if (isLoading) {
            iconModifier = iconModifier.then(Modifier.rotate(rotateDegrees))
        }

        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = "Refresh",
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = iconModifier.size(size)
        )
    }
}

@Composable
private fun SettingsButton(
    size: Dp,
    onClickSettings: () -> Unit,
    modifier: Modifier
) {
    CustomIconButton(
        onClick = onClickSettings,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Settings",
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .fillMaxHeight()
                .size(size)
        )
    }
}

@Composable
private fun FilterCurrencyButton(
    size: Dp,
    onClickFilterCurrency: () -> Unit,
    modifier: Modifier
) {
    CustomIconButton(
        onClick = onClickFilterCurrency,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_filter_list_24),
            contentDescription = "Filter",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer),
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .size(size)
        )
    }
}

@Composable
private fun BackspaceButton(
    size: Dp,
    onClickBackspace: (() -> Unit),
    onLongClickBackspace: () -> Unit,
    modifier: Modifier
) {
    val haptic = LocalHapticFeedback.current
    CustomIconButton(
        onClick = onClickBackspace,
        onLongClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onLongClickBackspace()
        },
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_backspace_24),
            contentDescription = "Backspace",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer),
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .size(size)
        )
    }
}

@Preview(
    "Small main screen (Landscape)",
    device = Devices.AUTOMOTIVE_1024p,
    widthDp = 360,
    heightDp = 140,
    showBackground = true
)
@Preview(showBackground = true)
@Composable
private fun PreviewCalculatorLayout() {
    CalculatorLayout(
        isLoading = false,
        onClickCalculatorButton = {},
        onClickBackspace = {},
        onLongClickBackspace = {},
        onClickSettings = {},
        onClickRefresh = {},
        onClickFilterCurrency = {},
    )
}
