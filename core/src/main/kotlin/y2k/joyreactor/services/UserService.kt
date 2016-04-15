package y2k.joyreactor.services

import rx.Completable
import rx.Observable
import y2k.joyreactor.common.concatAndRepeat
import y2k.joyreactor.model.Group
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.AddTagRequest
import y2k.joyreactor.services.requests.UserNameRequest
import y2k.joyreactor.services.synchronizers.MyTagFetcher

/**
 * Created by y2k on 11/24/15.
 */
class UserService(
    private val addTagRequest: AddTagRequest,
    private val dataContext: DataContext.Factory,
    private val userNameRequest: UserNameRequest,
    private val synchronizer: MyTagFetcher) {

    fun getMyTags(): Observable<List<Group>> {
        return dataContext
            .applyUse {
                Tags.filter("isVisible" to true).sortedBy { it.title.toLowerCase() }
            }
            .concatAndRepeat(synchronizer.synchronize())
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
        return dataContext.applyUse {
            val group = base.copy(id = 0L, quality = quality, isVisible = false)
            val exists = Tags.filter("type" to group.type, "name" to group.name, "quality" to group.name).firstOrNull()
            exists ?: Tags.add(group).apply { saveChanges() }
        }
    }
}