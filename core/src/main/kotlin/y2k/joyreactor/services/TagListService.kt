package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.Tag
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.AddTagRequest
import y2k.joyreactor.services.requests.UserNameRequest
import y2k.joyreactor.services.synchronizers.MyTagFetcher

/**
 * Created by y2k on 11/24/15.
 */
class TagListService(
        private val dataContext: DataContext.Factory,
        private val synchronizer: MyTagFetcher) {

    fun getMyTags(): Observable<List<Tag>> {
        return getFromRepo().mergeWith(
                synchronizer.synchronize().flatMap { getFromRepo() })
    }

    private fun getFromRepo(): Observable<List<Tag>> {
        return dataContext.use { entities -> entities.Tags.filter { it.isMine } }
    }

    fun addTag(tag: String): Observable<Void> {
        return AddTagRequest(tag).request()
    }

    fun getTagForFavorite(): Observable<Tag> {
        return UserNameRequest().request().map { Tag.makeFavorite(it) }
    }
}