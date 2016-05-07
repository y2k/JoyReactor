package y2k.joyreactor.services

import y2k.joyreactor.model.Post

data class ListState(val posts: List<Post>, val divider: Int?, val hasNew: Boolean)