package matveydidenko.pocketproof

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import matveydidenko.pocketproof.database.ReceiptEntity

@Composable
fun MainScreen(
    receipts: List<ReceiptEntity>,
    onScanClick: () -> Unit,
    onDeleteReceipt: (ReceiptEntity) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val expandedIds = remember { mutableStateListOf<Int>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = if (isLandscape) 50.dp else 30.dp,
                vertical = if (isLandscape) 24.dp else 45.dp
            )
    ) {
        Column(modifier = Modifier.weight(1f, fill = true)) {
            Text(
                text = "PocketProof",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            )
            Text(
                text = "Your receipts, organized.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )

            Spacer(Modifier.height(32.dp))

            Text("Saved Receipts", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = true)
            ) {
                items(receipts, key = { it.id }) { receipt ->
                    Column {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (expandedIds.contains(receipt.id)) {
                                        expandedIds.remove(receipt.id)
                                    } else {
                                        expandedIds.add(receipt.id)
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text("Receipt ID: ${receipt.id}")
                                Text("Contents: ${receipt.json}")
                                if (expandedIds.contains(receipt.id)) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        OutlinedButton(onClick = { onDeleteReceipt(receipt) }) {
                                            Text("Delete")
                                        }
                                        OutlinedButton(onClick = { /* TODO */ }) {
                                            Text("Edit")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onScanClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Scan New Receipt")
        }
    }
}
