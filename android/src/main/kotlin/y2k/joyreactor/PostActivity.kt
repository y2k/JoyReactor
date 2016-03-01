package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import y2k.joyreactor.common.*
import y2k.joyreactor.model.Comment
import y2k.joyreactor.viewmodel.PostViewModel

class PostActivity : AppCompatActivity() {

    //    lateinit var presenter: PostPresenter
    //    val adapter = Adapter()
    //    var imagePath: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        supportActionBar.setDisplayHomeAsUpEnabled(true)

        val vm = ServiceLocator.resolve(PostViewModel::class)
        bindingBuilder(this) {
            viewResolver(R.id.list)

            progressImageView(R.id.poster, vm.poster)
            fixedAspectPanel(R.id.posterPanel, vm.posterAspect)

            imagePanel(R.id.images, vm.images)
            visibility(R.id.showMoreImages, vm.images, { it.size > 3 })

            visibility(R.id.error, vm.error)

            textView(R.id.description, vm.description)
            action(vm.postData) {
                // TODO:
            }
            recyclerView(R.id.list, vm.comments) {
                // TODO:
                viewHolder {
                    CommentViewHolder(it)
                }
            }
            visibility(R.id.progress, vm.isBusy)
        }

        //        val list = find<RecyclerView>(R.id.list)
        //        list.adapter = adapter
        //
        //        val createComment = findViewById(R.id.createComment)
        //        createComment.setOnClickListener { presenter.replyToPost() }
        //
        //        presenter = ServiceLocator.resolve(object : PostPresenter.View {
        //
        //            override fun setEnableCreateComments() {
        //                createComment.compatAnimate().scaleX(1f).scaleY(1f).setInterpolator(AccelerateInterpolator())
        //            }
        //
        //            override fun updateComments(comments: CommentGroup) {
        //                adapter.updatePostComments(comments)
        //            }
        //
        //            override fun updatePostInformation(post: Post) {
        //                adapter.updatePostDetails(post)
        //            }
        //
        //            override fun setIsBusy(isBusy: Boolean) {
        //                findViewById(R.id.progress).isVisible = isBusy
        //            }
        //
        //            override fun showImageSuccessSavedToGallery() {
        //                Toast.makeText(applicationContext, R.string.image_saved_to_gallery, Toast.LENGTH_LONG).show()
        //            }
        //
        //            override fun updatePostImages(images: List<Image>) {
        //                adapter.updatePostImages(images)
        //            }
        //
        //            override fun updateSimilarPosts(similarPosts: List<SimilarPost>) {
        //                adapter.updateSimilarPosts(similarPosts)
        //            }
        //
        //            override fun updatePostImage(image: File) {
        //                this@PostActivity.imagePath = image
        //                adapter.notifyItemChanged(0)
        //                val imageProgress = find<ProgressBar>(R.id.imageProgress)
        //                imageProgress.visibility = View.GONE
        //            }
        //
        //            override fun updateImageDownloadProgress(progress: Int, maxProgress: Int) {
        //                val imageProgress = find<ProgressBar>(R.id.imageProgress)
        //                imageProgress.progress = progress
        //                imageProgress.max = maxProgress
        //            }
        //        })
    }

    class CommentViewHolder(parent: ViewGroup) :
        ListViewHolder<Comment>(parent.inflate(R.layout.item_comment)) {

        val rating = itemView.find<TextView>(R.id.rating)
        val text = itemView.find<TextView>(R.id.text)
        val replies = itemView.find<TextView>(R.id.replies)
        val avatar = itemView.find<WebImageView>(R.id.avatar)
        val attachment = itemView.find<WebImageView>(R.id.attachment)

        init {
            //            itemView.findViewById(R.id.action).setOnClickListener { presenter.selectComment(comments.getId(realPosition)) }

            val commentButton = itemView.findViewById(R.id.commentMenu)
            //            commentButton.setOnClickListener {
            //                val menu = PopupMenu(parent.context, commentButton)
            //                menu.setOnMenuItemClickListener { menuItem ->
            //                    if (menuItem.itemId == R.id.reply)
            //                        presenter.replyToComment(comments[realPosition])
            //                    true
            //                }
            //                menu.inflate(R.menu.comment)
            //                menu.show()
            //            }
        }

        override fun update(item: Comment) {
            //            (itemView.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = if (comments.isChild(realPosition)) toPx(64) else toPx(8)

            text.text = item.text
            avatar.setImage(item.userImageObject.toImage())
            rating.text = "" + item.rating
            replies.text = "" + item.replies

            attachment.visibility = if (item.attachmentObject == null) View.GONE else View.VISIBLE
            attachment.setImage(item.attachmentObject)
        }

        private val realPosition: Int
            get() = adapterPosition - 1

        private fun toPx(dip: Int): Int {
            return (dip * itemView.resources.displayMetrics.density).toInt()
        }
    }


    //    override fun onCreateOptionsMenu(menu: Menu): Boolean {
    //        menuInflater.inflate(R.menu.menu_post, menu)
    //        return true
    //    }
    //
    //    override fun onOptionsItemSelected(item: MenuItem): Boolean {
    //        when (item.itemId) {
    //            R.id.reply -> presenter.replyToPost()
    //            R.id.openInBrowser -> presenter.openPostInBrowser()
    //            R.id.saveImageToGallery -> saveImageToGallery()
    //            else -> super.onOptionsItemSelected(item)
    //        }
    //        return true
    //    }

    private fun saveImageToGallery() {
        //        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        //            presenter.saveImageToGallery()
        //        } else {
        //            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        //        }
    }

    //    inner class Adapter : RecyclerView.Adapter<ComplexViewHolder>() {
    //
    //        private var comments: CommentGroup = CommentGroup.Empty
    //        private var post: Post? = null
    //        private var images: List<Image> = emptyList()
    //        private var similarPosts: List<SimilarPost> = emptyList()
    //
    //        init {
    //            setHasStableIds(true)
    //        }
    //
    //        override fun getItemId(position: Int): Long {
    //            return if (position == 0) 0 else comments[position - 1].id
    //        }
    //
    //        override fun getItemViewType(position: Int): Int {
    //            return Math.min(1, position)
    //        }
    //
    //        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplexViewHolder {
    //            if (viewType == 0) return HeaderViewHolder(parent)
    //            return CommentViewHolder(parent)
    //        }
    //
    //        override fun onBindViewHolder(holder: ComplexViewHolder, position: Int) {
    //            holder.bind()
    //        }
    //
    //        override fun getItemCount(): Int {
    //            return comments.size() + 1
    //        }
    //
    //        fun updatePostComments(comments: CommentGroup) {
    //            this.comments = comments
    //            notifyDataSetChanged()
    //        }
    //
    //        fun updatePostDetails(post: Post) {
    //            this.post = post
    //            notifyItemChanged(0)
    //        }
    //
    //        fun updatePostImages(images: List<Image>) {
    //            this.images = images
    //            notifyItemChanged(0)
    //        }
    //
    //        fun updateSimilarPosts(similarPosts: List<SimilarPost>) {
    //            this.similarPosts = similarPosts
    //            notifyItemChanged(0)
    //        }
    //
    //        internal inner class HeaderViewHolder(parent: ViewGroup) :
    //            ComplexViewHolder(parent.inflate(R.layout.layout_post)) {
    //
    //            var image: LargeImageView
    //            var imagePanel: ImagePanel
    //            var similar: ImagePanel
    //            var posterPanel: FixedAspectPanel
    //
    //            init {
    //                image = itemView.findViewById(R.id.image) as LargeImageView
    //                imagePanel = itemView.findViewById(R.id.images) as ImagePanel
    //                similar = itemView.findViewById(R.id.similar) as ImagePanel
    //                posterPanel = itemView.findViewById(R.id.posterPanel) as FixedAspectPanel
    //                itemView.findViewById(R.id.showMoreImages).setOnClickListener { presenter.showMoreImages() }
    //            }
    //
    //            override fun bind() {
    //                if (post != null) {
    //                    val aspect = post?.image?.aspect
    //                    posterPanel.isVisible = aspect != null
    //                    aspect?.let { posterPanel.setAspect(it) }
    //                }
    //
    //                imagePath?.let { image.setImage(it) }
    //
    //                imagePanel.setImages(images)
    //                similar.setImages(toImages())
    //            }
    //
    //            private fun toImages(): List<Image> {
    //                val result = ArrayList<Image>()
    //                for (s in similarPosts)
    //                    s.image?.let { result.add(it) }
    //                return result
    //            }
    //        }
    //
    //        internal inner class CommentViewHolder(parent: ViewGroup) :
    //            ComplexViewHolder(parent.inflate(R.layout.item_comment)) {
    //
    //            var rating: TextView
    //            var text: TextView
    //            var replies: TextView
    //            var avatar: WebImageView
    //            var attachment: WebImageView
    //
    //            init {
    //                rating = itemView.findViewById(R.id.rating) as TextView
    //                text = itemView.findViewById(R.id.text) as TextView
    //                avatar = itemView.findViewById(R.id.avatar) as WebImageView
    //                replies = itemView.findViewById(R.id.replies) as TextView
    //                attachment = itemView.findViewById(R.id.attachment) as WebImageView
    //
    //                itemView.findViewById(R.id.action).setOnClickListener { presenter.selectComment(comments.getId(realPosition)) }
    //
    //                val commentButton = itemView.findViewById(R.id.commentMenu)
    //                commentButton.setOnClickListener {
    //                    val menu = PopupMenu(parent.context, commentButton)
    //                    menu.setOnMenuItemClickListener { menuItem ->
    //                        if (menuItem.itemId == R.id.reply)
    //                            presenter.replyToComment(comments[realPosition])
    //                        true
    //                    }
    //                    menu.inflate(R.menu.comment)
    //                    menu.show()
    //                }
    //            }
    //
    //            override fun bind() {
    //                (itemView.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = if (comments.isChild(realPosition)) toPx(64) else toPx(8)
    //
    //                val c = comments[realPosition]
    //                text.text = c.text
    //                avatar.setImage(c.userImageObject.toImage())
    //                rating.text = "" + c.rating
    //                replies.text = "" + c.replies
    //
    //                attachment.visibility = if (c.attachmentObject == null) View.GONE else View.VISIBLE
    //                attachment.setImage(c.attachmentObject)
    //            }
    //
    //            private val realPosition: Int
    //                get() = adapterPosition - 1
    //
    //            private fun toPx(dip: Int): Int {
    //                return (dip * itemView.resources.displayMetrics.density).toInt()
    //            }
    //        }
    //}
}