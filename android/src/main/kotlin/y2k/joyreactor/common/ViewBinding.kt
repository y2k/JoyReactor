package y2k.joyreactor.common

import android.app.Activity
import android.content.Context
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.ContentLoadingProgressBar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import y2k.joyreactor.App
import y2k.joyreactor.FixedAspectPanel
import y2k.joyreactor.ImagePanel
import y2k.joyreactor.WebImageView
import y2k.joyreactor.model.Image
import y2k.joyreactor.widget.MuteVideoView
import y2k.joyreactor.widget.ProgressImageView
import y2k.joyreactor.widget.TagsView
import java.io.File

/**
 * Created by y2k on 2/23/16.
 */
fun View.command(command: () -> Unit) = setOnClickListener { command() }

fun View.command(id: Int, command: () -> Unit): View {
    findViewById(id).setOnClickListener { command() }
    return this
}

fun bindingBuilder(root: View, init: BindingBuilder.() -> Unit): View {
    BindingBuilder(ViewGroupResolver(root), root.context).init()
    return root
}

fun bindingBuilder(root: ViewResolver, init: BindingBuilder.() -> Unit) {
    BindingBuilder(root).init()
}

fun bindingBuilder(root: Activity, init: BindingBuilder.() -> Unit) {
    BindingBuilder(ActivityViewResolver(root)).init()
}

private class ViewGroupResolver(private val view: View) : ViewResolver {

    override fun <T> find(id: Int): T? {
        return view.findOrNull<T>(id)
    }
}

private class ActivityViewResolver(val activity: Activity) : ViewResolver {

    override fun <T> find(id: Int): T? {
        return activity.findOrNull<T>(id)
    }
}

class BindingBuilder(root: ViewResolver, val context: Context = App.instance) {

    val resolvers = arrayListOf(root)

    fun menu(menuId: Int, init: MenuBinding.() -> Unit) {
        MenuBinding(menuId, resolvers).init()
    }

    //    fun refreshLayout(id: Int, binding: Binding<Boolean>) {
    //        val view = root.find<SwipeRefreshLayout>(id)
    //        binding.subscribe { view.isRefreshing = it }
    //    }

