package y2k.joyreactor.services.synchronizers

import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.onErrorAsync
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.requests.TagsForUserRequest
import y2k.joyreactor.services.requests.UserNameRequest

/**
 * Created by y2k on 11/25/15.
 */
class MyTagFetcher(
    private val userNameRequest: UserNameRequest,
    private val tagsForUserRequest: TagsForUserRequest,
    private val dataContext: Entities) : Function0<CompletableFuture<*>> {

    override fun invoke(): CompletableFuture<*> {
        return userNameRequest()
            .thenAsync { tagsForUserRequest.request(it) }
            .onErrorAsync { DefaultTagRequest().request() }
            .thenAsync(dataContext) { newTags ->
                val result = Tags.toList()
                    .union(newTags)
                    .union(listOf(Group.makeFeatured()))
                    .distinctBy { it.id }
                    .map { s -> s.copy(isVisible = newTags.any { it.id == s.id }) }

                Tags.clear()
                result.forEach {
                    Tags.add(Group(it, Group.Quality.Good).copy(isVisible = true))
                    Tags.add(Group(it, Group.Quality.Best))
                    Tags.add(Group(it, Group.Quality.All))
                }
            }
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

        fun request(): CompletableFuture<List<Group>> {
            return CompletableFuture.completedFuture(tags)
        }
    }
}