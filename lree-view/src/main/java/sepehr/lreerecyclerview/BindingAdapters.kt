package sepehr.lreerecyclerview

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("app:adapter")
    fun bindAdapter(recyclerView: LREERecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.recyclerView.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("app:data")
    fun bindData(recyclerView: LREERecyclerView, data: LREEData<*>) {
        recyclerView.data = data
    }
}