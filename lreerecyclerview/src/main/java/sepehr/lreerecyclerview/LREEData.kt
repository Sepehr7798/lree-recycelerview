package sepehr.lreerecyclerview

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import sepehr.lreerecyclerview.collections.MutableLiveList

class LREEData<T> {

    private val defaultInit: suspend MutableLiveList<T>.() -> Boolean

    val state: MutableLiveData<LREEState> = MutableLiveData()

    val data: MutableLiveList<T>

    constructor(data: MutableLiveList<T> = MutableLiveList()) {
        defaultInit = { true }
        this.data = data
    }

    constructor(
        scope: CoroutineScope,
        context: CoroutineDispatcher = Dispatchers.Main,
        init: suspend MutableLiveList<T>.() -> Boolean
    ) {
        defaultInit = init
        data = MutableLiveList(scope, context) {
            reload(init)
        }
    }

    suspend fun reload(init: suspend MutableLiveList<T>.() -> Boolean = defaultInit) {
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