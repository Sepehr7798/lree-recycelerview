package sepehr.lreerecyclerview.collections

import android.os.Looper

open class MutableLiveList<T>(override val list: MutableList<T> = mutableListOf()) :
    LiveList<T>(list), MutableList<T> {

    public override fun postValue(value: List<T>?) {
        super.postValue(value)
    }

    public override fun setValue(value: List<T>?) {
        super.setValue(value)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        super.subList(fromIndex, toIndex).toMutableList()

    override fun iterator(): MutableIterator<T> = object : MutableIterator<T> by list.iterator() {
        override fun remove() {
            list.iterator().remove()
            notifyData()
        }
    }

    override fun add(element: T): Boolean = list.add(element).also { notifyData() }

    override fun add(index: Int, element: T) = list.add(index, element).also { notifyData() }

    override fun addAll(index: Int, elements: Collection<T>): Boolean =
        list.addAll(index, elements).also { notifyData() }

    override fun addAll(elements: Collection<T>): Boolean =
        list.addAll(elements).also { notifyData() }

    override fun clear() = list.clear().also { notifyData() }

    override fun listIterator(): MutableListIterator<T> = listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<T> =
        object : MutableListIterator<T> by list.listIterator(index) {
            override fun add(element: T) =
                list.listIterator(index).add(element).also { notifyData() }

            override fun remove() = list.listIterator(index).remove().also { notifyData() }
            override fun set(element: T) =
                list.listIterator(index).set(element).also { notifyData() }
        }

    override fun remove(element: T): Boolean = list.remove(element).also { notifyData() }

    override fun removeAll(elements: Collection<T>): Boolean =
        list.removeAll(elements).also { notifyData() }

    override fun removeAt(index: Int): T = list.removeAt(index).also { notifyData() }

    override fun retainAll(elements: Collection<T>): Boolean =
        list.retainAll(elements).also { notifyData() }

    override fun set(index: Int, element: T): T = list.set(index, element).also { notifyData() }


    private fun notifyData() {
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            value = value
        } else {
            postValue(value)
        }
    }
}