package y2k.joyreactor;

/**
 * Created by y2k on 02/10/15.
 */
public abstract class Navigation {

    public static Navigation getInstance() {
        return Platform.Instance.getNavigator();
    }

    public abstract void switchProfileToLogin();

    public abstract void switchLoginToProfile();

    public abstract void closeCreateComment();

    public abstract void closeAddTag();
}