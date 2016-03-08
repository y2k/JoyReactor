package y2k.joyreactor.common

import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.foundation.NSURL
import org.robovm.apple.foundation.NSURLRequest
import org.robovm.apple.uikit.*
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 2/26/16.
 */
fun bindingBuilder(init: BindingBuild.() -> Unit) {
    BindingBuild().init()
}

class BindingBuild {

    fun focusOrder(vararg views: UITextField) {
        for (i in 0..views.size - 2)
            views[i].delegate = DefaultUITextFieldDelegate(views[i + 1])
        views.last().delegate = DefaultUITextFieldDelegate()
    }

    fun <T> action(binding: Binding<T>, callback: (T) -> Unit) {
        binding.subscribe(callback)
    }

    fun textField(view: UITextField, binding: Binding<String>) {
        binding.subscribe { view.text = it }
        view.addOnEditingChangedListener {
            binding.value = view.text
        }
    }

    fun textView(view: UITextView, binding: Binding<String>) {
        binding.subscribe { view.text = it }
        view.setDelegate(object : UITextViewDelegateAdapter() {

            override fun didChange(textView: UITextView) {
                binding.value = textView.text
            }
        })
    }

    fun <T, TC : ListCell<T>> tableView(view: UITableView, type: KClass<TC>, binding: Binding<List<T>>) {
        val source = ListDataSource.Default<T, TC>(view);
        binding.subscribe { source.update(it) }
        view.dataSource = source
    }

    fun command(view: UIButton, command: () -> Unit) {
        view.addOnTouchUpInsideListener { sender, e -> command() }
    }

    fun visible(view: UIView, binding: Binding<Boolean>, invert: Boolean = false) {
        binding.subscribe { view.isHidden = !it xor invert }
    }
}

fun UIWebView.bindUrl(binding: Binding<String>) {
    binding.subscribe { loadRequest(NSURLRequest(NSURL(it))) }
    loadRequest(NSURLRequest(NSURL(binding.value))) // TODO:
}

fun UIWebView.bindTitle(binding: Binding<String>) {
    delegate = object : UIWebViewDelegateAdapter() {

        override fun didFinishLoad(webView: UIWebView?) {
            binding.value = evaluateJavaScript("document.title")
        }
    }
}

fun UIView.bind(binding: Binding<Boolean>) {
    binding.subscribe { isHidden = !it }
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
            return cell.apply { bind(items[indexPath.row]) }
        }
    }
}

abstract class ListCell<T> : UITableViewCell() {

    abstract fun bind(data: T)
}

private class DefaultUITextFieldDelegate(val next: UITextField? = null) : UITextFieldDelegateAdapter() {

    override fun shouldReturn(textField: UITextField): Boolean {
        if (next == null) textField.resignFirstResponder()
        else next.becomeFirstResponder()
        return true
    }
}