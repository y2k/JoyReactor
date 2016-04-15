package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.TagsForUserRequest
import y2k.joyreactor.services.requests.UserNameRequest
import java.util.*

/**
 * Created by y2k on 11/25/15.
 */
class MyTagFetcher(
    private val userNameRequest: UserNameRequest,
    private val tagsForUserRequest: TagsForUserRequest,
    private val dataContext: DataContext.Factory) {

    fun synchronize(): Observable<Unit> {
        return userNameRequest
            .request()
            .flatMap {
                if (it == null) DefaultTagRequest().request()
                else tagsForUserRequest.request(it)
            }
            .flatMap { newTags ->
                dataContext.use { entities ->
                    val tags = merge(entities.Tags.toList(), newTags)

                    entities.Tags.clear()
                    tags.forEach { entities.Tags.add(it) }

                    entities.saveChanges()
                }
            }
    }

    private fun merge(oldTags: List<Group>, newGroups: List<Group>): List<Group> {
        return addOrReplaceAll(
            oldTags.map { it.copy(isVisible = false) },
            newGroups.map { it.copy(isVisible = true) })
    }

    private fun addOrReplaceAll(left: List<Group>, right: List<Group>): List<Group> {
        val result = ArrayList<Group>()
        for (tag in right) {
            val old = searchForServerId(left, tag.serverId2)
            result.add(if (old == null) tag else tag.identify(old.id))
        }
        return result
    }

    private fun searchForServerId(groups: List<Group>, serverId: String?): Group? {
        for (tag in groups)
            if (serverId == tag.serverId2) return tag
        return null
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