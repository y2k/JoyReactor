package y2k.joyreactor.requests;

/**
 * Created by y2k on 01/10/15.
 */
public class UserImageRequest {

    private static IconStorage sStorage;

    private String name;

    public UserImageRequest(String name) {
        this.name = name;
    }

    public String execute() {
        sStorage = IconStorage.get(sStorage, "user.names", "user.icons");

        Integer id = sStorage.getImageId(name);
        return id == null ? null : "http://img0.joyreactor.cc/pics/avatar/user/" + id;
    }
}