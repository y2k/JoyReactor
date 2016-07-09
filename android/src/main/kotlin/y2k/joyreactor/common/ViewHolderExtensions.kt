package y2k.joyreactor.common

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlin.reflect.KProperty

/**
 * Created by y2k on 5/29/16.
 */

fun RecyclerView.ViewHolder.setOnClick(id: Int, f: (Int) -> Unit) {
    itemView.find<View>(id).setOnClickListener { f(layoutPosition) }
}

fun <T : View> ViewGroup.view(): ViewGroupDelegate<T> {
    return ViewGroupDelegate()
}

class ViewGroupDelegate<out T : View>() {

    private var cached: T? = null

    operator fun getValue(group: ViewGroup, property: KProperty<*>): T {
        if (cached == null) {
            val context = group.context
            val id = context.resources.getIdentifier(property.name, "id", context.packageName)
            cached = group.findViewById(id) as T
        }
        return cached!!
    }
}

fun <T : View> RecyclerView.ViewHolder.view(): ViewHolderDelegate<T> {
    return ViewHolderDelegate()
}

class ViewHolderDelegate<out T : View>() {

    private var cached: T? = null

    operator fun getValue(holder: RecyclerView.ViewHolder, property: KProperty<*>): T {
        if (cached == null) {
            val context = holder.itemView.context
            val id = context.resources.getIdentifier(property.name, "id", context.packageName)
            cached = holder.itemView.findViewById(id) as T
        }
        return cached!!
    }
}