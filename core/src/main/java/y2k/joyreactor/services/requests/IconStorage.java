package y2k.joyreactor.services.requests;

import y2k.joyreactor.platform.Platform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Created by y2k on 11/23/15.
 */
class IconStorage {

    private int[] names;
    private int[] icons;

    private IconStorage(String names, String icons) {
        this.names = loadIndexes(names);
        this.icons = loadIndexes(icons);
    }

    private int[] loadIndexes(String name) {
        byte[] tmp = Platform.Instance.loadFromBundle(name, "dat");
        IntBuffer intBuffer = ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();

        int[] intArray = new int[intBuffer.remaining()];
        intBuffer.get(intArray);
        return intArray;
    }

    public Integer getImageId(String name) {
        int position = Arrays.binarySearch(names, name.toLowerCase().hashCode());
        return position < 0 ? null : icons[position];
    }

    public static IconStorage get(IconStorage cached, String names, String icons) {
        synchronized (IconStorage.class) {
            return cached != null ? cached : new IconStorage(names, icons);
        }
    }
}