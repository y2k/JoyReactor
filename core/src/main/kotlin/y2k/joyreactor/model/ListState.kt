package y2k.joyreactor.model

data class ListState(
    val posts: List<Post>,
    val divider: Int?,
    val hasNew: Boolean)