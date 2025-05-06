package matveydidenko.pocketproof

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import matveydidenko.pocketproof.ScreenState.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import matveydidenko.pocketproof.database.AppDatabase
import matveydidenko.pocketproof.database.ReceiptEntity

data class ReceiptItem(val name: String, val price: Double, val qty: String)
data class OcrResult(val items: List<ReceiptItem>, val subtotal: Double? = 0.0)

sealed class ScreenState {
    object Main : ScreenState()
    object Camera : ScreenState()
    data class Edit(val bitmap: Bitmap) : ScreenState()
    object Loading : ScreenState()
    data class Result(val data: OcrResult) : ScreenState()
}

suspend fun uploadImageToServer(file: File): String {
    val client = OkHttpClient()
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("image", file.name,
            file.asRequestBody("image/jpeg".toMediaType()))
        .build()

    val request = Request.Builder()
        .url("http://192.168.5.87:5050/ocr") //my PC address
        .post(requestBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        response.body?.string() ?: "No response body"
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

fun parseOcrResult(json: String): OcrResult {
    val jsonObj = JSONObject(json)
    val itemsJson = jsonObj.getJSONArray("items")
    val items = mutableListOf<ReceiptItem>()
    for (i in 0 until itemsJson.length()) {
        val item = itemsJson.getJSONObject(i)
        items.add(
            ReceiptItem(
                name = item.getString("name"),
                price = item.getDouble("price"),
                qty = item.getString("qty")
            )
        )
    }
    val subtotal = if (jsonObj.has("subtotal") && !jsonObj.isNull("subtotal")) {
        jsonObj.getDouble("subtotal")
    } else {
        items.sumOf { it.price }
    }

    return OcrResult(items = items, subtotal = subtotal)
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(this)
        val dao = db.receiptDao()

        enableEdgeToEdge()

        setContent {
            val screenState by viewModel.screenState.collectAsStateWithLifecycle()
            val receipts by viewModel.savedReceipts.collectAsStateWithLifecycle(initialValue = emptyList())

            PocketProofTheme {
                when (val state = screenState) {
                    Main -> {
                        MainScreen(
                            receipts = receipts,
                            onScanClick = {
                                viewModel.setScreenState(Camera)
                            },
                            onDeleteReceipt = { viewModel.deleteReceipt(it) }
                        )
                    }
                    Camera -> {
                        CameraScreen(
                            onImageCaptured = { bitmap ->
                                viewModel.setScreenState(Edit(bitmap))
                            }
                        )
                    }
                    is Edit -> {
                        EditImageScreen(
                            bitmap = state.bitmap,
                            onSave = { finalBitmap ->
                                val filename = "edited_img.jpg"
                                val file = File(cacheDir, filename)
                                FileOutputStream(file).use { out ->
                                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                                }

                                viewModel.setScreenState(Loading)

                                //OCR network request
                                CoroutineScope(Dispatchers.IO).launch {
                                    val json = uploadImageToServer(file)
                                    Log.e("MainActivityLOG", "json: ${json}")
                                    val parsedResult = parseOcrResult(json)
                                    Log.e("MainActivityLOG", "parsed: ${parsedResult}")
                                    withContext(Dispatchers.Main) {
                                        viewModel.setScreenState(Result(parsedResult))
                                    }
                                }
                            },
                            onCancel = {
                                viewModel.setScreenState(Camera)
                            }
                        )
                    }
                    Loading -> LoadingScreen()
                    is Result -> ResultScreen(
                        data = state.data,
                        onBack = { viewModel.setScreenState(Camera) },
                        onSave = { rawJson ->
                            CoroutineScope(Dispatchers.IO).launch {
                                dao.insertReceipt(ReceiptEntity(json = rawJson, timestamp = System.currentTimeMillis()))
                            }

                            viewModel.setScreenState(Main)
                        }
                    )
                }
            }
        }
    }
}