    fun spinner(id: Int, binding: Binding<Int>) {
        val view = find<Spinner>(id)
        binding.subscribe { view.setSelection(it) }
        view.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.value = position
            }
        }
    }

    fun tabLayout(id: Int, binding: Binding<Int>) {
        val view = find<TabLayout>(id)
        view.setOnTabSelectedListener(object : OnTabSelectedListenerAdapter() {

            override fun onTabReselected(tab: TabLayout.Tab) {
                binding.value = tab.position
            }
        })
        binding.subscribe { view.getTabAt(it)?.select() }
    }

    fun radioGroup(id: Int, binding: Binding<Int>) {
        val view = find<RadioGroup>(id)
        binding.subscribe { (view.getChildAt(it) as RadioButton).isChecked = true }
        view.setOnCheckedChangeListener { group, id ->
            view.getChildren().map { it as RadioButton }.indexOfFirst { it.isChecked }
        }
    }

    fun <T> animator(id: Int, binding: Binding<T>, convert: (T) -> Int) {
        val view = find<ViewAnimator>(id)
        binding.subscribe { view.displayedChild = convert(it) }
    }

    fun progressBar(id: Int, binding: Binding<Float>) {
        val view = find<ProgressBar>(id)
        binding.subscribe { view.progress = it.toInt() }
    }

    fun ratingBar(id: Int, binding: Binding<Float>) {
        val view = find<RatingBar>(id)
        binding.subscribe { view.rating = it }
    }

    fun webImageView(id: Int, binding: Binding<Image?>) {
        val view = find<WebImageView>(id)
        binding.subscribe { view.image = it }
    }

    fun muteVideoView(id: Int, binding: Binding<File?>) {
        val view = find<MuteVideoView>(id)
        binding.subscribe { it?.let { view.play(it) } }
    }

    fun viewResolver(id: Int) {
        resolvers.add(find<ViewResolver>(id))
    }

    fun refreshLayout(id: Int, init: SwipeRefreshLayoutBinding.() -> Unit) {
        SwipeRefreshLayoutBinding(find<SwipeRefreshLayout>(id)).init()
    }

    fun view(id: Int, init: ViewBinding.() -> Unit) {
        ViewBinding(find<View>(id)).init()
    }

    fun command(id: Int, f: () -> Unit) {
        find<View>(id).setOnClickListener { f() }
    }

    fun <T> action(binding: Binding<T>, f: (T) -> Unit) {
        binding.subscribe(f)
    }

    fun <T> recyclerView(id: Int, binding: Binding<out List<T>>, init: DslRecyclerView<T>.() -> Unit) {
        val view = find<RecyclerView>(id)
        val dsl = DslRecyclerView<T>()
        dsl.init()
        // TODO: разобраться с конвертирование
        view.adapter = dsl.build().apply { binding.subscribe { update(it as List<T>) } }
    }

    fun loadingProgressBar(id: Int, binding: Binding<Boolean>) {
        val view = find<ContentLoadingProgressBar>(id)
        binding.subscribe { if (it) view.show() else view.hide() }
    }

    fun <T> textView(id: Int, binding: Binding<T>) {
        val view = find<TextView>(id)
        binding.subscribe { if (view.text.toString() != it.toString()) view.text = it.toString() }
    }

    fun fixedAspectPanel(id: Int, binding: Binding<Float>) {
        val view = find<FixedAspectPanel>(id)
        binding.subscribe { view.aspect = it }
    }

    fun progressImageView(id: Int, binding: Binding<PartialResult<File>>) {
        val view = find<ProgressImageView>(id)
        binding.subscribe { view.image = it }
    }

    fun tagsView(id: Int, binding: Binding<List<String>>) {
        val view = find<TagsView>(id)
        binding.subscribe { view.tags = it }
    }

    fun imagePanel(id: Int, binding: Binding<List<Image>>) {
        val view = find<ImagePanel>(id)
        binding.subscribe { view.setImages(it) }
    }

    fun editText(id: Int, init: EditTextBinding.() -> Unit) {
        EditTextBinding(find<EditText>(id)).init()
    }

    fun editText(id: Int, binding: Binding<String>) {
        val view = find<EditText>(id)
        binding.subscribe { if (view.text.toString() != it) view.setText(it) }
        view.addTextChangedListener(object : TextWatcherAdapter() {

            override fun afterTextChanged(s: Editable?) {
                binding.value = "" + s
            }
        })
    }

    fun <T> visibility(id: Int, binding: Binding<T>, converter: (T) -> Boolean) {
        val view = find<View>(id)
        binding.subscribe { view.visibility = if (converter(it)) View.VISIBLE else View.GONE }
    }

    fun visibility(id: Int, binding: Binding<Boolean>, invert: Boolean = false) {
        val view = find<View>(id)
        binding.subscribe {
            if (invert) view.visibility = if (it) View.GONE else View.VISIBLE
            else view.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    fun webView(id: Int, init: WebViewBinding.() -> Unit) {
        WebViewBinding(find<WebView>(id)).init()
    }

    fun viewPager(view: ViewPager, binding: Binding<Int>) {
        view.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                binding.value = position
            }
        })
    }

    private fun <T : Any> find(id: Int): T {
        return resolvers.mapNotNull { it.find<T>(id) }.first()
    }
}

class EditTextBinding(val view: EditText) {

    fun text(binding: Binding<String>) {
        binding.subscribe { if (view.text.toString() != it) view.setText(it) }
        view.addTextChangedListener(object : TextWatcherAdapter() {

            override fun afterTextChanged(s: Editable?) {
                binding.value = "" + s
            }
        })
    }

    fun error(binding: Binding<Boolean>, text: String) {
        binding.subscribe { view.error = if (it) text else null }
    }
}

class MenuBinding(menuId: Int, resolvers: List<ViewResolver>) {

    private val menu = MenuHolder(menuId)

    init {
        val activity = resolvers
            .filterIsInstance(ActivityViewResolver::class.java)
            .map { it.activity as BaseActivity }
            .first()
        activity.menuHolder = menu
    }

    fun command(id: Int, command: () -> Unit) {
        menu.addAction(id, command)
    }
}

class SwipeRefreshLayoutBinding(private val view: SwipeRefreshLayout) {

    fun isRefreshing(binding: Binding<Boolean>) {
        binding.subscribe { view.isRefreshing = it }
    }

    fun command(func: () -> Unit) {
        view.setOnRefreshListener(func)
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

    fun <T> find(id: Int): T?
}