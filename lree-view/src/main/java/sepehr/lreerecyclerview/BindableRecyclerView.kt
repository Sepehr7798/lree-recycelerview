package sepehr.lreerecyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import sepehr.lreerecyclerview.collections.ListObserver
import sepehr.lreerecyclerview.collections.LiveList

open class BindableRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    open var data: LiveList<*>? = null
        set(value) {
            field = value
            bind()
        }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        bind()
    }

    private fun bind() {
        if (data != null && adapter != null && adapter is Observer<*>) {
            data?.observe(context as LifecycleOwner, adapter as Observer<in List<Any?>>)
        }
    }
}