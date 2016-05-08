package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.await
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Group
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

    val tags = property(emptyList<Group>())

    init {
        lifeCycleService.register {
            service
                .getMyTags()
                .await {
                    tags += it
                }
        }
        lifeCycleService.toString()
    }

    fun selectTag(position: Int) {
        val tag = tags.value[position]
        broadcastService.broadcast(BroadcastService.TagSelected(tag))
    }

    fun selectTag(group: Group) {
        broadcastService.broadcast(BroadcastService.TagSelected(group))
    }

    fun selectedFeatured() {
        broadcastService.broadcast(BroadcastService.TagSelected(Group.makeFeatured()))
    }

    fun selectedFavorite() {
        service
            .getTagForFavorite()
            .await { broadcastService.broadcast(BroadcastService.TagSelected(it)) }
    }
}