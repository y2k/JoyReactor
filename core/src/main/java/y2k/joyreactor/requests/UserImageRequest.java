package y2k.joyreactor.requests;

import y2k.joyreactor.Platform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Created by y2k on 01/10/15.
 */
public class UserImageRequest {

    private String name;

    public UserImageRequest(String name) {
        this.name = name;
    }

    public String execute() {
        Integer id = UserImageStorage.getInstance().getImageId(name);
        return id == null ? null : "http://img0.joyreactor.cc/pics/avatar/user/" + id;
    }

    private static class UserImageStorage {

        private static UserImageStorage sInstance;

        public static UserImageStorage getInstance() {
            synchronized (UserImageStorage.class) {
                if (sInstance == null)
                    sInstance = new UserImageStorage();
                return sInstance;
            }
        }

        private int[] names = loadIndexes("user.names");
        private int[] icons = loadIndexes("user.icons");

        private int[] loadIndexes(String name) {
            byte[] tmp = Platform.Instance.loadFromBundle(name, "dat");
            IntBuffer intBuffer = ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();

            int[] intArray = new int[intBuffer.remaining()];
            intBuffer.get(intArray);
            return intArray;
        }

        public Integer getImageId(String name) {
            int position = Arrays.binarySearch(names, name.hashCode());
            return position < 0 ? null : icons[position];
        }
    }
}