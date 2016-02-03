package y2k.joyreactor.tv

import android.os.Bundle
import android.support.v17.leanback.app.HeadersFragment
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.HeaderItem
import android.support.v17.leanback.widget.ListRowPresenter
import android.support.v17.leanback.widget.Row
import y2k.joyreactor.R
import y2k.joyreactor.Tag
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.presenters.TagListPresenter
import y2k.joyreactor.services.LifeCycleService

/**
 * Created by y2k on 11/25/15.
 */
class TagHeadersFragment : HeadersFragment() {

    lateinit var presenter: TagListPresenter
    lateinit var tags: List<Tag>
    val lifeCycleService = LifeCycleService()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.setOnHeaderViewSelectedListener { viewHolder, row ->
            when (row.id) {
                0L -> presenter.selectedFeatured()
                1L -> presenter.selectedFavorite()
                else -> presenter.selectTag(tags[(row.id - 2).toInt()])
            }
        }

        presenter = ServiceLocator.provideTagListPresenter(
            object : TagListPresenter.View {

                override fun reloadData(tags: List<Tag>) {
                    this@TagHeadersFragment.tags = tags
                    val adapter = ArrayObjectAdapter(ListRowPresenter())

                    addRow(adapter, getString(R.string.feed))
                    addRow(adapter, getString(R.string.favorite))
                    for (tag in tags)
                        addRow(adapter, tag.title)

                    setAdapter(adapter)
                }

                private fun addRow(adapter: ArrayObjectAdapter, title: String?) {
                    adapter.add(Row(adapter.size().toLong(), HeaderItem(title)))
                }
            }, lifeCycleService)
    }

    override fun onResume() {
        super.onResume()
        lifeCycleService.activate()
    }

    override fun onPause() {
        super.onPause()
        lifeCycleService.deactivate()
    }
}