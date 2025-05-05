package matveydidenko.pocketproof

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Camera)
    val screenState: StateFlow<ScreenState> = _screenState

    fun setScreenState(state: ScreenState){
        _screenState.value = state
    }
}