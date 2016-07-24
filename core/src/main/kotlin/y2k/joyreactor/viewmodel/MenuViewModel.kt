package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.WorkStatus
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Group
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.Works

/**
 * Created by y2k on 3/8/16.
 */
class MenuViewModel(
    val syncInBackground: (Works, Any) -> Unit,
    val watchForBackground: (Works, Any, (WorkStatus) -> Unit) -> Unit,
    val getMyTags: () -> CompletableFuture<List<Group>>,
    val getTagForFavorite: () -> CompletableFuture<Group>,
    val broadcast: (Any) -> Unit) {

    val tags = property(emptyList<Group>())

    init {
        syncInBackground(Works.syncGroups, false)
        watchForBackground(Works.syncGroups, false) {
            async_ {
                tags += await(getMyTags())
            }
        }
    }

    fun selectTag(group: Group) = broadcast(BroadcastService.TagSelected(group))
    fun selectedFeatured() = broadcast(BroadcastService.TagSelected(Group.makeFeatured()))
    fun selectedFavorite() {
        getTagForFavorite().thenAccept { broadcast(BroadcastService.TagSelected(it.result)) }
    }
}