package y2k.joyreactor

import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import y2k.joyreactor.common.ListCell
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.common.translate
import y2k.joyreactor.model.Comment
import y2k.joyreactor.platform.ImageRequest
import y2k.joyreactor.viewmodel.PostViewModel

/**
 * Created by y2k on 28/09/15.
 */
@CustomClass("PostViewController")
class PostViewController : UIViewController() {

    @IBOutlet lateinit var list: UITableView

    //    lateinit var presenter: PostPresenter
    //    internal var comments: CommentGroup? = null
    //    internal var post: Post? = null

    override fun viewDidLoad() {
        super.viewDidLoad()

        //        UILongPressGestureRecognizer gr = new UILongPressGestureRecognizer();
        //        gr.setMinimumPressDuration(0.5);
        //        gr.addListener(s -> {
        //            if (s.getState() != UIGestureRecognizerState.Ended) return;
        //
        //            UIActionSheet menu = new UIActionSheet();
        //            menu.addButton(Translator.get("Reply"));
        //            menu.setCancelButtonIndex(menu.addButton(Translator.get("Cancel")));
        //            menu.setDelegate(new UIActionSheetDelegateAdapter() {
        //
        //                @Override
        //                public void clicked(UIActionSheet actionSheet, long buttonIndex) {
        //                    if (buttonIndex == 0)
        //                        getNavigationController().pushViewController(
        //                                getStoryboard().instantiateViewController("CreateComment"), true);
        //                }
        //            });
        //            menu.showFrom(getNavigationItem().getRightBarButtonItem(), true);
        //        });
        //        list.addGestureRecognizer(gr);

        val vm = ServiceLocator.resolve<PostViewModel>()

        bindingBuilder {
            // FIXME: вернуть загрузку поста
            tableView(list, vm.comments) {
                cellSelector { "Comment" }
                command { vm.selectComment(it) }
            }
        }

        navigationItem.rightBarButtonItem.setOnClickListener {
            val alert = UIAlertController()
            alert.addAction(UIAlertAction("Add comment".translate(), UIAlertActionStyle.Default) { s ->
                navigationController.pushViewController(
                    storyboard.instantiateViewController("CreateComment"), true)
            })
            alert.addAction(UIAlertAction("Save image to gallery".translate(), UIAlertActionStyle.Default) {
                vm.saveToGallery()
            })
            alert.addAction(UIAlertAction("Open in Safari".translate(), UIAlertActionStyle.Default) {
                vm.openInBrowser()
            })
            alert.addAction(UIAlertAction("Cancel".translate(), UIAlertActionStyle.Cancel, null))
            presentViewController(alert, true, null)
        }

        //        list.setDelegate(CommentDelegate())
        //        list.dataSource = CommentDataSource()
        //        list.rowHeight = UITableView.getAutomaticDimension()
        //        list.estimatedRowHeight = 44.0
        //
        //        presenter = ServiceLocator.resolve(this)
    }

    @CustomClass("CommentCell")
    class CommentCell : ListCell<Comment>() {

        @IBOutlet lateinit var commentText: UILabel
        @IBOutlet lateinit var userImage: UIImageView
        @IBOutlet lateinit var replies: UILabel
        @IBOutlet lateinit var rating: UILabel

        override fun bind(data: Comment, position: Int) {
            commentText.text = data.text

            userImage.layer.cornerRadius = userImage.frame.width / 2
            ImageRequest()
                .setUrl(data.userImageObject.toImage())
                .setSize(userImage.frame.width.toInt(), userImage.frame.height.toInt())
                .to(userImage, { userImage.image = it })

            replies.text = "" + data.replies
            rating.text = "" + data.rating
        }
    }

    //    class CommentDataSource : UITableViewDataSourceAdapter() {
    //        override fun getTitleForHeader(tableView: UITableView?, section: Long): String? {
    //            return super.getTitleForHeader(tableView, section)
    //        }
    //    }

    //    override fun updateComments(comments: CommentGroup) {
    //        this.comments = comments
    //        list.reloadData()
    //    }
    //
    //    override fun updatePostInformation(post: Post) {
    //        this.post = post
    //        list.reloadData()
    //    }
    //
    //    override fun setIsBusy(isBusy: Boolean) {
    //        navigationItem.setHidesBackButton(isBusy, true)
    //        navigationItem.rightBarButtonItem.isEnabled = !isBusy
    //    }
    //
    //    override fun showImageSuccessSavedToGallery() {
    //        // TODO
    //    }
    //
    //    override fun updatePostImage(image: File) {
    //        // TODO:
    //    }
    //
    //    override fun updateImageDownloadProgress(progress: Int, maxProgress: Int) {
    //        // TODO:
    //    }
    //
    //    override fun setEnableCreateComments() {
    //        // TODO:
    //    }
    //
    //    override fun updatePostImages(images: List<Image>) {
    //        // TODO:
    //    }
    //
    //    override fun updateSimilarPosts(similarPosts: List<SimilarPost>) {
    //        // TODO:
    //    }

    //    private inner class CommentDataSource : UITableViewDataSourceAdapter() {
    //
    //        override fun getNumberOfRowsInSection(tableView: UITableView?, section: Long): Long {
    //            return ((if (comments == null) 0 else comments!!.size()) + 1).toLong()
    //        }
    //
    //        override fun getCellForRow(tableView: UITableView?, indexPath: NSIndexPath?): UITableViewCell {
    //            return if (indexPath!!.row == 0)
    //                createHeaderCell(tableView!!)
    //            else
    //                createCommentCell(tableView!!, indexPath)
    //        }
    //
    //        internal fun createHeaderCell(tableView: UITableView): UITableViewCell {
    //            val cell: UITableViewCell
    //            cell = tableView.dequeueReusableCell("Header")
    //            if (post != null) {
    //                val iv = cell.getViewWithTag(1) as UIImageView
    //
    //                val image = post!!.image
    //                if (image == null) {
    //                    // TODO:
    //                } else {
    //                    ImageRequest()
    //                        .setUrl(post!!.image)
    //                        .setSize(300, (300 / image.aspect).toInt())
    //                        .to(iv) { iv.image = it }
    //                }
    //            }
    //            return cell
    //        }
    //
    //        internal fun createCommentCell(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell {
    //            val cell: UITableViewCell
    //            cell = tableView.dequeueReusableCell("Comment")
    //            val item = comments!![indexPath.row - 1]
    //            (cell.getViewWithTag(1) as UILabel).text = item.text
    //
    //            val iv = cell.getViewWithTag(2) as UIImageView
    //            iv.layer.cornerRadius = iv.frame.width / 2
    //            ImageRequest()
    //                .setUrl(item.userImageObject.toImage())
    //                .setSize(iv.frame.width.toInt(), iv.frame.height.toInt())
    //                .to(iv) { iv.image = it }
    //
    //            (cell.getViewWithTag(3) as UILabel).text = "" + item.replies
    //            (cell.getViewWithTag(4) as UILabel).text = "" + item.rating
    //
    //            return cell
    //        }
    //    }
    //
    //    private inner class CommentDelegate : UITableViewDelegateAdapter() {
    //
    //        override fun didSelectRow(tableView: UITableView?, indexPath: NSIndexPath?) {
    //            presenter.selectComment(comments!![indexPath!!.row - 1].id)
    //            tableView!!.deselectRow(indexPath, true)
    //        }
    //    }
}