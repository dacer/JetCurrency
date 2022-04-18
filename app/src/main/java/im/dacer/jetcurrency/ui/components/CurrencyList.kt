package im.dacer.jetcurrency.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import im.dacer.jetcurrency.model.Currency
import im.dacer.jetcurrency.ui.main.MainUiState
import im.dacer.jetcurrency.ui.theme.RobotoFontFamily
import im.dacer.jetcurrency.ui.theme.drawableResource
import im.dacer.jetcurrency.utils.WindowSize
import im.dacer.jetcurrency.utils.toWidthWindowSize
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.draggedItem
import org.burnoutcrew.reorderable.rememberReorderState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun CurrencyList(
    mainUiState: MainUiState,
    modifier: Modifier = Modifier,
    shownCurrencyList: List<Currency>,
    footerSpacerHeight: Dp? = null,
    extraEndPadding: Dp = 0.dp,
    onClickCurrencyItem: (code: String) -> Unit,
    onListReordered: (from: Int, to: Int) -> Unit,
) = BoxWithConstraints(modifier = modifier) {
    val state = rememberReorderState()
    when (mainUiState) {
        is MainUiState.HasData -> {
            LazyColumn(
                state = state.listState,
                modifier = Modifier
                    .fillMaxSize()
                    .reorderable(
                        state = state,
                        onMove = { from, to -> onListReordered(from.index - 1, to.index - 1) },
                        // statusBar spacer cannot be reordered
                        canDragOver = { it.index > 0 && it.index <= shownCurrencyList.size }
                    )
            ) {
                item { Spacer(modifier = Modifier.statusBarsHeight()) }
                items(shownCurrencyList, { it.code }) { c ->
                    CurrencyItem(
                        modifier = Modifier
                            .draggedItem(state.offsetByKey(c.code))
                            .detectReorderAfterLongPress(state),
                        currency = c,
                        amount = mainUiState.dataMap[c.code]?.displayValue ?: "0",
                        isFocused = mainUiState.focusedCurrencyCode == c.code,
                        widthWindowSize = maxWidth.toWidthWindowSize(),
                        extraEndPadding = extraEndPadding,
                        onClickCurrencyItem = onClickCurrencyItem
                    )
                }
                item { Spacer(modifier = Modifier.height(footerSpacerHeight ?: 0.dp)) }
            }
        }
        is MainUiState.NoData -> {
            val infiniteTransition = rememberInfiniteTransition()
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 0.7f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 800
                        0.6f at 500
                    },
                    repeatMode = RepeatMode.Reverse
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier)
            ) {
                Spacer(modifier = Modifier.statusBarsHeight())
                FakeCurrencyItem(alpha)
                FakeCurrencyItem(alpha)
            }
        }
    }
}

/**
 * For loading animation.
 */
@Preview
@Composable
private fun FakeCurrencyItem(alpha: Float = 0.5f) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 16.dp)
            .alpha(alpha = alpha)
            .background(MaterialTheme.colorScheme.inverseSurface)
    )
}

@Composable
private fun CurrencyItem(
    modifier: Modifier = Modifier,
    currency: Currency,
    amount: String = "",
    isFocused: Boolean = false,
    widthWindowSize: WindowSize = WindowSize.EXPANDED,
    extraEndPadding: Dp = 0.dp,
    onClickCurrencyItem: (code: String) -> Unit = {},
) {
    val mainFontSize = when (widthWindowSize) {
        WindowSize.TINY -> 16.sp
        WindowSize.COMPACT -> 24.sp
        WindowSize.MEDIUM -> 30.sp
        WindowSize.EXPANDED -> 36.sp
    }

    val subFontSize = when (widthWindowSize) {
        WindowSize.TINY -> 12.sp
        WindowSize.COMPACT -> 12.sp
        WindowSize.MEDIUM -> 14.sp
        WindowSize.EXPANDED -> 16.sp
    }

    val imageSize = when (widthWindowSize) {
        WindowSize.TINY -> 0.dp
        WindowSize.COMPACT -> 22.dp
        WindowSize.MEDIUM -> 24.dp
        WindowSize.EXPANDED -> 26.dp
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickCurrencyItem.invoke(currency.code) }
            .background(if (isFocused) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
            .padding(start = 22.dp, end = 22.dp + extraEndPadding, top = 16.dp, bottom = 16.dp)
            .navigationBarsPadding(bottom = false, end = false)
    ) {
        val fontColor =
            if (isFocused) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (widthWindowSize != WindowSize.TINY) {
                Image(
                    painter = painterResource(id = drawableResource(name = currency.flagDrawableName)),
                    contentDescription = "flag",
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
            }
            Column {
                Text(
                    text = currency.code,
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = mainFontSize),
                    color = fontColor,
                    modifier = Modifier.padding(end = 6.dp)
                )
                if (widthWindowSize != WindowSize.TINY) {
                    AnimatedVisibility(visible = isFocused) {
                        Text(
                            text = currency.fullName,
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = subFontSize),
                            color = fontColor,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        Text(
            text = amount,
            style = MaterialTheme.typography.displaySmall.copy(
                fontFamily = RobotoFontFamily,
                fontSize = mainFontSize,
            ),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCurrencyItem() {
    CurrencyItem(
        currency = Currency(
            code = "JPY",
            fullName = "Japanese Yen",
            exchangeRateFromUsd = 0.0
        ),
        amount = "0",
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewFocusedCurrencyItem() {
    CurrencyItem(
        currency = Currency(
            code = "JPY",
            fullName = "Japanese Yen",
            exchangeRateFromUsd = 0.0
        ),
        amount = "1024323",
        isFocused = true,
    )
}

@Preview(
    "Small",
    device = Devices.AUTOMOTIVE_1024p,
    widthDp = 224,
    heightDp = 120
)
@Composable
private fun PreviewFocusedSmallCurrencyItem() {
    CurrencyItem(
        currency = Currency(
            code = "JPY",
            fullName = "Japanese Yen",
            exchangeRateFromUsd = 0.0
        ),
        amount = "1024323",
        widthWindowSize = WindowSize.TINY,
        isFocused = true,
    )
}
