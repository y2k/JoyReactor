package y2k.joyreactor

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import y2k.joyreactor.common.*
import y2k.joyreactor.model.Comment
import y2k.joyreactor.viewmodel.PostViewModel
import y2k.joyreactor.widget.WebImageView

class PostActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val vm = ServiceLocator.resolve<PostViewModel>(lifeCycleService)
        bindingBuilder(this) {
            viewResolver(R.id.list)

            visibility(R.id.createComment, vm.canCreateComments)
            command(R.id.createComment) { vm.commentPost() }

            progressImageView(R.id.poster, vm.poster)
            fixedAspectPanel(R.id.posterPanel, vm.posterAspect)

            tagsView(R.id.tags, vm.tags)

            // Image panel
            imagePanel(R.id.images, vm.images) { vm.openImage(it) }
            visibility(R.id.showMoreImages, vm.images, { it.size > 3 })
            visibility(R.id.imagePanel, vm.images, { it.isNotEmpty() })
            command(R.id.showMoreImages) { vm.showMoreImages() }

            textView(R.id.description, vm.description)
            recyclerView(R.id.list, vm.comments) {
                itemId { it.id }
                viewHolder {
                    CommentViewHolder(it).apply {
                        itemView.findViewById(R.id.action).setOnClickListener {
                            lastComment?.let { vm.selectComment(it) }
                        }
                    }
                }
            }

            visibility(R.id.error, vm.error)
            snackbar(R.id.list, R.string.updating, vm.isBusy)

            menu(R.menu.menu_post) {
                command(R.id.saveImageToGallery) { vm.saveToGallery() }
                command(R.id.openInBrowser) { vm.openInBrowser() }
            }
        }
    }

    class CommentViewHolder(parent: ViewGroup) :
        ListViewHolder<Comment>(parent.inflate(R.layout.item_comment)) {

        val rating by view<TextView>()
        val text by view<TextView>()
        val replies by view<TextView>()
        val avatar by view<WebImageView>()
        val attachment by view<WebImageView>()

        var lastComment: Comment? = null

        override fun update(item: Comment) {
            lastComment = item
            itemView.updateMargin(left = (28 * item.level + 8).dipToPx())

            text.text = item.text
            avatar.image = item.userImageObject.toImage()
            rating.text = "" + item.rating
            replies.text = "" + item.replies

            attachment.setVisible(item.attachment != null)
            attachment.image = item.attachment
        }
    }
}