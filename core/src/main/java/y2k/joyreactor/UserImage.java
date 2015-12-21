package y2k.joyreactor;

/**
 * Created by y2k on 10/13/15.
 */
public class UserImage {

    private static final String SITE_DEFAULT_USER_IMAGE = "http://img0.joyreactor.cc/images/default_avatar.jpeg";
    private static final String APP_DEFAULT_USER_IMAGE = "https://raw.githubusercontent.com/y2k/JoyReactor/master/ios/resources/Images.xcassets/AppIcon.appiconset/Icon-60%403x.png";

    private String userImage;

    UserImage() {
        userImage = APP_DEFAULT_USER_IMAGE;
    }

    public UserImage(String url) {
        this.userImage = SITE_DEFAULT_USER_IMAGE.equals(url)
                ? APP_DEFAULT_USER_IMAGE
                : url;
    }

    public Image toImage() {
        return new Image(userImage, 0, 0);
    }

    public static UserImage fromUrl(String url) {
        return url == null ? new UserImage() : new UserImage(url);
    }
}