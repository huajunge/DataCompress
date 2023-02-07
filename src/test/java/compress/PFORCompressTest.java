package compress;


import org.example.compress.PFORCompress;
import org.example.compress.PFORCompress.*;
import org.example.compress.Simple8b;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class PFORCompressTest {

    @Test
    public void decoding() {
        int testDataSize = 1000;
        int[] values = new int[testDataSize];
        for (int i = 0; i < 1000; i++) {
            values[i] = (int) (Math.random() * Math.pow(2, 20));
        }
        PFORCompress PFORCompress = new PFORCompress(PFOR_TYPE.NEW_PFD);
        int[] result = PFORCompress.decoding(PFORCompress.encoding(values));
        assertArrayEquals(values, result);

        PFORCompress = new PFORCompress(PFOR_TYPE.OPT_PFD);
        result = PFORCompress.decoding(PFORCompress.encoding(values));
        assertArrayEquals(values, result);

        PFORCompress = new PFORCompress(10);
        result = PFORCompress.decoding(PFORCompress.encoding(values));
        assertArrayEquals(values, result);

        PFORCompress = new PFORCompress(PFOR_TYPE.NEW_PFD, new Simple8b());
        result = PFORCompress.decoding(PFORCompress.encoding(values));
        assertArrayEquals(values, result);

        PFORCompress = new PFORCompress(PFOR_TYPE.OPT_PFD, new Simple8b());
        result = PFORCompress.decoding(PFORCompress.encoding(values));
        assertArrayEquals(values, result);

        PFORCompress = new PFORCompress(10, new Simple8b());
        result = PFORCompress.decoding(PFORCompress.encoding(values));
        assertArrayEquals(values, result);

        PFORCompress = new PFORCompress(7, new Simple8b());
        result = PFORCompress.decoding(PFORCompress.encoding(values));
        assertArrayEquals(values, result);
    }
}