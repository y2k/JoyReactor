package y2k.joyreactor

/**
 * Created by y2k on 10/13/15.
 */
class UserImage(url: String? = null) {

    private val userImage: String

    init {
        if (url == null) userImage = APP_DEFAULT_USER_IMAGE
        else userImage = if (SITE_DEFAULT_USER_IMAGE == url) APP_DEFAULT_USER_IMAGE else url
    }

    fun toImage(): Image {
        return Image(userImage, 0, 0)
    }

    companion object {

        private val SITE_DEFAULT_USER_IMAGE = "http://img0.joyreactor.cc/images/default_avatar.jpeg"
        private val APP_DEFAULT_USER_IMAGE = "https://raw.githubusercontent.com/y2k/JoyReactor/master/ios/resources/Images.xcassets/AppIcon.appiconset/Icon-60%403x.png"

        fun fromUrl(url: String?): UserImage {
            return if (url == null) UserImage() else UserImage(url)
        }
    }
}