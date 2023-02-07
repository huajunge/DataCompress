package compress;


import org.example.compress.DeltaCompress2;
import org.example.compress.VarintGB;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class DeltaCompress2Test {

    @Test
    public void decoding() {
        int[] values = new int[1000];
        for (int i = 0; i < 1000; i++) {
            values[i] = (int) (Math.random() * Math.pow(2, 31));
        }
        DeltaCompress2 deltaCompress = new DeltaCompress2(2);
        int[] result = deltaCompress.decoding(deltaCompress.encoding(values));
        assertArrayEquals(values, result);

        deltaCompress = new DeltaCompress2(new VarintGB());
        result = deltaCompress.decoding(deltaCompress.encoding(values));
        assertArrayEquals(values, result);
    }
}