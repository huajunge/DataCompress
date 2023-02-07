package compress;


import org.example.compress.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : hehuajun3
 * @description :
 * @date : Created in 2019-08-22 11:07
 * @modified by :
 **/
public class CompressPerformanceTest {
    @Test
    public void performance() throws IOException {
        int testDataSize = 1000000;
        int[] unsortedArray = new int[testDataSize];
        long maxBytesSize = testDataSize * Integer.SIZE / Byte.SIZE;
        for (int i = 0; i < testDataSize; i++) {
            unsortedArray[i] = (int) (Math.random() * Math.pow(2, 20));
        }
        int[] sortedArray = new int[testDataSize];
        for (int i = 0; i < testDataSize; i++) {
            sortedArray[i] = i;
        }
        Map<String, IIntegerCompress> compressMap = new HashMap<>();
        initUnSortedCompressMap(compressMap);
        Map<String, IIntegerCompress> sortCompressMap = new HashMap<>();
        initSortedCompressMap(sortCompressMap);

        Map<String, CompressTimeRecord> unSortedResult = new HashMap<>();
        for (Map.Entry<String, IIntegerCompress> integerCompressEntry : compressMap.entrySet()) {
            doCompressAndUnCompress(unsortedArray, maxBytesSize, unSortedResult, integerCompressEntry);
        }

        Map<String, CompressTimeRecord> sortedResult = new HashMap<>();
        for (Map.Entry<String, IIntegerCompress> integerCompressEntry : compressMap.entrySet()) {
            doCompressAndUnCompress(sortedArray, maxBytesSize, sortedResult, integerCompressEntry);
        }

        for (Map.Entry<String, IIntegerCompress> integerCompressEntry : sortCompressMap.entrySet()) {
            doCompressAndUnCompress(sortedArray, maxBytesSize, sortedResult, integerCompressEntry);
        }
        CompressTimeRecord zipRecord = new CompressTimeRecord();
        long t = System.currentTimeMillis();
        byte[] bytes = GZipUtils.gZip(unsortedArray);
        zipRecord.setZipEncodingTime(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        GZipUtils.unGZip(bytes);
        zipRecord.setUnZipDecodingTime(System.currentTimeMillis() - t);
        zipRecord.setZippedTotalTime(zipRecord.getZipEncodingTime() + zipRecord.getUnZipDecodingTime());
        zipRecord.setZippedCodingRatio(bytes.length * 1.0 / maxBytesSize);
        unSortedResult.put("onlyGZip", zipRecord);
        System.out.println("unsorted:");
        for (Map.Entry<String, CompressTimeRecord> result : unSortedResult.entrySet()) {
            CompressTimeRecord timeRecord = result.getValue();
            System.out.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", result.getKey(), timeRecord.getEncodingTime(),
                    timeRecord.getDecodingTime(), timeRecord.getTotalTime(), timeRecord.getZipEncodingTime(),
                    timeRecord.getUnZipDecodingTime(), timeRecord.getZippedTotalTime(), timeRecord.getCodingRatio(), timeRecord.getZippedCodingRatio()));
        }
        System.out.println("---------------");
        System.out.println("sorted:");
        for (Map.Entry<String, CompressTimeRecord> result : sortedResult.entrySet()) {
            CompressTimeRecord timeRecord = result.getValue();
            System.out.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", result.getKey(), timeRecord.getEncodingTime(),
                    timeRecord.getDecodingTime(), timeRecord.getTotalTime(), timeRecord.getZipEncodingTime(),
                    timeRecord.getUnZipDecodingTime(), timeRecord.getZippedTotalTime(), timeRecord.getCodingRatio(), timeRecord.getZippedCodingRatio()));
        }
    }

    private void doCompressAndUnCompress(int[] unsortedArray, long maxBytesSize, final Map<String, CompressTimeRecord> unSortedResult, Map.Entry<String, IIntegerCompress> integerCompressEntry) throws IOException {
        String name = integerCompressEntry.getKey();
        CompressTimeRecord timeRecord = new CompressTimeRecord();
        IIntegerCompress integerCompress = integerCompressEntry.getValue();
        long time = System.currentTimeMillis();
        byte[] coding = integerCompress.encoding(unsortedArray);
        timeRecord.setEncodingTime(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        integerCompress.decoding(coding);
        timeRecord.setDecodingTime(System.currentTimeMillis() - time);
        timeRecord.setTotalTime(timeRecord.getEncodingTime() + timeRecord.getDecodingTime());
        time = System.currentTimeMillis();
        byte[] zippedCoding = GZipUtils.gZip(coding);
        timeRecord.setZipEncodingTime(System.currentTimeMillis() - time + timeRecord.getEncodingTime());
        time = System.currentTimeMillis();
        GZipUtils.unGZip(zippedCoding);
        timeRecord.setUnZipDecodingTime(System.currentTimeMillis() - time + timeRecord.getDecodingTime());
        timeRecord.setZippedTotalTime(timeRecord.getZipEncodingTime() + timeRecord.getUnZipDecodingTime());

        double ratio = coding.length * 1.0 / maxBytesSize;
        timeRecord.setCodingRatio(ratio);
        ratio = zippedCoding.length * 1.0 / maxBytesSize;
        timeRecord.setZippedCodingRatio(ratio);
        unSortedResult.put(name, timeRecord);
    }

    private void initSortedCompressMap(Map<String, IIntegerCompress> sortCompressMap) {
        sortCompressMap.put("Delta1-sorted-VGB", new DeltaCompress(true));
        sortCompressMap.put("Delta1-sorted-Simple8b", new DeltaCompress(true));
        sortCompressMap.put("Delta1-sorted-PFOR_Simple8b_NEW_PFD", new DeltaCompress(true, new PFORCompress(PFORCompress.PFOR_TYPE.NEW_PFD, new Simple8b())));
        sortCompressMap.put("Delta1-sorted-PFOR_Simple8b_OPT_PFD", new DeltaCompress(true, new PFORCompress(PFORCompress.PFOR_TYPE.OPT_PFD, new Simple8b())));
    }

    private void initUnSortedCompressMap(final Map<String, IIntegerCompress> compressMap) {
        compressMap.put("V-GB", new VarintGB());
        compressMap.put("Simple-8b", new Simple8b());
        //PFOR-VGB
        compressMap.put("PFOR_VGB_NEW_PFD", new PFORCompress(PFORCompress.PFOR_TYPE.NEW_PFD));
        compressMap.put("PFOR_VGB_OPT_PFD", new PFORCompress(PFORCompress.PFOR_TYPE.OPT_PFD));
        compressMap.put("PFOR_VGB_3-bits", new PFORCompress(3));
        compressMap.put("PFOR_VGB_4-bits", new PFORCompress(4));
        compressMap.put("PFOR_VGB_5-bits", new PFORCompress(5));
        compressMap.put("PFOR_VGB_6-bits", new PFORCompress(6));
        compressMap.put("PFOR_VGB_7-bits", new PFORCompress(7));
        compressMap.put("PFOR_VGB_8-bits", new PFORCompress(8));
        compressMap.put("PFOR_VGB_9-bits", new PFORCompress(9));
        compressMap.put("PFOR_VGB_10-bits", new PFORCompress(10));
        compressMap.put("PFOR_VGB_15-bits", new PFORCompress(15));
        compressMap.put("PFOR_VGB_20-bits", new PFORCompress(20));
        //PFOR-Simple8b
        compressMap.put("PFOR_Simple8b_NEW_PFD", new PFORCompress(PFORCompress.PFOR_TYPE.NEW_PFD, new Simple8b()));
        compressMap.put("PFOR_Simple8b_OPT_PFD", new PFORCompress(PFORCompress.PFOR_TYPE.OPT_PFD, new Simple8b()));
        compressMap.put("PFOR_Simple8b_3-bits", new PFORCompress(3, new Simple8b()));
        compressMap.put("PFOR_Simple8b_4-bits", new PFORCompress(4, new Simple8b()));
        compressMap.put("PFOR_Simple8b_5-bits", new PFORCompress(5, new Simple8b()));
        compressMap.put("PFOR_Simple8b_6-bits", new PFORCompress(6, new Simple8b()));
        compressMap.put("PFOR_Simple8b_7-bits", new PFORCompress(7, new Simple8b()));
        compressMap.put("PFOR_Simple8b_8-bits", new PFORCompress(8, new Simple8b()));
        compressMap.put("PFOR_Simple8b_9-bits", new PFORCompress(9, new Simple8b()));
        compressMap.put("PFOR_Simple8b_10-bits", new PFORCompress(10, new Simple8b()));
        compressMap.put("PFOR_Simple8b_15-bits", new PFORCompress(15, new Simple8b()));
        compressMap.put("PFOR_Simple8b_20-bits", new PFORCompress(20, new Simple8b()));
        //Delta1
        compressMap.put("Delta1-unsorted-VGB", new DeltaCompress(false));
        compressMap.put("Delta1-unsorted-Simple8b", new DeltaCompress(false));
        compressMap.put("Delta1-unsorted-PFOR_Simple8b_NEW_PFD", new DeltaCompress(false, new PFORCompress(PFORCompress.PFOR_TYPE.NEW_PFD, new Simple8b())));
        compressMap.put("Delta1-unsorted-PFOR_Simple8b_OPT_PFD", new DeltaCompress(false, new PFORCompress(PFORCompress.PFOR_TYPE.OPT_PFD, new Simple8b())));
        //Delta2
        compressMap.put("Delta2-VGB", new DeltaCompress2(new VarintGB()));
        compressMap.put("Delta2-1-byte", new DeltaCompress2(1));
        compressMap.put("Delta2-2-bytes", new DeltaCompress2(2));
        compressMap.put("Delta2-3-bytes", new DeltaCompress2(3));
        compressMap.put("Delta2-4-bytes", new DeltaCompress2(4));
    }

    class CompressTimeRecord {
        long encodingTime;
        long decodingTime;
        long totalTime;
        long zipEncodingTime;
        long unZipDecodingTime;
        long zippedTotalTime;
        double codingRatio;
        double zippedCodingRatio;

        long getEncodingTime() {
            return encodingTime;
        }

        void setEncodingTime(long encodingTime) {
            this.encodingTime = encodingTime;
        }

        long getDecodingTime() {
            return decodingTime;
        }

        void setDecodingTime(long decodingTime) {
            this.decodingTime = decodingTime;
        }

        long getTotalTime() {
            return totalTime;
        }

        void setTotalTime(long totalTime) {
            this.totalTime = totalTime;
        }

        long getZipEncodingTime() {
            return zipEncodingTime;
        }

        void setZipEncodingTime(long zipEncodingTime) {
            this.zipEncodingTime = zipEncodingTime;
        }

        long getUnZipDecodingTime() {
            return unZipDecodingTime;
        }

        void setUnZipDecodingTime(long unZipDecodingTime) {
            this.unZipDecodingTime = unZipDecodingTime;
        }

        long getZippedTotalTime() {
            return zippedTotalTime;
        }

        void setZippedTotalTime(long zippedTotalTime) {
            this.zippedTotalTime = zippedTotalTime;
        }

        double getCodingRatio() {
            return codingRatio;
        }

        void setCodingRatio(double codingRatio) {
            this.codingRatio = codingRatio;
        }

        double getZippedCodingRatio() {
            return zippedCodingRatio;
        }

        void setZippedCodingRatio(double zippedCodingRatio) {
            this.zippedCodingRatio = zippedCodingRatio;
        }
    }
}
