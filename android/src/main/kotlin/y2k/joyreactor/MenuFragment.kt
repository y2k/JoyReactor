package y2k.joyreactor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import y2k.joyreactor.common.*
import y2k.joyreactor.model.Group
import y2k.joyreactor.viewmodel.TagListViewModel

/**
 * Created by y2k on 11/12/15.
 */
class MenuFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        val vm = ServiceLocator.resolve<TagListViewModel>(lifeCycleService)
        bindingBuilder(view) {
            viewResolver(R.id.list)

            command(R.id.selectFeatured, { vm.selectedFeatured() })
            command(R.id.selectFavorite, { vm.selectedFavorite() })

            recyclerView(R.id.list, vm.tags) {
                itemId { it.id }
                viewHolder {
                    ViewHolder(it.inflate(R.layout.item_subscription)).apply {
                        itemView
                            .findViewById(R.id.action)
                            .setOnClickListener { vm.selectTag(vm.tags.value[layoutPosition - 1]) }
                    }
                }
            }
        }
        return view
    }

    class ViewHolder(view: View) : ListViewHolder<Group>(view) {

        val title = view.find<TextView>(R.id.title)
        val icon = view.find<WebImageView>(R.id.icon)

        override fun update(item: Group) {
            title.text = item.title
            icon.image = item.image
        }
    }
}