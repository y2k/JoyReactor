package y2k.joyreactor.services

import y2k.joyreactor.common.BackgroundWorks
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.then
import y2k.joyreactor.model.Group
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.requests.AddTagRequest
import y2k.joyreactor.services.requests.UserNameRequest

/**
 * Created by y2k on 11/24/15.
 */
class UserService(
    private val addTagRequest: AddTagRequest,
    private val entities: Entities,
    private val userNameRequest: UserNameRequest,
    private val synchronize: () -> CompletableFuture<*>,
    private val backgroundWorks: BackgroundWorks) {

    fun getMyTags(): CompletableFuture<List<Group>> {
        return entities
            .useAsync {
                Tags.filter("isVisible" to true)
                    .sortedBy { it.title.toLowerCase() }
            }
    }

    fun favoriteTag(tag: String): CompletableFuture<*> {
        return addTagRequest.request(tag)
    }

    fun getTagForFavorite(): CompletableFuture<Group> {
        return userNameRequest()
            .then { Group.makeFavorite(it) }
    }

    fun makeGroup(base: Group, quality: Group.Quality): CompletableFuture<Group> {
        return entities.use {
            val group = Group(base, quality)
            val exists = Tags.filter("serverId" to group.serverId).firstOrNull()
            exists ?: Tags.add(group).apply { saveChanges() }
        }
    }

    fun syncTagsInBackground(): String {
        val key = "sync-my-tags"
        backgroundWorks.markWorkStarted(key)
        synchronize().thenAccept { backgroundWorks.markWorkFinished(key, it.errorOrNull) }
        return key
    }
}