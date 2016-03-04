package y2k.joyreactor.common

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import java.util.*

/**
 * Created by y2k on 3/4/16.
 */
class MenuHolder(private val menuId: Int = 0) {

    private val actions = HashMap<Int, () -> Unit>()

    fun addAction(id: Int, action: () -> Unit) {
        actions[id] = action
    }

    fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater): Boolean {
        if (menuId == 0) return false
        menuInflater.inflate(menuId, menu)
        return true
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        val action = actions[item.itemId] ?: return false
        action()
        return true
    }
}