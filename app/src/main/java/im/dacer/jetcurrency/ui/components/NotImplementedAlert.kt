package im.dacer.jetcurrency.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun NotImplementedAlert(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        text = {
            Text(text = "This feature is not implemented yet")
        },
        confirmButton = {
            TextButton(
                onClick = { onDismiss.invoke() }
            ) {
                Text("Confirm")
            }
        },
    )
}
