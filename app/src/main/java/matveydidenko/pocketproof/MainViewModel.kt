package matveydidenko.pocketproof

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import matveydidenko.pocketproof.database.AppDatabase
import matveydidenko.pocketproof.database.ReceiptEntity

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).receiptDao()
    val savedReceipts = dao.getAllReceiptsFlow()

    fun deleteReceipt(receipt: ReceiptEntity) {
        viewModelScope.launch {
            dao.delete(receipt)
        }
    }

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Main)
    val screenState: StateFlow<ScreenState> = _screenState

    fun setScreenState(state: ScreenState){
        _screenState.value = state
    }
}