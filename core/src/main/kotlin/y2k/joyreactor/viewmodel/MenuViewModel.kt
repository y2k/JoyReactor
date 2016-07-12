package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.property
import y2k.joyreactor.common.subscribe
import y2k.joyreactor.common.ui
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
    private val lifeCycleService: LifeCycleService) {

    val tags = property(emptyList<Group>())

    init {
        service
            .getMyTags()
            .subscribe(lifeCycleService) { tags += it }
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
        service
            .getTagForFavorite()
            .ui { broadcastService.broadcastType(BroadcastService.TagSelected(it)) }
    }
}