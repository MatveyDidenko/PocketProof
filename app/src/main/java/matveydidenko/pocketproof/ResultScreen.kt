package matveydidenko.pocketproof

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultScreen(data: OcrResult, onBack: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Receipt Summary", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        data.items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${item.qty} × ${item.name}")
                Text("$${"%.2f".format(item.price)}")
            }
            Spacer(Modifier.height(8.dp))
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subtotal", style = MaterialTheme.typography.titleMedium)
            Text("$${"%.2f".format(data.subtotal)}", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Back to Camera")
        }
    }
}
