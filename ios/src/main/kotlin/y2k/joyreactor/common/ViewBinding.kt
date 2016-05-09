package y2k.joyreactor.common

import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.foundation.NSURL
import org.robovm.apple.foundation.NSURLRequest
import org.robovm.apple.uikit.*
import y2k.joyreactor.ProgressBar
import y2k.joyreactor.StarProgress
import y2k.joyreactor.model.Image
import y2k.joyreactor.platform.ImageRequest
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 2/26/16.
 */
fun bindingBuilder(controller: UIViewController? = null, init: BindingBuild.() -> Unit) {
    BindingBuild(controller).init()
}

class BindingBuild(private val controller: UIViewController?) {

    fun <T> bind(source: ObservableProperty<T>, destination: ObservableProperty<T>) {
        subscribe(source) { destination.value = it }
    }

    fun refreshControl(view: UIRefreshControl, property: ObservableProperty<Boolean>) {
        subscribe(property) {
            if (it) view.beginRefreshing()
            else view.endRefreshing()
        }
    }

    fun indicatorView(view: UIActivityIndicatorView, property: ObservableProperty<Boolean>) {
        subscribe(property) {
            if (it) view.startAnimating();
            else view.stopAnimating();
        }
    }

    fun starProgress(view: StarProgress, property: ObservableProperty<Float>) {
        subscribe(property) { view.setStars(it.toInt()) }
    }

    fun progressBar(view: ProgressBar, property: ObservableProperty<Float>) {
        subscribe(property) { view.setValue(it) }
    }

    fun imageView(view: UIImageView, property: ObservableProperty<Image?>) {
        subscribe(property) {
            ImageRequest()
                .setUrl(it)
                .setSize(view.frame.width.toInt(), view.frame.height.toInt())
                .to(view) { view.image = it }
        }
    }

    inline fun <T> label(view: UILabel, property: ObservableProperty<T>, crossinline converter: (T) -> String) {
        property.subscribe { view.text = converter(it) }
    }

    fun label(view: UILabel, property: ObservableProperty<String>) {
        subscribe(property) { view.text = it }
    }

    fun navigationItem(init: NavigationItemBinding.() -> Unit) {
        NavigationItemBinding(controller!!).init() // TODO:
    }

    fun focusOrder(vararg views: UITextField) {
        for (i in 0..views.size - 2)
            views[i].delegate = DefaultUITextFieldDelegate(views[i + 1])
        views.last().delegate = DefaultUITextFieldDelegate()
    }

    fun <T> action(property: ObservableProperty<T>, callback: (T) -> Unit) {
        property.subscribe(callback)
    }

    fun textField(view: UITextField, property: ObservableProperty<String>) {
        subscribe(property) { view.text = it }
        view.addOnEditingChangedListener {
            property.value = view.text
        }
    }

    fun textView(view: UITextView, property: ObservableProperty<String>) {
        subscribe(property) { view.text = it }
        view.setDelegate(object : UITextViewDelegateAdapter() {

            override fun didChange(textView: UITextView) {
                property.value = textView.text
            }
        })
    }

    fun <T, TC : ListCell<T>> tableView(view: UITableView, type: KClass<TC>, property: ObservableProperty<List<T>>) {
        val source = ListDataSource.Default<T, TC>(view);
        subscribe(property) { source.update(it) }
        view.dataSource = source
    }

    fun <T> tableView(view: UITableView, property: ObservableProperty<out List<T>>, init: UITableViewBinding<T>.() -> Unit) {
        //        val source = ListDataSource.Default<T, TC>(view);
        //        subscribe(binding) { source.update(it) }
        //        view.dataSource = source
        UITableViewBinding(view, property).init()
    }

    fun command(view: UIButton, command: () -> Unit) {
        view.addOnTouchUpInsideListener { sender, e -> command() }
    }

    fun visible(view: UIView, property: ObservableProperty<Boolean>, invert: Boolean = false) {
        subscribe(property) { view.isHidden = !it xor invert }
    }

    private inline fun <T> subscribe(property: ObservableProperty<T>, crossinline f: (T) -> Unit) {
        property.subscribe { f(it) }
    }
}

class UITableViewBinding<T>(
    private val view: UITableView,
    private val property: ObservableProperty<out List<T>>) {

    val source = ListDataSource2<T>(view);

    init {
        property.subscribe { source.update(it as List<T>) }
        view.dataSource = source
    }

    fun command(f: (Int) -> Unit) {
        view.setDelegate(object : UITableViewDelegateAdapter() {
            override fun willSelectRow(tableView: UITableView?, indexPath: NSIndexPath): NSIndexPath? {
                f(indexPath.row)
                return null
            }
        })
    }

    fun cellSelector(f: (T) -> String) {
        source.selector = f
    }
}

class NavigationItemBinding(private val controller: UIViewController) {

    fun rightCommand(command: () -> Unit) {
        controller.navigationItem
            .rightBarButtonItem
            .setOnClickListener { command() }
    }
}

fun UIWebView.bindUrl(property: ObservableProperty<String>) {
    property.subscribe { loadRequest(NSURLRequest(NSURL(it))) }
    loadRequest(NSURLRequest(NSURL(property.value))) // TODO:
}

fun UIWebView.bindTitle(property: ObservableProperty<String>) {
    delegate = object : UIWebViewDelegateAdapter() {

        override fun didFinishLoad(webView: UIWebView?) {
            property.value = evaluateJavaScript("document.title")
        }
    }
}

fun UIView.bind(property: ObservableProperty<Boolean>) {
    property.subscribe { isHidden = !it }
}

fun UILabel.bind(text: String) {
    this.text = text
}

abstract class ListDataSource<T>(private val tableView: UITableView) : UITableViewDataSourceAdapter() {

    protected val items = ArrayList<T>()

    override fun getNumberOfRowsInSection(tableView: UITableView?, section: Long): Long {
        return items.size.toLong()
    }

    fun update(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        tableView.reloadData()
    }

    class Default<T, TC : ListCell<T>>(tableView: UITableView) : ListDataSource<T>(tableView) {

        @Suppress("UNCHECKED_CAST")
        override fun getCellForRow(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell? {
            val cell = tableView.dequeueReusableCell("cell", indexPath) as TC
            return cell.apply { bind(items[indexPath.row], indexPath.row) }
        }
    }
}

abstract class ListCell<T> : UITableViewCell() {

    abstract fun bind(data: T, position: Int)
}

private class DefaultUITextFieldDelegate(val next: UITextField? = null) : UITextFieldDelegateAdapter() {

    override fun shouldReturn(textField: UITextField): Boolean {
        if (next == null) textField.resignFirstResponder()
        else next.becomeFirstResponder()
        return true
    }
}