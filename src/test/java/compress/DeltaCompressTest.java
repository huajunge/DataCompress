package compress;

import org.example.compress.DeltaCompress;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class DeltaCompressTest {

    @Test
    public void decoding() {
        int[] values = new int[1000];
        for (int i = 0; i < 1000; i++) {
            values[i] = (int) (Math.random() * Math.pow(2, 30));
        }
        DeltaCompress deltaCompress = new DeltaCompress(false);
        int[] result = deltaCompress.decoding(deltaCompress.encoding(values));
        assertArrayEquals(values, result);
    }

    @Test
    public void decodingSortedArray() {
        int[] values = new int[1000];
        for (int i = 0; i < 1000; i++) {
            values[i] = i;
        }
        DeltaCompress deltaCompress = new DeltaCompress(true);
        int[] result = deltaCompress.decoding(deltaCompress.encoding(values));
        assertArrayEquals(values, result);
    }
}