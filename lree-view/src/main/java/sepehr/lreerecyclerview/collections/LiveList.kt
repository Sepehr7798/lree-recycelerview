package sepehr.lreerecyclerview.collections

import androidx.lifecycle.LiveData

open class LiveList<T>(
    protected open val list: List<T> = listOf()
) : LiveData<List<T>>(), List<T> by list {

    override fun getValue(): List<T> = List(list.size) { list[it] }
}