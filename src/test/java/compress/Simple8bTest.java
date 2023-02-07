package compress;

import org.example.compress.Simple8b;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class Simple8bTest {

    @Test
    public void decoding() {
        int[] values = new int[65535];
        for (int i = 0; i < 65535; i++) {
            values[i] = (int) (Math.random() * Math.pow(2, 15));
        }
        Simple8b simple8b = new Simple8b();
        int[] result = simple8b.decoding(simple8b.encoding(values));
        assertArrayEquals(values, result);
    }
}