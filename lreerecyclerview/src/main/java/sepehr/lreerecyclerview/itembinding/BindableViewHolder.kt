package sepehr.lreerecyclerview.itembinding

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import sepehr.lreerecyclerview.BR

class BindableViewHolder<T>(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: T) {
        binding.setVariable(BR.item, item)
        binding.executePendingBindings()
    }
}