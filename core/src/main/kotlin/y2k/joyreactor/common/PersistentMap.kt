package y2k.joyreactor.common

import y2k.joyreactor.platform.Platform
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by y2k on 19/10/15.
 */
class PersistentMap(name: String) {

    private val cookieFile: File
    private val map = ConcurrentHashMap<String, String>()

    init {
        cookieFile = File(Platform.Instance.currentDirectory, name)
        if (cookieFile.exists())
            map.putAll(cookieFile.readLines().groupToPair())
    }

    fun flush() {
        if (map.isEmpty()) return

        val cookie = StringBuilder()
        for (key in map.keys)
            cookie.append(key).append("\n").append(map[key]).append("\n")

        cookieFile.writeText(cookie.toString())
    }

    @Synchronized fun clear() {
        cookieFile.delete()
        map.clear()
    }

    val isEmpty: Boolean
        get() = map.isEmpty()

    fun put(key: String, value: String): PersistentMap {
        map.put(key, value)
        return this
    }

    operator fun get(key: String): String {
        return map[key]!!
    }

    fun keySet(): Set<String> {
        return map.keys
    }
}