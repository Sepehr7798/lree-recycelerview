package sepehr.lreerecyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
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

class LREERecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private val inflater = LayoutInflater.from(context)

    val recyclerView: BindableRecyclerView = BindableRecyclerView(context, attrs, defStyleAttr)

    var loadingView: View = inflater.inflate(R.layout.layout_loading, this, false)

    var errorView: View = inflater.inflate(R.layout.layout_empty_error, this, false)
        set(value) {
            field = value
            retryButton = value.findViewById(R.id.lree_retry_button)
        }

    var emptyView: View = inflater.inflate(R.layout.layout_empty_error, this, false)

    var retryButton: Button = errorView.findViewById(R.id.lree_retry_button)
        private set

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LREERecyclerView,
            0, R.style.DefaultStyle
        ).apply {
            try {
                setVisibleOrGone(
                    errorView,
                    getBooleanOrThrow(R.styleable.LREERecyclerView_show_error)
                )
                setVisibleOrGone(
                    emptyView,
                    getBooleanOrThrow(R.styleable.LREERecyclerView_show_empty)
                )

                loadingView.findViewById<TextView>(R.id.lree_loading_text).run {
                    text = getStringOrThrow(R.styleable.LREERecyclerView_lree_loading_text)
                    setVisibleOrGone(
                        this,
                        getBooleanOrThrow(R.styleable.LREERecyclerView_lree_loading_text_visible)
                    )
                }
                errorView.findViewById<TextView>(R.id.lree_empty_error_text).run {
                    text = getStringOrThrow(R.styleable.LREERecyclerView_lree_error_text)
                    setVisibleOrGone(
                        this,
                        getBooleanOrThrow(R.styleable.LREERecyclerView_lree_error_text_visible)
                    )
                }
                emptyView.findViewById<TextView>(R.id.lree_empty_error_text).run {
                    text = getStringOrThrow(R.styleable.LREERecyclerView_lree_empty_text)
                    setVisibleOrGone(
                        this,
                        getBooleanOrThrow(R.styleable.LREERecyclerView_lree_empty_text_visible)
                    )
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

                setVisibleOrGone(
                    retryButton,
                    getBooleanOrThrow(R.styleable.LREERecyclerView_lree_retry_enabled)
                )
                retryButton.text = getStringOrThrow(R.styleable.LREERecyclerView_lree_retry_text)

                val reverseLayout =
                    getBooleanOrThrow(R.styleable.LREERecyclerView_lree_layout_manager_reverse)
                val orientation =
                    when (getIntegerOrThrow(R.styleable.LREERecyclerView_lree_layout_manager_orientation)) {
                        0 -> RecyclerView.VERTICAL
                        1 -> RecyclerView.HORIZONTAL
                        else -> throw IllegalArgumentException("Illegal orientation")
                    }
                when (getIntegerOrThrow(R.styleable.LREERecyclerView_lree_layout_manager)) {
                    0 -> {
                        recyclerView.layoutManager = LinearLayoutManager(
                            context, orientation, reverseLayout
                        )
                    }
                    1 -> {
                        val c =
                            getIntegerOrThrow(R.styleable.LREERecyclerView_lree_layout_manager_grid_columns)
                        recyclerView.layoutManager =
                            GridLayoutManager(context, c, orientation, reverseLayout)
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


    private fun setVisibleOrGone(view: View, visible: Boolean) {
        view.visibility =
            if (visible)
                View.VISIBLE
            else
                View.GONE
    }
}
