package y2k.joyreactor.presenters

import y2k.joyreactor.Tag
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.TagListService

/**
 * Created by y2k on 9/26/15.
 */
class TagListPresenter(
    private val view: TagListPresenter.View,
    private val service: TagListService,
    private val broadcastService: BroadcastService,
    private val lifeCycleService: LifeCycleService) {

    init {
        lifeCycleService.add {
            service
                .getMyTags()
                .subscribeOnMain { view.reloadData(it) }
        }
    }

    fun selectTag(tag: Tag) {
        broadcastService.broadcast(BroadcastService.TagSelected(tag))
    }

    fun selectedFeatured() {
        broadcastService.broadcast(BroadcastService.TagSelected(Tag.makeFeatured()))
    }

    fun selectedFavorite() {
        service
            .getTagForFavorite()
            .subscribe { broadcastService.broadcast(BroadcastService.TagSelected(it)) }
    }

    interface View {

        fun reloadData(tags: List<Tag>)
    }
}