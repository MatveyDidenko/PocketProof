package matveydidenko.pocketproof


import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap


@Composable
fun EditImageScreen(
    bitmap: Bitmap,
    onSave: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    var rotatedBitmap by remember { mutableStateOf(bitmap) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        verticalArrangement = Arrangement.SpaceBetween) {
        Image(
            bitmap = rotatedBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                val matrix = Matrix().apply { postRotate(90f) }
                rotatedBitmap = Bitmap.createBitmap(
                    rotatedBitmap, 0, 0, rotatedBitmap.width, rotatedBitmap.height, matrix, true
                )
            }) {
                Text("Rotate 90°")
            }

            Button(onClick = {
                //launch cropper
            }) {
                Text("Crop")
            }

            Button(onClick = {
                onSave(rotatedBitmap)
            }) {
                Text("Save")
            }

            Button(onClick = {
                onCancel()
            }) {
                Text("Cancel")
            }
        }
    }
}
