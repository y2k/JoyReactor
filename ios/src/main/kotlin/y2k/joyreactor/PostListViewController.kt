package y2k.joyreactor

import org.ocpsoft.prettytime.PrettyTime
import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBAction
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.*
import y2k.joyreactor.model.Image
import y2k.joyreactor.model.Post
import y2k.joyreactor.platform.ImageRequest
import y2k.joyreactor.platform.StoryboardNavigation
import y2k.joyreactor.viewmodel.PostListViewModel
import y2k.joyreactor.widget.Snackbar

/**
 * Created by y2k on 9/26/15.
 */
@CustomClass("PostListViewController")
class PostListViewController : BaseUIViewController() {

    @IBOutlet lateinit var list: UITableView
    @IBOutlet lateinit var progressView: UIActivityIndicatorView
    @IBOutlet lateinit var applyButton: UIButton
    @IBOutlet lateinit var hasNewPosts: Snackbar

    override fun viewDidLoad() {
        super.viewDidLoad()

        list.estimatedRowHeight = 100.0
        list.rowHeight = UITableView.getAutomaticDimension()

        SideMenu(this, "Menu").attach()
        val refresher = UIRefreshControl()
        list.addSubview(refresher)

        val vm = ServiceLocator.resolve<PostListViewModel>(lifeCycleService)
        bindingBuilder(this) {
            refreshControl(refresher, vm.isBusy)
            action(vm.hasNewPosts) { BottomButton(applyButton).setHidden(!it) }
            indicatorView(progressView, vm.isBusy)

            command(applyButton) { vm.applyNew() }
            bind(vm.hasNewPosts, hasNewPosts.visible)

            navigationItem {
                rightCommand {
                    MenuController(this@PostListViewController)
                        .addNavigation("Add tag".translate(), "AddTag")
                        .addNavigation("Profile".translate(), "Profile")
                        .addNavigation("Messages".translate(), "MessageThreads")
                        .addCancel("Cancel".translate())
                        .present()
                }
            }

            tableView(list, vm.posts) {
                cellSelector { if (it == null) "LoadMore" else "Post" }
                command { vm.postClicked(it) }
            }
        }
    }

    @CustomClass("LoadMoreCell")
    class LoadMoreCell : ListCell<Post?>() {

        override fun bind(data: Post?, position: Int) {
            TODO()
        }
    }

    @CustomClass("PostCell")
    class PostCell : ListCell<Post?>() {

        @IBOutlet lateinit var replyCountView: UILabel
        @IBOutlet lateinit var ratingView: UILabel
        @IBOutlet lateinit var playButton: UIButton
        @IBOutlet lateinit var rateButton: UIButton

        @IBOutlet lateinit var userName: UILabel
        @IBOutlet lateinit var userImage: UIImageView
        @IBOutlet lateinit var created: UILabel
        @IBOutlet lateinit var image: UIImageView
        @IBOutlet lateinit var root: UIView

        @IBOutlet lateinit var height: NSLayoutConstraint

        lateinit var vm: PostListViewModel
        lateinit var post: Post

        var position = 0

        @IBAction
        fun rate() {
            val alert = UIAlertController()
            alert.addAction(UIAlertAction("Like".translate(), UIAlertActionStyle.Default) {
                // TODO:
            })
            alert.addAction(UIAlertAction("Dislike".translate(), UIAlertActionStyle.Destructive) {
                // TODO:
            })
            alert.addAction(UIAlertAction("Cancel".translate(), UIAlertActionStyle.Cancel, null))
            StoryboardNavigation.navigationController.presentViewController(alert, true, null)
        }

        @IBAction
        fun play() {
            vm.playClicked(position)
        }

        override fun bind(data: Post?, position: Int) {
            this.position = position

            if (data == null) return
            this.post = data

            replyCountView.text = "" + data.commentCount
            ratingView.text = "" + data.rating

            playButton.isHidden = data.image == null
            playButton.setTitle(getPlayButtonTitle(data), UIControlState.Normal)

            userName.text = post.userName
            created.text = PrettyTime().format(post.created)

            val i = post.image
            if (i == null) {
                // TODO
            } else {
                height.constant = image.frame.width / i.aspect
                loadImage(post.image!!, 300, (300 / i.aspect).toInt(), image)
            }

            loadImage(post.getUserImage2().toImage(), 50, 50, userImage)
            userImage.layer.cornerRadius = userImage.frame.width / 2

            root.layer.cornerRadius = 10.0
            root.layer.setMasksToBounds(true)
            root.layer.borderColor = UIColor.lightGray().cgColor
            root.layer.borderWidth = 1.0
        }

        fun loadImage(image: Image, width: Int, height: Int, iv: UIImageView) {
            iv.alpha = 0.0
            ImageRequest()
                .setUrl(image)
                .setSize(width, height)
                .to(iv) {
                    iv.image = it
                    UIView.animate(0.3) { iv.alpha = 1.0 }
                }
        }

        private fun getPlayButtonTitle(post: Post): String {
            return if (post.image?.isAnimated ?: false) "Play".translate() else "View".translate()
        }
    }
}