package y2k.joyreactor.common

import android.app.Activity
import android.support.v4.view.ViewPager
import android.support.v4.widget.ContentLoadingProgressBar
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText

/**
 * Created by y2k on 2/23/16.
 */
fun View.command(command: () -> Unit) = setOnClickListener { command() }

fun View.command(id: Int, command: () -> Unit): View {
    findViewById(id).setOnClickListener { command() }
    return this
}

fun bindingBuilder(root: View, init: BindingBuilder.() -> Unit): View {
    BindingBuilder(ViewGroupResolver(root)).init()
    return root
}

fun bindingBuilder(root: ViewResolver, init: BindingBuilder.() -> Unit) {
    BindingBuilder(root).init()
}

fun bindingBuilder(root: Activity, init: BindingBuilder.() -> Unit) {
    BindingBuilder(ActivityViewResolver(root)).init()
}

private class ViewGroupResolver(private val view: View) : ViewResolver {

    override fun <T : View> find(id: Int): T {
        return view.find<T>(id)
    }
}

private class ActivityViewResolver(private val activity: Activity) : ViewResolver {

    override fun <T : View> find(id: Int): T {
        return activity.find<T>(id)
    }
}

class BindingBuilder(private val root: ViewResolver) {

    fun view(id: Int, init: ViewBinding.() -> Unit) {
        ViewBinding(root.find<View>(id)).init()
    }

    fun click(id: Int, f: () -> Unit) {
        root.find<View>(id).setOnClickListener { f() }
    }

    fun <T> action(binding: Binding<T>, f: (T) -> Unit) {
        binding.subscribe(f)
    }

    fun <T> recyclerView(id: Int, binding: Binding<List<T>>, init: DslRecyclerView<T>.() -> Unit) {
        val view = root.find<RecyclerView>(id)
        val dsl = DslRecyclerView<T>()
        dsl.init()
        view.adapter = dsl.build().apply { binding.subscribe { update(it) } }
    }

    fun loadingProgressBar(id: Int, binding: Binding<Boolean>) {
        val view = root.find<ContentLoadingProgressBar>(id)
        binding.subscribe { if (it) view.show() else view.hide() }
    }

    fun editText(id: Int, binding: Binding<String>) {
        val view = root.find<EditText>(id)
        view.addTextChangedListener(object : TextWatcherAdapter() {

            override fun afterTextChanged(s: Editable?) {
                binding.value = "" + s
            }
        })
    }

    fun visibility(id: Int, binding: Binding<Boolean>, invert: Boolean = false) {
        val view = root.find<View>(id)
        binding.subscribe {
            if (invert) view.visibility = if (it) View.GONE else View.VISIBLE
            else view.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    fun webView(id: Int, init: WebViewBinding.() -> Unit) {
        WebViewBinding(root.find<WebView>(id)).init()
    }

    fun viewPager(view: ViewPager, binding: Binding<Int>) {
        view.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                binding.value = position
            }
        })
    }
}

class DslRecyclerView<T> {

    private lateinit var createVH: (ViewGroup, Int) -> ListViewHolder<T>
    private var getItemId: ((T) -> Long)? = null
    private var viewTypeFactory: (ItemViewTypeProperties<T>) -> Int = { 0 }

    fun itemViewType(f: (ItemViewTypeProperties<T>) -> Int) {
        viewTypeFactory = f
    }

    fun itemId(getItemId: (T) -> Long) {
        this.getItemId = getItemId
    }

    fun viewHolderWithType(createVH: (ViewGroup, Int) -> ListViewHolder<T>) {
        this.createVH = createVH
    }

    fun viewHolder(createVH: (ViewGroup) -> ListViewHolder<T>) {
        this.createVH = { v, i -> createVH(v) }
    }

    fun build(): ListAdapter<T, ListViewHolder<T>> {
        return object : ListAdapter<T, ListViewHolder<T>>() {

            init {
                setHasStableIds(getItemId != null)
            }

            override fun getItemViewType(position: Int): Int {
                return viewTypeFactory(ItemViewTypeProperties(items[position], items, position))
            }

            override fun getItemId(position: Int): Long {
                return getItemId?.invoke(items[position]) ?: 0L
            }

            override fun onBindViewHolder(holder: ListViewHolder<T>, position: Int) {
                holder.update(items[position])
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder<T>? {
                return createVH(parent, viewType)
            }
        }
    }

    class ItemViewTypeProperties<T>(val value: T, val items: List<T>, val position: Int)
}

abstract class ListViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun update(item: T)
}

class ViewBinding(private val view: View) {

    fun click(f: () -> Unit) {
        view.setOnClickListener { f() }
    }

    fun visibility(binding: Binding<Boolean>) {
        binding.subscribe { view.visibility = if (it) View.VISIBLE else View.GONE }
    }
}

class WebViewBinding(private val webView: WebView) {

    val settings: WebSettings
        get() = webView.settings

    fun url(binding: Binding<String>) {
        binding.subscribe { webView.loadUrl(it) }
    }

    fun title(binding: Binding<String>) {
        webView.setWebViewClient(object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String?) {
                binding.value = view.title
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                view.loadUrl(url)
                return true;
            }
        })
    }
}

interface ViewResolver {
    fun <T : View> find(id: Int): T
}