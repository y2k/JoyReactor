package y2k.joyreactor;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by y2k on 11/24/15.
 */
public class TestSerialization {

    @Test
    public void test() throws Exception {
        Subject0 expected = new Subject0();
        expected.f1 = 999;
        expected.f2 = 111;
        expected.f3 = 222;

        ByteArrayOutputStream s = new ByteArrayOutputStream();
        new ObjectOutputStream(s).writeObject(expected);

        ByteArrayInputStream is = new ByteArrayInputStream(s.toByteArray());
        Subject0 actual = (Subject0) new ObjectInputStream(is).readObject();

        assertEquals(expected.f1, actual.f1);
        assertEquals(expected.f2, actual.f2);
        assertEquals(expected.f3, actual.f3);
    }

    public static class Subject0 implements Serializable {

        public int f1;
        protected int f2;
        private int f3;
    }
}
