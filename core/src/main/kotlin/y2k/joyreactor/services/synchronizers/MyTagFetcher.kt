package y2k.joyreactor.services.synchronizers

import rx.Completable
import rx.Observable
import y2k.joyreactor.common.mapDatabase
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.TagsForUserRequest
import y2k.joyreactor.services.requests.UserNameRequest

/**
 * Created by y2k on 11/25/15.
 */
class MyTagFetcher(
    private val userNameRequest: UserNameRequest,
    private val tagsForUserRequest: TagsForUserRequest,
    private val dataContext: DataContext.Factory) {

    fun synchronize(): Completable {
        return userNameRequest
            .request()
            .flatMap {
                if (it == null) DefaultTagRequest().request()
                else tagsForUserRequest.request(it)
            }
            .mapDatabase(dataContext) { newTags ->
                val old = Tags.toList().map { it.copy(isVisible = false) }
                val new = newTags.map { it.copy(isVisible = true) }
                val result = old.union(new).distinctBy { it.serverId }

                Tags.clear()
                result.forEach { Tags.add(it) }
            }
            .toCompletable()
    }

    private class DefaultTagRequest() {

        private val tags = listOf(
            makeTag("Anime", "2851"),
            makeTag("Красивые картинки", "31505"),
            makeTag("Игры", "753"),

            makeTag("Длинные картинки", "2851"),
            makeTag("hi-res", "2851"),

            makeTag("Комиксы", "27"),
            makeTag("Гифки", "116"),
            makeTag("Песочница", "10891"),
            makeTag("Geek", "7"),
            makeTag("Котэ", "1481"),
            makeTag("Видео", "1243"),
            makeTag("Story", "227"))

        private fun makeTag(title: String, tagId: String): Group {
            return Group.makeTag(title, Image("http://img0.joyreactor.cc/pics/avatar/tag/" + tagId))
        }

        fun request(): Observable<List<Group>> {
            return Observable.just(tags)
        }
    }
}