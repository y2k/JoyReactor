package y2k.joyreactor

import org.ocpsoft.prettytime.PrettyTime
import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.BaseUIViewController
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.platform.ImageRequest
import y2k.joyreactor.presenters.PostListPresenter

/**
 * Created by y2k on 9/26/15.
 */
@CustomClass("PostListViewController")
class PostListViewController : BaseUIViewController(), PostListPresenter.View {

    @IBOutlet lateinit var list: UITableView
    @IBOutlet lateinit var progressView: UIActivityIndicatorView
    @IBOutlet lateinit var applyButton: UIButton

    lateinit var refresher: UIRefreshControl
    lateinit var presenter: PostListPresenter
    var posts: List<Post>? = null

    override fun viewDidLoad() {
        super.viewDidLoad()
        list.dataSource = PostDataSource()
        list.setDelegate(PostDelegate())

        SideMenu(this, "Menu").attach()

        refresher = UIRefreshControl()
        list.addSubview(refresher)

        navigationItem.rightBarButtonItem.setOnClickListener {
            MenuController(this)
                .addNavigation("Add tag", "AddTag")
                .addNavigation("Profile", "Profile")
                .addNavigation("Messages", "MessageThreads")
                .addCancel("Cancel")
                .present()
        }

        progressView.stopAnimating()

        applyButton.addOnTouchUpInsideListener { sender, e -> presenter.applyNew() }

        presenter = ServiceLocator.resolve(lifeCycleService, this)
    }

    // ==========================================
    // Implement View methods
    // ==========================================

    override fun setBusy(isBusy: Boolean) {
        if (isBusy)
            refresher.beginRefreshing()
        else
            refresher.endRefreshing()
    }

    override fun reloadPosts(posts: List<Post>, divider: Int?) {
        this.posts = posts
        list.reloadData()
    }

    override fun setHasNewPosts(hasNewPosts: Boolean) {
        BottomButton(applyButton).setHidden(!hasNewPosts)
    }

    internal inner class PostDataSource : UITableViewDataSourceAdapter() {

        override fun getNumberOfRowsInSection(tableView: UITableView?, section: Long): Long {
            val count = if (posts == null) 0 else posts!!.size
            return (if (count == 0) 0 else count + 1).toLong()
        }

        override fun getCellForRow(tableView: UITableView?, indexPath: NSIndexPath?): UITableViewCell {
            if (indexPath!!.row == posts!!.size) {
                val cell = tableView!!.dequeueReusableCell("LoadMore") as LoadMoreCell
                cell.presenter = presenter
                return cell
            } else {
                val cell = tableView!!.dequeueReusableCell("Post") as PostCell
                val post = posts!![indexPath.row]
                cell.update(presenter, post)

                (cell.getViewWithTag(3) as UILabel).text = post.userName
                (cell.getViewWithTag(4) as UILabel).text = PrettyTime().format(post.created)

                val image = post.image
                if (image == null) {
                    // TODO
                } else {
                    loadImage(post.image!!, 300,
                        (300 / image.aspect).toInt(),
                        cell.getViewWithTag(1) as UIImageView)
                }

                val userImageView = cell.getViewWithTag(2) as UIImageView
                loadImage(post.getUserImage2().toImage(), 50, 50, userImageView)
                userImageView.layer.cornerRadius = userImageView.frame.width / 2

                val root = cell.getViewWithTag(10)
                root.layer.cornerRadius = 10.0
                root.layer.setMasksToBounds(true)
                root.layer.borderColor = UIColor.lightGray().cgColor
                root.layer.borderWidth = 1.0
                return cell
            }
        }

        fun loadImage(image: Image, width: Int, height: Int, iv: UIImageView) {
            iv.alpha = 0.0
            ImageRequest()
                .setUrl(image)
                .setSize(width, height)
                .to(iv) { data ->
                    iv.image = data
                    UIView.animate(0.3) { iv.alpha = 1.0 }
                }
        }
    }

    internal inner class PostDelegate : UITableViewDelegateAdapter() {

        override fun getHeightForRow(tableView: UITableView?, indexPath: NSIndexPath?): Double {
            if (indexPath!!.row == posts!!.size) return -1.0
            val post = posts!![indexPath.row]
            val image = post.image
            val imageHeight = if (image == null) 0.0 else (tableView!!.frame.width - 16) / image.aspect
            return imageHeight + 66.0 + 16.0
        }

        override fun didEndDecelerating(scrollView: UIScrollView?) {
            if (refresher.isRefreshing) presenter.reloadFirstPage()
        }
    }
}