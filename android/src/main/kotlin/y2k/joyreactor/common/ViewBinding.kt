package y2k.joyreactor.common

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.support.design.widget.Snackbar
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
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Image
import y2k.joyreactor.widget.*
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

    fun snackbar(viewId: Int, stringRes: Int, property: ObservableProperty<Boolean>) {
        val snackbar = Snackbar.make(find(viewId), stringRes, Snackbar.LENGTH_INDEFINITE)
        snackbar.setActionTextColor(Color.WHITE)
        property.subscribe {
            if (it) snackbar.show()
            else snackbar.dismiss()
        }
    }

    fun blockDialog(dialog: Dialog, property: ObservableProperty<Boolean>) {
        property.subscribe { dialog.setCancelable(!it) }
    }

    fun menu(menuId: Int, init: MenuBinding.() -> Unit) {
        MenuBinding(menuId, resolvers).init()
    }

    //    fun refreshLayout(id: Int, binding: Binding<Boolean>) {
    //        val view = root.find<SwipeRefreshLayout>(id)
    //        binding.subscribe { view.isRefreshing = it }
    //    }

    fun spinnerTemp(id: Int, property: ObservableProperty<Group.Quality>) {
        val view = find<Spinner>(id)
        property.subscribe { view.setSelection(it.ordinal) }
        view.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                property += Group.Quality.valueOf(Group.Quality.values()[position].name)
            }
        }
    }

    fun spinner(id: Int, property: ObservableProperty<Int>) {
        val view = find<Spinner>(id)
        property.subscribe { view.setSelection(it) }
        view.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                property += position
            }
        }
    }

    fun tabLayout(id: Int, property: ObservableProperty<Int>) {
        val view = find<TabLayout>(id)
        view.setOnTabSelectedListener(object : OnTabSelectedListenerAdapter() {

            override fun onTabReselected(tab: TabLayout.Tab) {
                property += tab.position
            }
        })
        property.subscribe { view.getTabAt(it)?.select() }
    }

    fun radioGroup(id: Int, property: ObservableProperty<Int>) {
        val view = find<RadioGroup>(id)
        property.subscribe { (view.getChildAt(it) as RadioButton).isChecked = true }
        view.setOnCheckedChangeListener { group, id ->
            view.getChildren().map { it as RadioButton }.indexOfFirst { it.isChecked }
        }
    }

    fun animator(id: Int, property: ObservableProperty<Boolean>) {
        animator(id, property) { if (it) 1 else (0) }
    }

    fun <T> animator(id: Int, property: ObservableProperty<T>, convert: (T) -> Int) {
        val view = find<ViewAnimator>(id)
        property.subscribe { view.displayedChild = convert(it) }
    }

    fun progressBar(id: Int, property: ObservableProperty<Float>) {
        val view = find<ProgressBar>(id)
        property.subscribe { view.progress = it.toInt() }
    }

    fun ratingBar(id: Int, property: ObservableProperty<Float>) {
        val view = find<RatingBar>(id)
        property.subscribe { view.rating = it }
    }

    fun webImageView(id: Int, property: ObservableProperty<Image?>) {
        val view = find<WebImageView>(id)
        property.subscribe { view.image = it }
    }

    fun muteVideoView(id: Int, property: ObservableProperty<File?>) {
        val view = find<MuteVideoView>(id)
        property.subscribe { it?.let { view.play(it) } }
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

    fun <T> action(property: ObservableProperty<T>, f: (T) -> Unit) {
        property.subscribe(f)
    }

    fun <T> recyclerView(id: Int, property: ObservableProperty<out List<T>>, init: DslRecyclerView<T>.() -> Unit) {
        val view = find<RecyclerView>(id)
        val dsl = DslRecyclerView<T>()
        dsl.init()
        // TODO: разобраться с конвертирование
        view.adapter = dsl.build().apply { property.subscribe { update(it as List<T>) } }
    }

    fun loadingProgressBar(id: Int, property: ObservableProperty<Boolean>) {
        val view = find<ContentLoadingProgressBar>(id)
        property.subscribe { if (it) view.show() else view.hide() }
    }

    fun <T> textView(id: Int, property: ObservableProperty<T>) {
        val view = find<TextView>(id)
        property.subscribe { if (view.text.toString() != it.toString()) view.text = it.toString() }
    }

    fun fixedAspectPanel(id: Int, property: ObservableProperty<Float>) {
        val view = find<FixedAspectPanel>(id)
        property.subscribe { view.aspect = it }
    }

    fun progressImageView(id: Int, property: ObservableProperty<File?>) {
        val view = find<ProgressImageView>(id)
        property.subscribe { view.setImage(it) }
    }

    fun imageView(id: Int, property: ObservableProperty<File?>) {
        val view = find<ImageView>(id)
        property.subscribe { view.setImageURI(it?.let { Uri.fromFile(it) }) }
    }

    fun tagsView(id: Int, property: ObservableProperty<List<String>>) {
        val view = find<TagsView>(id)
        property.subscribe { view.tags = it }
    }

    fun imagePanel(id: Int, property: ObservableProperty<List<Image>>, f: (Image) -> Unit) {
        val view = find<ImagePanel>(id)
        property.subscribe { view.setImages(it, f) }
    }

    fun editText(id: Int, init: EditTextBinding.() -> Unit) {
        EditTextBinding(find<EditText>(id)).init()
    }

    fun editText(id: Int, property: ObservableProperty<String>) {
        val view = find<EditText>(id)
        property.subscribe { if (view.text.toString() != it) view.setText(it) }
        view.addTextChangedListener(object : TextWatcherAdapter() {

            override fun afterTextChanged(s: Editable?) {
                property += "" + s
            }
        })
    }

    fun <T> visibility(id: Int, property: ObservableProperty<T>, converter: (T) -> Boolean) {
        val view = find<View>(id)
        property.subscribe { view.visibility = if (converter(it)) View.VISIBLE else View.GONE }
    }

    fun visibility(id: Int, property: ObservableProperty<Boolean>, invert: Boolean = false) {
        val view = find<View>(id)
        property.subscribe {
            if (invert) view.visibility = if (it) View.GONE else View.VISIBLE
            else view.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    fun webView(id: Int, init: WebViewBinding.() -> Unit) {
        WebViewBinding(find<WebView>(id)).init()
    }

    fun viewPager(view: ViewPager, property: ObservableProperty<Int>) {
        view.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                property += position
            }
        })
    }

    fun <T : Any> find(id: Int): T {
        return resolvers.mapNotNull { it.find<T>(id) }.first()
    }

    fun <T> bind(id: Int, property: ObservableProperty<T>) {
        val view = find<BindableComponent<T>>(id)
        property.subscribe { view.value += it }
    }

    fun command(id: Int, command: String, f: () -> Unit) {
        val view = find<View>(id)
        view.javaClass.getMethod(toSetterName(command), Function0::class.java).invoke(view, f)
    }

    fun <T> command(id: Int, command: String, f: (T) -> Unit) {
        val view = find<View>(id)
        view.javaClass.getMethod(toSetterName(command), Function1::class.java).invoke(view, f)
    }

    private fun toSetterName(prop: String) = "set${prop.substring(0, 1).toUpperCase()}${prop.substring(1)}"
}

