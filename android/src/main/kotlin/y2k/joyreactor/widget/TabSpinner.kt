package y2k.joyreactor.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.support.v7.widget.ThemedSpinnerAdapter
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView
import y2k.joyreactor.R
import y2k.joyreactor.common.px
import y2k.joyreactor.common.inflate

/**
 * Created by y2k on 3/27/16.
 */
class TabSpinner(context: Context, attrs: AttributeSet?) : Spinner(context, attrs) {

    init {
        val items = context.resources.getStringArray(R.array.tabs_mode)
        adapter = TabAdapter(context, items)
    }

    class TabAdapter(context: Context, val items: Array<String>) : BaseAdapter(), ThemedSpinnerAdapter {

        private val dropDownHelper = ThemedSpinnerAdapter.Helper(context)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view = (convertView ?: parent.inflate(android.R.layout.simple_list_item_1))as TextView
            view.text = items[position]
            view.setTextColor(Color.WHITE)
            return view
        }

        override fun getItem(position: Int): Any? {
            throw UnsupportedOperationException()
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return items.size
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = dropDownHelper.dropDownViewInflater
            val view = (convertView ?: inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)) as TextView
            view.text = items[position]
            view.setTextColor(Color.WHITE)
            view.minWidth = 100.px()
            view.setPaddingRelative(20.px(), 0, 0, 0)
            return view
        }

        override fun setDropDownViewTheme(theme: Resources.Theme?) {
            dropDownHelper.dropDownViewTheme = theme
        }

        override fun getDropDownViewTheme(): Resources.Theme? {
            return dropDownHelper.dropDownViewTheme
        }
    }
}