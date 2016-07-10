package y2k.joyreactor.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import y2k.joyreactor.R
import y2k.joyreactor.common.BindableComponent
import y2k.joyreactor.common.listProperty
import y2k.joyreactor.common.setVisible
import y2k.joyreactor.common.view
import y2k.joyreactor.model.Image

/**
 * Created by y2k on 10/07/16.
 */
class AttachmentsComponent(
    context: Context?, attrs: AttributeSet?) :
    RelativeLayout(context, attrs), BindableComponent<List<Image>> {

    private val showMoreImages by view<Button>()
    private val images by view<ImagePanel>()

    override val value = listProperty<Image>()
    var commandShowMore: () -> Unit = {}
    var commandOpen: (Image) -> Unit = {}

    init {
        View.inflate(context, R.layout.component_attachments, this)
        value.subscribe { onAttachmentsChanged(it) }

        showMoreImages.setOnClickListener { commandShowMore() }
    }

    private fun onAttachmentsChanged(attachments: List<Image>) {
        images.setImages(attachments) { commandOpen(it) }
        showMoreImages.setVisible(attachments.size > 3)
        setVisible(attachments.size > 0)
    }
}