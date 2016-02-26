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

    fun click(view: UIButton, command: () -> Unit) {
        view.addOnTouchUpInsideListener { sender, e -> command() }
    }

    fun view(view: UIView, binding: Binding<Boolean>, invert: Boolean = false) {
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

//fun UIImageView.bind(image: Image) {
//    ServiceLocator
//        .resolveImageService<UIImage>()
//        .get(image.normalize(frame.width.toInt(), frame.height.toInt()))
//        .subscribe { this.image = it }
//}

//fun <T, TC : ListCell<T>> UITableView.bind(binding: Binding<List<T>>) {
//    val source = ListDataSource.Default<T, TC>(this);
//    binding.subscribe { source.update(it) }
//    dataSource = source
//}

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