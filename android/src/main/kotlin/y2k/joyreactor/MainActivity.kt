package y2k.joyreactor

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import y2k.joyreactor.common.*
import y2k.joyreactor.model.Post
import y2k.joyreactor.viewmodel.MainViewModel
import y2k.joyreactor.viewmodel.MenuViewModel
import y2k.joyreactor.widget.TagComponent

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPostList()
        initTagList()
    }

    private fun initPostList() {
        find<RecyclerView>(R.id.list).apply { addItemDecoration(ItemDividerDecoration(this)) }

        val vm = ServiceLocator.resolve<MainViewModel>(lifeCycleService)
        bindingBuilder(this) {
            spinnerTemp(R.id.tabs, vm.quality)
            visibility(R.id.apply, vm.hasNewPosts)
            visibility(R.id.error, vm.isError)
            command(R.id.apply) { vm.applyNew() }
            refreshLayout(R.id.refresher) {
                isRefreshing(vm.isBusy)
                command { vm.reloadFirstPage() }
            }
            recyclerView(R.id.list, vm.posts) {
                itemId { it?.id ?: 0L }
                itemViewType { if (it.value == null) 2 else 1 }
                viewHolderWithType { parent, type ->
                    when (type) {
                        1 -> PostViewHolder(parent.inflate(R.layout.item_feed)).apply {
                            setOnClick(R.id.like) { vm.changeLike(it) }
                            setOnClick(R.id.card, { vm.postClicked(it) })
                            setOnClick(R.id.videoMark) { vm.playClicked(it) }
                            setOnClick(R.id.favorite) { vm.toggleFavorite(it) }
                        }
                        else -> DividerHolder(parent.inflate(R.layout.item_post_divider)).apply {
                            setOnClick(R.id.dividerButton) { vm.loadMore() }
                        }
                    }
                }
            }
            menu(R.menu.main) {
                command(R.id.profile) { vm.openProfile() }
                command(R.id.messages) { vm.openMessages() }
                command(R.id.addTag) { vm.openAddTag() }
                command(R.id.feedback) { vm.openFeedback() }
            }
        }
    }

    private fun initTagList() {
        val vm = ServiceLocator.resolve<MenuViewModel>(lifeCycleService)
        bindingBuilder(this) {
            viewResolver(R.id.listTags)

            command(R.id.selectFeatured, { vm.selectedFeatured() })
            command(R.id.selectFavorite, { vm.selectedFavorite() })

            recyclerView(R.id.listTags, vm.tags) {
                itemId { it.id }
                component { TagComponent(context) { vm.selectTag(it) } }
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)
        ActionBarDrawerToggle(this, findViewById(R.id.drawer_layout) as DrawerLayout,
            toolbar, R.string.app_name, R.string.app_name).syncState()
    }

    class DividerHolder(view: View) : ListViewHolder<Post?>(view) {

        init {
            (itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
        }

        override fun update(item: Post?) {
        }
    }
}