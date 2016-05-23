package y2k.joyreactor.model

import rx.Observable
import y2k.joyreactor.services.MemoryBuffer
import java.util.*

/**
 * Created by y2k on 07/12/15.
 */
interface CommentGroup : List<Comment> {

    fun getNavigation(comment: Comment): Long
}

class EmptyGroup() : ArrayList<Comment>(), CommentGroup {

    override fun getNavigation(comment: Comment): Long {
        throw UnsupportedOperationException()
    }
}

class RootComments() : ArrayList<Comment>(), CommentGroup {

    override fun getNavigation(comment: Comment): Long {
        return comment.id
    }

    companion object {

        fun create(buffer: MemoryBuffer, postId: Long): Observable<CommentGroup> {
            return Observable.fromCallable {
                val firstLevelComments = HashSet<Long>()
                val comments = buffer.comments
                    .filter { it.postId == postId }
                    .filter {
                        if (it.parentId == 0L) {
                            firstLevelComments.add(it.id)
                            true
                        } else {
                            firstLevelComments.contains(it.parentId)
                        }
                    }
                    .map { it.copy(level = if (firstLevelComments.contains(it.parentId)) 1 else 0) }
                    .toList()

                RootComments().apply { addAll(comments) }
            }
        }
    }
}

class ChildComments() : ArrayList<Comment>(), CommentGroup {

    override fun getNavigation(comment: Comment): Long {
        return if (comment.id == this[0].id) comment.parentId else comment.id
    }

    companion object {

        fun create(buffer: MemoryBuffer, parentCommentId: Long): Observable<CommentGroup> {
            return Observable.fromCallable {
                val parent = buffer.comments
                    .first { it.id == parentCommentId }
                    .copy(level = 0)

                val children = buffer.comments
                    .filter { it.parentId == parentCommentId }
                    .map { it.copy(level = 1) }
                    .toList()

                ChildComments().apply {
                    this.add(parent) // TODO: разобраться почему не работает без this.
                    addAll(children)
                }
            }
        }
    }
}