interface BindableComponent<T> {

    val value: ObservableProperty<T>
}

class EditTextBinding(val view: EditText) {

    fun text(property: ObservableProperty<String>) {
        property.subscribe { if (view.text.toString() != it) view.setText(it) }
        view.addTextChangedListener(object : TextWatcherAdapter() {

            override fun afterTextChanged(s: Editable?) {
                property += "" + s
            }
        })
    }

    fun error(property: ObservableProperty<Boolean>, text: String) {
        property.subscribe { view.error = if (it) text else null }
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

    fun isRefreshing(property: ObservableProperty<Boolean>) {
        property.subscribe {
            view.post { view.isRefreshing = it }
        }
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

    fun component(f: (ViewGroup) -> View) {
        viewHolder { BindableListViewHolder(f(it)) }
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

class BindableListViewHolder<T>(view: View) : ListViewHolder<T>(view) {

    @Suppress("UNCHECKED_CAST")
    val component = view as BindableComponent<T>

    init {
        view.layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun update(item: T) {
        component.value += item
    }
}

abstract class ListViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun update(item: T)
}

class ViewBinding(private val view: View) {

    fun click(f: () -> Unit) {
        view.setOnClickListener { f() }
    }

    fun visibility(property: ObservableProperty<Boolean>) {
        property.subscribe { view.visibility = if (it) View.VISIBLE else View.GONE }
    }
}

class WebViewBinding(private val webView: WebView) {

    val settings: WebSettings
        get() = webView.settings

    fun url(property: ObservableProperty<String>) {
        property.subscribe { webView.loadUrl(it) }
    }

    fun title(property: ObservableProperty<String>) {
        webView.setWebViewClient(object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String?) {
                property += view.title
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