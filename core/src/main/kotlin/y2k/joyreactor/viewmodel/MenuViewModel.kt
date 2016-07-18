package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Group
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.UserService

/**
 * Created by y2k on 3/8/16.
 */
class MenuViewModel(
    private val service: UserService,
    private val broadcastService: BroadcastService,
    scope: LifeCycleService) {

    val tags = property(emptyList<Group>())

    init {
        scope(service.syncTagsInBackground()) {
            async_ {
                tags += await(service.getMyTags())
            }
        }
    }

    fun selectTag(position: Int) {
        val tag = tags.value[position]
        broadcastService.broadcastType(BroadcastService.TagSelected(tag))
    }

    fun selectTag(group: Group) {
        broadcastService.broadcastType(BroadcastService.TagSelected(group))
    }

    fun selectedFeatured() {
        broadcastService.broadcastType(BroadcastService.TagSelected(Group.makeFeatured()))
    }

    fun selectedFavorite() {
        service.getTagForFavorite()
            .thenAccept { broadcastService.broadcastType(BroadcastService.TagSelected(it.result!!)) }
    }
}