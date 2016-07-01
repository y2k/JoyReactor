package y2k.joyreactor.model

import java.io.Serializable

/**
 * Created by y2k on 01/07/16.
 */
class TagList(private val items: List<String> = emptyList()) : Iterable<String>, Serializable {

    override fun iterator(): Iterator<String> = items.iterator()
}