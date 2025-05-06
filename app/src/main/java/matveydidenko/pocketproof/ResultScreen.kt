package matveydidenko.pocketproof

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun ResultScreen(
    data: OcrResult,
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(
            horizontal = if (isLandscape) 50.dp else 30.dp,
            vertical = if (isLandscape) 24.dp else 45.dp
        ),
        horizontalArrangement = if (isLandscape) Arrangement.SpaceBetween else Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(end = if (isLandscape) 16.dp else 0.dp)
        ) {
            Text("Receipt Summary", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(data.items) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.qty} × ${item.name}")
                        Text("$${"%.2f".format(item.price)}")
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", fontWeight = FontWeight.SemiBold)
                Text("$${"%.2f".format(data.subtotal)}", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { onSave(data.toJson()) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                ) {
                    Text("Save")
                }

                OutlinedButton(
                    onClick = {/* TODO */ },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                ) {
                    Text("Edit")
                }

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Back")
                }
            }
        }
    }
}

//TODO: move to separate file
private fun OcrResult.toJson(): String {
    return JSONObject().apply {
        put("subtotal", subtotal)
        put("items", JSONArray(items.map {
            JSONObject().apply {
                put("name", it.name)
                put("price", it.price)
                put("qty", it.qty)
            }
        }))
    }.toString()
}
