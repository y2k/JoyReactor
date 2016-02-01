package y2k.joyreactor.tv

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v17.leanback.app.VerticalGridFragment
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.ImageCardView
import android.support.v17.leanback.widget.Presenter
import android.support.v17.leanback.widget.VerticalGridPresenter
import android.view.ViewGroup
import y2k.joyreactor.App
import y2k.joyreactor.Post
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.platform.ImageRequest
import y2k.joyreactor.presenters.PostListPresenter

/**
 * Created by y2k on 11/25/15.
 */
class PostsFragment : VerticalGridFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gridPresenter = VerticalGridPresenter()
        gridPresenter.numberOfColumns = 4
        setGridPresenter(gridPresenter)

        val adapter = ArrayObjectAdapter(CardPresenter())
        setAdapter(adapter)

        ServiceLocator.providePostListPresenter(
            object : PostListPresenter.View {

                override fun reloadPosts(posts: List<Post>, divider: Int?) {
                    adapter.clear()
                    adapter.addAll(0, posts)
                }

                override fun setBusy(isBusy: Boolean) {
                    // TODO:
                }

                override fun setHasNewPosts(hasNewPosts: Boolean) {
                    // TODO:
                }
            })
    }

    class CardPresenter : Presenter() {

        override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
            val cardView = ImageCardView(parent.context)
            cardView.isFocusable = true
            cardView.isFocusableInTouchMode = true
            return Presenter.ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
            val v = viewHolder.view as ImageCardView
            val p = item as Post
            v.titleText = p.title
            ImageRequest()
                .setSize(200, 200)
                .setUrl(p.image)
                .to(v) { v.mainImage = RenderDrawable(it, 200, 200) }
        }

        override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        }

        internal class RenderDrawable(private val bitmap: Bitmap?, width: Int, height: Int) : Drawable() {
            private val width: Float
            private val height: Float

            init {
                this.width = width * App.instance.resources.displayMetrics.density
                this.height = height * App.instance.resources.displayMetrics.density
            }

            override fun getIntrinsicWidth(): Int {
                return width.toInt()
            }

            override fun getIntrinsicHeight(): Int {
                return height.toInt()
            }

            override fun draw(canvas: Canvas) {
                if (bitmap != null) {
                    canvas.save()
                    canvas.scale(width / bitmap.width, height / bitmap.height)
                    canvas.drawBitmap(bitmap, 0f, 0f, null)
                    canvas.restore()
                }
            }

            override fun setAlpha(alpha: Int) {
                // ignore
            }

            override fun setColorFilter(colorFilter: ColorFilter?) {
                // ignore
            }

            override fun getOpacity(): Int {
                return 0
            }
        }
    }
}