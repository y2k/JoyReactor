package y2k.joyreactor

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import y2k.joyreactor.common.*
import y2k.joyreactor.model.Comment
import y2k.joyreactor.viewmodel.PostViewModel

class PostActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val vm = ServiceLocator.resolve<PostViewModel>()
        bindingBuilder(this) {
            viewResolver(R.id.list)

            progressImageView(R.id.poster, vm.poster)
            fixedAspectPanel(R.id.posterPanel, vm.posterAspect)

            tagsView(R.id.tags, vm.tags)

            // Image panel
            imagePanel(R.id.images, vm.images)
            visibility(R.id.showMoreImages, vm.images, { it.size > 3 })
            visibility(R.id.imagePanel, vm.images, { it.isNotEmpty() })
            command(R.id.showMoreImages) { vm.showMoreImages() }

            textView(R.id.description, vm.description)
            recyclerView(R.id.list, vm.comments) {
                itemId { it.id }
                viewHolder {
                    CommentViewHolder(it, vm)
                }
            }

            visibility(R.id.error, vm.error)
            visibility(R.id.progress, vm.isBusy)

            menu(R.menu.menu_post) {
                command(R.id.saveImageToGallery) { vm.saveToGallery() }
                command(R.id.openInBrowser) { vm.openInBrowser() }
            }
        }
    }

    class CommentViewHolder(parent: ViewGroup, vm: PostViewModel) :
        ListViewHolder<Comment>(parent.inflate(R.layout.item_comment)) {

        val rating = itemView.find<TextView>(R.id.rating)
        val text = itemView.find<TextView>(R.id.text)
        val replies = itemView.find<TextView>(R.id.replies)
        val avatar = itemView.find<WebImageView>(R.id.avatar)
        val attachment = itemView.find<WebImageView>(R.id.attachment)
        var lastComment: Comment? = null

        init {
            itemView.findViewById(R.id.action).setOnClickListener {
                lastComment?.let { vm.selectComment(it) }
            }
        }

        override fun update(item: Comment) {
            lastComment = item
            itemView.updateMargin(left = (28 * item.level + 8).dipToPx())

            text.text = item.text
            avatar.image = item.userImageObject.toImage()
            rating.text = "" + item.rating
            replies.text = "" + item.replies

            attachment.visibility = if (item.attachmentObject == null) View.GONE else View.VISIBLE
            attachment.image = item.attachmentObject
        }
    }
}