package y2k.joyreactor.widget

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import y2k.joyreactor.R
import y2k.joyreactor.common.BindableComponent
import y2k.joyreactor.common.property
import y2k.joyreactor.common.setOnClick
import y2k.joyreactor.common.view
import y2k.joyreactor.model.Group

/**
 * Created by y2k on 10/07/16.
 */
class TagComponent(
    context: Context?, val onSelect: (Group) -> Unit) : FrameLayout(context), BindableComponent<Group> {

    private val title by view<TextView>()
    private val icon by view<WebImageView>()

    override val value = property(Group.Undefined)

    init {
        View.inflate(context, R.layout.item_subscription, this)
        setOnClick(R.id.action) { onSelect(value.value) }
        value.subscribe {
            if (it !== Group.Undefined) {
                title.text = it.title
                icon.image = it.image
            }
        }
    }
}