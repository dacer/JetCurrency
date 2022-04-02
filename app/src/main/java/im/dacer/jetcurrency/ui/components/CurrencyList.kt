package im.dacer.jetcurrency.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import im.dacer.jetcurrency.model.Currency
import im.dacer.jetcurrency.ui.main.MainUiState

@Composable
fun CurrencyList(
    mainUiState: MainUiState,
    modifier: Modifier = Modifier,
    shownCurrencyList: List<Currency>,
    footerSpacerHeight: Dp? = null,
    extraEndPadding: Dp = 0.dp,
    addStatusBarPadding: Boolean = true,
    onClickCurrencyItem: (code: String) -> Unit
) {
    when (mainUiState) {
        is MainUiState.HasData -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier)
            ) {
                if (addStatusBarPadding) {
                    item { Spacer(modifier = Modifier.statusBarsHeight()) }
                }
                items(shownCurrencyList) { c ->
                    CurrencyItem(
                        code = c.code,
                        fullName = c.fullName,
                        amount = mainUiState.dataMap[c.code]?.displayValue ?: "0",
                        isFocused = mainUiState.focusedCurrencyCode == c.code,
                        extraEndPadding = extraEndPadding,
                        onClickCurrencyItem = onClickCurrencyItem
                    )
                }
                footerSpacerHeight?.let {
                    item { Spacer(modifier = Modifier.height(it)) }
                }
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
            if (addStatusBarPadding) Spacer(modifier = Modifier.statusBarsHeight())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier)
            ) {
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
    code: String,
    fullName: String,
    amount: String = "",
    isFocused: Boolean = false,
    extraEndPadding: Dp = 0.dp,
    onClickCurrencyItem: (code: String) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickCurrencyItem.invoke(code) }
            .background(if (isFocused) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .padding(start = 22.dp, end = 22.dp + extraEndPadding, top = 16.dp, bottom = 16.dp)
            .navigationBarsPadding(bottom = false, end = false)
    ) {
        val fontColor =
            if (isFocused) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground
        Column {
            Text(
                text = code,
                style = MaterialTheme.typography.displaySmall,
                color = fontColor,
            )
            AnimatedVisibility(visible = isFocused) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.labelMedium,
                    color = fontColor,
                    maxLines = 1,
                )
            }
        }
        Text(
            text = amount,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCurrencyItem() {
    CurrencyItem(
        code = "JPY",
        fullName = "Japanese Yen",
        amount = "0",
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewFocusedCurrencyItem() {
    CurrencyItem(
        code = "JPY",
        fullName = "Japanese Yen",
        amount = "1000",
        isFocused = true,
    )
}
