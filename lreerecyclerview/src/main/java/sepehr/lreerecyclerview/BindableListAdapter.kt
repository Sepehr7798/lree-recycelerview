package sepehr.lreerecyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sepehr.lreerecyclerview.collections.ListObserver
import javax.security.auth.callback.Callback

abstract class BindableListAdapter<T, VH : RecyclerView.ViewHolder>(
    callback: DiffUtil.ItemCallback<T> = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = true
    }
) : ListAdapter<T, VH>(callback), ListObserver<T> {

    override fun onChanged(t: List<T>?) {
        t?.let(::submitList)
    }
}