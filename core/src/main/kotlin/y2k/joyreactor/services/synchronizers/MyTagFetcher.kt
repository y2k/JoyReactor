package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.model.Image
import y2k.joyreactor.model.Tag
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.TagsForUserRequest
import y2k.joyreactor.services.requests.UserNameRequest
import java.util.*

/**
 * Created by y2k on 11/25/15.
 */
class MyTagFetcher(private val dataContext: DataContext.Factory) {

    fun synchronize(): Observable<Unit> {
        return UserNameRequest()
            .request()
            .flatMap {
                if (it == null) DefaultTagRequest().request()
                else TagsForUserRequest(it).request()
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

    private fun merge(oldTags: List<Tag>, newTags: List<Tag>): List<Tag> {
        val result = ArrayList<Tag>()

        for (s in oldTags) s.isVisible = false
        for (s in newTags) s.isVisible = true

        result.addAll(oldTags)
        addOrReplaceAll(result, newTags)

        return result
    }

    private fun addOrReplaceAll(left: MutableList<Tag>, right: List<Tag>) {
        for (tag in right) {
            val old = searchForServerId(left, tag.serverId)
            if (old == null) {
                left.add(tag)
            } else {
                tag.id = old.id
                left[left.indexOf(old)] = tag
            }
        }
    }

    private fun searchForServerId(tags: List<Tag>, serverId: String?): Tag? {
        for (tag in tags)
            if (serverId == tag.serverId) return tag
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

        private fun makeTag(title: String, tagId: String): Tag {
            return Tag(title, title, false, Image("http://img0.joyreactor.cc/pics/avatar/tag/" + tagId))
        }

        fun request(): Observable<List<Tag>> {
            return Observable.just(tags)
        }
    }
}