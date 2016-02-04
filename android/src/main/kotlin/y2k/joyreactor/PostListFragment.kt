package y2k.joyreactor

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import y2k.joyreactor.common.BaseFragment
import y2k.joyreactor.common.ItemDividerDecoration
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.presenters.PostListPresenter

/**
 * Created by y2k on 9/26/15.
 */
class PostListFragment : BaseFragment() {

    lateinit var adapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        view.findViewById(R.id.error).visibility = View.GONE

        val refreshLayout = view.findViewById(R.id.refresher) as SwipeRefreshLayout
        val list = view.findViewById(R.id.list) as RecyclerView
        list.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        list.addItemDecoration(ItemDividerDecoration(list))

        var presenter: PostListPresenter = ServiceLocator.resolve(lifeCycleService,
            object : PostListPresenter.View {

                override fun setBusy(isBusy: Boolean) {
                    refreshLayout.isRefreshing = isBusy
                }

                override fun reloadPosts(posts: List<Post>, divider: Int?) {
                    adapter.reloadData(posts, divider)
                }

                override fun setHasNewPosts(hasNewPosts: Boolean) {
                    (view.findViewById(R.id.apply) as ReloadButton).setVisibility(hasNewPosts)
                }
            })

        adapter = PostAdapter(presenter); list.adapter = adapter
        view.findViewById(R.id.apply).setOnClickListener { presenter.applyNew() }
        refreshLayout.setOnRefreshListener { presenter.reloadFirstPage() }
        return view
    }
}