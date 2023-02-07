package org.example.compress;



import java.util.Arrays;

/**
 * @author : hehuajun3
 * @description : varint-GB编码。基本思想：将整数分成4个为一组，使用一个byte指示这4个整数每个占用的长度（i.e., 每个整数
 * 的长度用两bit来表示）。
 * e.g., 四个整数：2^15， 2^23, 2^7, 2^31，分别可以用2,3,1,4个字节表示
 * 那么可以用01 10 00 11（一个字节）来指示每个数字所占用的字节数。
 * @date : Created in 2019-08-16 17:11
 * @modified by : Ruiyuan Li
 **/
public class VarintGB implements IIntegerCompress {

    @Override
    public byte[] encoding(int[] rawValues) {
        if (0 == rawValues.length) {
            return null;
        }
        int[] values = Arrays.copyOf(rawValues, rawValues.length);
        // 每个分组中整数的个数
        int groupSize = 4;
        // 最大可能字节数，前面是指示器占用空间，后面是整数最大占用空间，每个整数最大占用4个字节。
        int maxLen = (int) Math.ceil((double) values.length / groupSize) + values.length * 4;
        byte[] result = new byte[maxLen];
        // 每组中开始下标
        int startIndex = 0;
        for (int i = 0; i < values.length; i += groupSize) {
            // 指示器
            byte indicator = 0;
            int currentIndex = startIndex + 1;
            for (int j = i; j < i + groupSize && j < values.length; j++) {
                //0,1,2,3
                int bytesSize = 0;
                if (0 != values[j]) {
                    // 此整数所占用的字节数 - 1
                    bytesSize = IIntegerCompress.getBytesLength(values[j]) - 1;
                }
                indicator |= (bytesSize << (2 * (j - i)));
                NumberUtil.copyLongToBytes(values[j], bytesSize + 1, result, currentIndex);
                currentIndex += bytesSize + 1;
            }
            result[startIndex] = indicator;
            startIndex = currentIndex;
        }
        return Arrays.copyOfRange(result, 0, startIndex);
    }

    @Override
    public int[] decoding(byte[] coding) {
        int[] result = new int[coding.length];
        int resultIndex = 0;
        int index = 0;
        int groupSize = 4;
        while (index < coding.length - 1) {
            byte indicator = coding[index];
            index++;
            for (int j = 0; j < groupSize && index < coding.length; j++) {
                //xx & 00000011，取最后两位
                // 此整数所占用字节数 - 1
                int byteSize = ((indicator >> (j * 2)) & 0x03);
                result[resultIndex++] = NumberUtil.bytesToInt(coding, index, index + byteSize + 1);
                index += byteSize + 1;
            }
        }

        return Arrays.copyOfRange(result, 0, resultIndex);
    }
}
