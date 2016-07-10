package y2k.joyreactor

import android.os.Bundle
import y2k.joyreactor.common.BaseActivity
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.model.Comment
import y2k.joyreactor.model.Image
import y2k.joyreactor.viewmodel.PostViewModel

class PostActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val vm = ServiceLocator.resolve<PostViewModel>(lifeCycleService)
        bindingBuilder(this) {
            visibility(R.id.createComment, vm.canCreateComments)
            command(R.id.createComment) { vm.commentPost() }

            progressImageView(R.id.poster, vm.poster)
            fixedAspectPanel(R.id.posterPanel, vm.posterAspect)

            tagsView(R.id.tags, vm.tags)

            // Image panel
            bind(R.id.attachments, vm.images)
            command(R.id.attachments, "commandShowMore") { vm.showMoreImages() }
            command<Image>(R.id.attachments, "commandOpen") { vm.openImage(it) }

            textView(R.id.description, vm.description)

            bind(R.id.comments, vm.comments)
            command<Comment>(R.id.comments, "commandOpenComment") { vm.selectComment(it) }
            snackbar(R.id.comments, R.string.updating, vm.isBusy)

            visibility(R.id.error, vm.error)

            menu(R.menu.menu_post) {
                command(R.id.saveImageToGallery) { vm.saveToGallery() }
                command(R.id.openInBrowser) { vm.openInBrowser() }
            }
        }
    }
}