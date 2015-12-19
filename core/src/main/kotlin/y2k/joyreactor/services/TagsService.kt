package y2k.joyreactor.services

import rx.Observable
import rx.functions.Func1
import y2k.joyreactor.Tag
import y2k.joyreactor.services.repository.MyTagQuery
import y2k.joyreactor.services.repository.Repository
import y2k.joyreactor.services.requests.AddTagRequest
import y2k.joyreactor.services.requests.UserNameRequest
import y2k.joyreactor.services.synchronizers.MyTagFetcher

/**
 * Created by y2k on 11/24/15.
 */
class TagsService(
        private val repository: Repository<Tag>,
        private val synchronizer: MyTagFetcher) {

    fun getMyTags(): Observable<List<Tag>> {
        return getFromRepo().mergeWith(
                synchronizer.synchronize().flatMap { getFromRepo() })
    }

    private fun getFromRepo(): Observable<List<Tag>> {
        return repository.queryAsync(MyTagQuery())
    }

    fun addTag(tag: String): Observable<*> {
        return AddTagRequest(tag).request()
    }

    fun getTagForFavorite(): Observable<Tag> {
        return UserNameRequest().request().map { Tag.makeFavorite(it) }
    }
}