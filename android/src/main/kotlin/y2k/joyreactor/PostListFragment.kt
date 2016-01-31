package y2k.joyreactor

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.ocpsoft.prettytime.PrettyTime
import y2k.joyreactor.common.ComplexViewHolder
import y2k.joyreactor.common.ItemDividerDecoration
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.presenters.PostListPresenter
import java.util.*

/**
 * Created by y2k on 9/26/15.
 */
class PostListFragment : Fragment() {

    private var presenter: PostListPresenter = ServiceLocator.getInstance().providePostListPresenter(ViewImpl())
    private val adapter = PostAdapter(presenter)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_posts, container, false)

        view.findViewById(R.id.error).visibility = View.GONE

        val list = view.findViewById(R.id.list) as RecyclerView
        list.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        list.adapter = adapter
        list.addItemDecoration(ItemDividerDecoration(list))

        view.findViewById(R.id.apply).setOnClickListener { v -> presenter.applyNew() }
        return view
    }

    override fun onResume() {
        super.onResume()
        presenter.activate()
    }

    override fun onPause() {
        super.onPause()
        presenter.deactivate()
    }

    inner class ViewImpl : PostListPresenter.View {

        override fun setBusy(isBusy: Boolean) {
            // TODO:
        }

        override fun reloadPosts(posts: List<Post>, divider: Int?) {
            adapter.reloadData(posts, divider)
        }

        override fun setHasNewPosts(hasNewPosts: Boolean) {
            (view!!.findViewById(R.id.apply) as ReloadButton).setVisibility(hasNewPosts)
        }
    }
}