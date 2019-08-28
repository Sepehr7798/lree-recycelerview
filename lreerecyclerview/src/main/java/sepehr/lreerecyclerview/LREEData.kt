package sepehr.lreerecyclerview

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import sepehr.lreerecyclerview.collections.MutableLiveList

class LREEData<T>(
    scope: CoroutineScope,
    context: CoroutineDispatcher = Dispatchers.Main,
    private val init: suspend MutableLiveList<T>.() -> Boolean
) {

    val state: MutableLiveData<LREEState> = MutableLiveData()

    val data: MutableLiveList<T> = MutableLiveList(scope, context) {
        reload(init)
    }

    suspend fun reload(init: suspend MutableLiveList<T>.() -> Boolean = this.init) {
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