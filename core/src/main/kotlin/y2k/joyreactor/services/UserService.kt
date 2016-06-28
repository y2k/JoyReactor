package y2k.joyreactor.services

import rx.Completable
import rx.Observable
import rx.Single
import y2k.joyreactor.common.Notifications
import y2k.joyreactor.model.Group
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.requests.AddTagRequest
import y2k.joyreactor.services.requests.UserNameRequest
import y2k.joyreactor.services.synchronizers.MyTagFetcher

/**
 * Created by y2k on 11/24/15.
 */
class UserService(
    private val addTagRequest: AddTagRequest,
    private val dataContext: Entities,
    private val userNameRequest: UserNameRequest,
    private val synchronizer: MyTagFetcher) {

    fun getMyTags(): Pair<Single<List<Group>>, Notifications> {
        synchronizer
            .synchronize()
            .subscribe(
                { it.printStackTrace() },
                { BroadcastService.broadcast(Notifications.Groups) })

        val fromDb = dataContext
            .use {
                Tags.filter("isVisible" to true)
                    .sortedBy { it.title.toLowerCase() }
            }
            .toSingle()

        return fromDb to Notifications.Groups
    }

    fun favoriteTag(tag: String): Completable {
        return addTagRequest.request(tag)
    }

    fun getTagForFavorite(): Observable<Group> {
        return userNameRequest
            .request()
            .map { Group.makeFavorite(it!!) }
    }

    fun makeGroup(base: Group, quality: Group.Quality): Observable<Group> {
        return dataContext.use {
            val group = Group(base, quality)
            val exists = Tags.filter("serverId" to group.serverId).firstOrNull()
            exists ?: Tags.add(group).apply { saveChanges() }
        }
    }
}