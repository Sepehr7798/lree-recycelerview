package sepehr.lreerecyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.*
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Error

class LREERecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private val inflater = LayoutInflater.from(context)

    val recyclerView: BindableRecyclerView = BindableRecyclerView(context, attrs, defStyleAttr)

    var loadingView: View = inflater.inflate(R.layout.layout_loading, this, false)

    var errorView: View = inflater.inflate(R.layout.layout_empty_error, this, false)
        set(value) {
            field = value
            retryButton = errorView.findViewById(R.id.lree_retry_button)
        }

    var emptyView: View = inflater.inflate(R.layout.layout_empty_error, this, false)

    var retryButton: Button = errorView.findViewById(R.id.lree_retry_button)

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LREERecyclerView,
            0, R.style.DefaultStyle
        ).apply {
            try {
                loadingView.findViewById<TextView>(R.id.lree_loading_text).run {
                    text = getStringOrThrow(R.styleable.LREERecyclerView_lree_loading_text)
                    visibility =
                        if (getBooleanOrThrow(R.styleable.LREERecyclerView_lree_loading_text_visible)) View.VISIBLE
                        else View.GONE
                }
                errorView.findViewById<TextView>(R.id.lree_empty_error_text).run {
                    text = getStringOrThrow(R.styleable.LREERecyclerView_lree_error_text)
                    visibility =
                        if (getBooleanOrThrow(R.styleable.LREERecyclerView_lree_error_text_visible)) View.VISIBLE
                        else View.GONE
                }
                emptyView.findViewById<TextView>(R.id.lree_empty_error_text).run {
                    text = getStringOrThrow(R.styleable.LREERecyclerView_lree_empty_text)
                    visibility =
                        if (getBooleanOrThrow(R.styleable.LREERecyclerView_lree_empty_text_visible)) View.VISIBLE
                        else View.GONE
                }

                errorView.findViewById<ImageView>(R.id.lree_empty_error_icon)
                    .setImageDrawable(getDrawableOrThrow(R.styleable.LREERecyclerView_lree_error_icon).apply {
                        val c = getColor(R.styleable.LREERecyclerView_lree_error_tint, 0)
                        if (c > 0) {
                            DrawableCompat.setTint(this, c)
                        }
                    })
                emptyView.findViewById<ImageView>(R.id.lree_empty_error_icon)
                    .setImageDrawable(getDrawableOrThrow(R.styleable.LREERecyclerView_lree_empty_icon).apply {
                        val c = getColor(R.styleable.LREERecyclerView_lree_empty_tint, 0)
                        if (c > 0) {
                            DrawableCompat.setTint(this, c)
                        }
                    })

                retryButton.visibility =
                    if (getBooleanOrThrow(R.styleable.LREERecyclerView_lree_retry_enabled)) View.VISIBLE
                    else View.GONE
                retryButton.text = getStringOrThrow(R.styleable.LREERecyclerView_lree_retry_text)

                when (getIntegerOrThrow(R.styleable.LREERecyclerView_lree_layoutManager)) {
                    0 -> {
                        recyclerView.layoutManager = LinearLayoutManager(context)
                    }
                    1 -> {
                        val c =
                            getIntegerOrThrow(R.styleable.LREERecyclerView_lree_grid_columns)
                        recyclerView.layoutManager = GridLayoutManager(context, c)
                    }
                }
            } finally {
                recycle()
            }
        }

        removeAllViews()
        addView(errorView)
    }

    fun setOnRetryClickListener(listener: OnClickListener) {
        retryButton.setOnClickListener(listener)
    }

    fun setOnRetryClickListener(onClick: (View) -> Unit) {
        retryButton.setOnClickListener(onClick)
    }

    var data: LREEData<*>? = null
        set(value) {
            field = value
            recyclerView.data = value?.data

            value?.data?.observe(context as LifecycleOwner, Observer {
                when {
                    it == null -> data?.state?.value = LREEState.ERROR
                    it.isEmpty() -> data?.state?.value = LREEState.EMPTY
                    else -> data?.state?.value = LREEState.RESULT
                }
            })

            value?.state?.observe(context as LifecycleOwner, Observer {
                removeAllViews()
                when (it) {
                    LREEState.LOADING -> {
                        addView(loadingView)
                    }
                    LREEState.RESULT -> {
                        addView(recyclerView)
                    }
                    LREEState.ERROR -> {
                        addView(errorView)
                    }
                    LREEState.EMPTY -> {
                        addView(emptyView)
                    }
                    else -> {
                        throw IllegalArgumentException("illegal lree state: $it")
                    }
                }
            })
        }
}