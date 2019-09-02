package sepehr.lreerecyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.*
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

class LREERecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BindableRecyclerView(context, attrs, defStyleAttr) {

    private val inflater = LayoutInflater.from(context)
    var loadingView: View = inflater.inflate(R.layout.lree_layout_loading, this, false)
    var emptyView: View = inflater.inflate(R.layout.lree_layout_empty_error, this, false)
    var errorView: View = inflater.inflate(R.layout.lree_layout_empty_error, this, false)
        set(value) {
            field = value
            retryButton = value.findViewById(R.id.lree_retry_button)
        }
    var retryButton: Button = errorView.findViewById(R.id.lree_retry_button)
        private set

    private var dataAdapter: Adapter<*>? = null
    private var internalSetAdapterCall: Boolean = false
    private var dataLayoutManager: LayoutManager? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LREEView,
            0, R.style.DefaultStyle
        ).apply {
            try {
                setVisibleOrGone(
                    errorView,
                    getBooleanOrThrow(R.styleable.LREEView_lree_show_error)
                )
                setVisibleOrGone(
                    emptyView,
                    getBooleanOrThrow(R.styleable.LREEView_lree_show_empty)
                )

                loadingView.findViewById<TextView>(R.id.lree_loading_text).run {
                    text = getStringOrThrow(R.styleable.LREEView_lree_loading_text)
                    setVisibleOrGone(
                        this,
                        getBooleanOrThrow(R.styleable.LREEView_lree_loading_text_visible)
                    )
                }
                emptyView.findViewById<TextView>(R.id.lree_empty_error_text).run {
                    text = getStringOrThrow(R.styleable.LREEView_lree_empty_text)
                    setVisibleOrGone(
                        this,
                        getBooleanOrThrow(R.styleable.LREEView_lree_empty_text_visible)
                    )
                }
                errorView.findViewById<TextView>(R.id.lree_empty_error_text).run {
                    text = getStringOrThrow(R.styleable.LREEView_lree_error_text)
                    setVisibleOrGone(
                        this,
                        getBooleanOrThrow(R.styleable.LREEView_lree_error_text_visible)
                    )
                }

                emptyView.findViewById<ImageView>(R.id.lree_empty_error_icon)
                    .setImageDrawable(getDrawableOrThrow(R.styleable.LREEView_lree_empty_icon).apply {
                        val c = getColor(R.styleable.LREEView_lree_empty_tint, 0)
                        if (c > 0) {
                            DrawableCompat.setTint(this, c)
                        }
                    })
                errorView.findViewById<ImageView>(R.id.lree_empty_error_icon)
                    .setImageDrawable(getDrawableOrThrow(R.styleable.LREEView_lree_error_icon).apply {
                        val c = getColor(R.styleable.LREEView_lree_error_tint, 0)
                        if (c > 0) {
                            DrawableCompat.setTint(this, c)
                        }
                    })

                setVisibleOrGone(
                    retryButton,
                    getBooleanOrThrow(R.styleable.LREEView_lree_retry_enabled)
                )
                retryButton.text = getStringOrThrow(R.styleable.LREEView_lree_retry_text)
            } finally {
                recycle()
            }
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)

        if (!internalSetAdapterCall) {
            dataAdapter = adapter
        }
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)

        if (!internalSetAdapterCall) {
            dataLayoutManager = layout
        }
    }

    var lreeData: LREEData<*>? = null
        set(value) {
            field = value
            data = value?.data

            value?.data?.observe(context as LifecycleOwner, Observer {
                when {
                    it == null -> lreeData?.state?.value = LREEState.ERROR
                    it.isEmpty() -> lreeData?.state?.value = LREEState.EMPTY
                    else -> lreeData?.state?.value = LREEState.RESULT
                }
            })

            value?.state?.observe(context as LifecycleOwner, Observer {
                removeAllViews()
                when (it) {
                    LREEState.LOADING -> {
                        setStateAdapter(loadingView)
                    }
                    LREEState.RESULT -> {
                        setDataAdapter()
                    }
                    LREEState.EMPTY -> {
                        setStateAdapter(emptyView)
                    }
                    LREEState.ERROR -> {
                        setStateAdapter(errorView)
                    }
                    else -> {
                        throw IllegalArgumentException("illegal lree state: $it")
                    }
                }
            })
        }


    private fun setStateAdapter(view: View) {
        internalSetAdapterCall = true
        adapter = StateAdapter(view)
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        internalSetAdapterCall = false
    }

    private fun setDataAdapter() {
        adapter = dataAdapter
        layoutManager = dataLayoutManager
    }

    private fun setVisibleOrGone(view: View, visible: Boolean) {
        view.visibility =
            if (visible)
                View.VISIBLE
            else
                View.GONE
    }

    private class StateAdapter(private val view: View) : Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            object : ViewHolder(view) {}

        override fun getItemCount(): Int = 1

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = Unit
    }
}
