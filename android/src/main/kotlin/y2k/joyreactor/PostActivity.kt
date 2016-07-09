package y2k.joyreactor

import android.os.Bundle
import y2k.joyreactor.common.BaseActivity
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.common.setOnClick
import y2k.joyreactor.viewmodel.PostViewModel
import y2k.joyreactor.widget.CommentComponent

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
                component {
                    CommentComponent(it.context).apply {
                        setOnClick(R.id.action) { vm.selectComment(value.value) }
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
}