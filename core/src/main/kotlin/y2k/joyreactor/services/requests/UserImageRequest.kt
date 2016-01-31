package y2k.joyreactor.services.requests

/**
 * Created by y2k on 01/10/15.
 */
class UserImageRequest {

    fun execute(name: String): String? {
        sStorage = IconStorage.get(sStorage, "user.names", "user.icons")

        val id = sStorage!!.getImageId(name)
        return if (id == null) null else "http://img0.joyreactor.cc/pics/avatar/user/" + id
    }

    companion object {

        private var sStorage: IconStorage? = null
    }
}