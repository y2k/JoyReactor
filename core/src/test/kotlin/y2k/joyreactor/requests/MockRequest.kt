package y2k.joyreactor.requests

/**
 * Created by y2k on 5/3/16.
 */
object MockRequest {

    fun load(name: String): String {
        val resourceAsStream = javaClass.getResourceAsStream("/mock-responses/$name")
            ?: throw Exception("Resource $name not found")

        return resourceAsStream.use { it.bufferedReader().readText() }
    }
}