package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.binding
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.model.Tag
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.UserService

/**
 * Created by y2k on 3/8/16.
 */
class TagListViewModel(
    private val service: UserService,
    private val broadcastService: BroadcastService,
    private val lifeCycleService: LifeCycleService) {

    val tags = binding(emptyList<Tag>())

    init {
        lifeCycleService.add {
            service
                .getMyTags()
                .subscribeOnMain {
                    tags.value = it
                }
        }
        lifeCycleService.toString()
    }

    fun selectTag(position: Int) {
        val tag = tags.value[position]
        broadcastService.broadcast(BroadcastService.TagSelected(tag))
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
            .subscribeOnMain { broadcastService.broadcast(BroadcastService.TagSelected(it)) }
    }
}