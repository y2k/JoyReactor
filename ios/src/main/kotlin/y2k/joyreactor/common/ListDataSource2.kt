package y2k.joyreactor.common

import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.UITableView
import org.robovm.apple.uikit.UITableViewCell
import org.robovm.apple.uikit.UITableViewDataSourceAdapter
import java.util.*

/**
 * Created by y2k on 3/12/16.
 */
class ListDataSource2<T>(private val tableView: UITableView) : UITableViewDataSourceAdapter() {

    lateinit var selector: (T) -> String

    private val items = ArrayList<T>()

    override fun getNumberOfRowsInSection(tableView: UITableView?, section: Long): Long {
        return items.size.toLong()
    }

    fun update(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        tableView.reloadData()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCellForRow(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell? {
        val item = items[indexPath.row]
        val cell = tableView.dequeueReusableCell(selector(item), indexPath) as ListCell<T>
        return cell.apply { bind(item) }
    }
}