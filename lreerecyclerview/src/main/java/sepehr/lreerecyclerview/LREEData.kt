package sepehr.lreerecyclerview

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import sepehr.lreerecyclerview.collections.MutableLiveList

class LREEData<T>(
    scope: LifecycleCoroutineScope,
    context: CoroutineDispatcher = Dispatchers.Main,
    private val init: suspend MutableLiveList<T>.() -> Boolean
) {

    val state: MutableLiveData<LREEState> = MutableLiveData()

    val data: MutableLiveList<T>

    init {
        data = MutableLiveList(scope, context) {
            reload()
        }
    }

    suspend fun reload() {
        state.value = LREEState.LOADING
        state.value = if (!init(data)) {
            LREEState.ERROR
        } else {
            if (data.isEmpty()) {
                LREEState.EMPTY
            } else {
                LREEState.RESULT
            }
        }
    }
}