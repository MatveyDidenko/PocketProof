package matveydidenko.pocketproof

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pocketproof.ui.theme.PocketProofTheme
import java.io.File
import java.io.FileOutputStream

sealed class ScreenState {
    object Camera : ScreenState()
    data class Edit(val bitmap: Bitmap) : ScreenState()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            var screenState by remember { mutableStateOf<ScreenState>(ScreenState.Camera) }

            PocketProofTheme {
                when (val state = screenState) {
                    is ScreenState.Camera -> {
                        CameraScreen(
                            onImageCaptured = { bitmap ->
                                screenState = ScreenState.Edit(bitmap)
                            }
                        )
                    }
                    is ScreenState.Edit -> {
                        EditImageScreen(
                            bitmap = state.bitmap,
                            onSave = { finalBitmap ->
                                val filename = "edited_img.jpg"
                                val file = File(cacheDir, filename)
                                FileOutputStream(file).use { out ->
                                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                                }
                                screenState = ScreenState.Camera
                            },
                            onCancel = {
                                screenState = ScreenState.Camera
                            }
                        )
                    }
                }
            }
        }
    }
}
