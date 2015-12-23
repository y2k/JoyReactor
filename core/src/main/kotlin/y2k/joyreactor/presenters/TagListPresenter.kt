package y2k.joyreactor.presenters

import y2k.joyreactor.Tag
import y2k.joyreactor.common.Messages
import y2k.joyreactor.services.TagListService

/**
 * Created by y2k on 9/26/15.
 */
class TagListPresenter(
        private val view: TagListPresenter.View,
        private val service: TagListService) : Presenter() {

    override fun activate() {
        service.getMyTags().subscribe({ view.reloadData(it) }, { it.printStackTrace() })
    }

    fun selectTag(tag: Tag) {
        Messages.TagSelected(tag).broadcast()
    }

    fun selectedFeatured() {
        Messages.TagSelected(Tag.makeFeatured()).broadcast()
    }

    fun selectedFavorite() {
        service.getTagForFavorite().subscribe { tag -> Messages.TagSelected(tag).broadcast() }
    }

    interface View {

        fun reloadData(tags: List<Tag>)
    }
}