package compress;

import org.example.compress.VarintGB;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class VarintGBTest {

    @Test
    public void decoding() {
        int testSize = 1000;
        int[] values = new int[testSize];
        // 随机
        for (int i = 0; i < testSize; i++) {
            values[i] = (int) (Math.random() * Math.pow(2, 31));
        }
        VarintGB varintGB = new VarintGB();
        int[] result = varintGB.decoding(varintGB.encoding(values));
        assertArrayEquals(values, result);

        // 顺序
        for (int i = 0; i < testSize; i++) {
            values[i] = i;
        }
        result = varintGB.decoding(varintGB.encoding(values));
        assertArrayEquals(values, result);
        int a = 3 & ~3;
        System.out.println(a);
    }
}