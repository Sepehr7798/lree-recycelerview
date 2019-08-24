package sepehr.lreerecyclerview.itembinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import sepehr.lreerecyclerview.BindableListAdapter

abstract class BindableListItemAdapter<T> : BindableListAdapter<T, BindableViewHolder<T>> {

    constructor() : super()

    constructor(callback: DiffUtil.ItemCallback<T>) : super(callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder<T> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        return BindableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindableViewHolder<T>, position: Int) {
        holder.bind(getItem(position))
    }

    abstract override fun getItemViewType(position: Int): Int
}