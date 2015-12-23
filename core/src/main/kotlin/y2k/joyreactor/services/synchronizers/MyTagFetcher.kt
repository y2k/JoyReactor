package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.Image
import y2k.joyreactor.Tag
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.TagsForUserRequest
import y2k.joyreactor.services.requests.UserNameRequest
import java.util.*

/**
 * Created by y2k on 11/25/15.
 */
class MyTagFetcher(private val dataContext: DataContext.Factory) {

    fun synchronize(): Observable<Void> {
        return UserNameRequest()
                .request()
                .flatMap({ username ->
                    if (username == null)
                        DefaultTagRequest().request()
                    else
                        TagsForUserRequest(username).request()
                })
                .flatMap { newTags ->
                    dataContext
                            .usingAction { entities ->
                                val tags = merge(entities.Tags.toList(), newTags)

                                entities.Tags.clear()
                                entities.Tags.addAll(tags)

                                entities.saveChanges()
                            }
                }
    }

    private fun merge(oldTags: List<Tag>, newTags: List<Tag>): List<Tag> {
        val result = ArrayList<Tag>()

        for (s in oldTags) s.isMine = false
        for (s in newTags) s.isMine = true

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
                left.set(left.indexOf(old), tag)
            }
        }
    }

    private fun searchForServerId(tags: List<Tag>, serverId: String?): Tag? {
        for (tag in tags)
            if (serverId == tag.serverId) return tag
        return null
    }

    private class DefaultTagRequest internal constructor() {

        private val tags = ArrayList<Tag>()

        init {
            addTag("Anime", "2851")
            addTag("Красивые картинки", "31505")
            addTag("Игры", "753")

            addTag("Длинные картинки", "2851")
            addTag("hi-res", "2851")

            addTag("Комиксы", "27")
            addTag("Гифки", "116")
            addTag("Песочница", "10891")
            addTag("Geek", "7")
            addTag("Котэ", "1481")
            addTag("Видео", "1243")
            addTag("Story", "227")
        }

        private fun addTag(title: String, tagId: String) {
            val tag = Tag()
            tag.title = title
            tag.image = Image("http://img0.joyreactor.cc/pics/avatar/tag/" + tagId, 0, 0)
            tags.add(tag)
        }

        fun request(): Observable<List<Tag>> {
            return Observable.just<List<Tag>>(tags)
        }
    }
}