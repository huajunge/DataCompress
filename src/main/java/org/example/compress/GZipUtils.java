package org.example.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author : suiyuan
 * @description : GZip压缩工具类
 * @date : Created in 2019-04-12 18:24
 * @modified by :
 **/
public class GZipUtils implements Serializable {

    /***
       * GZip压缩字节数组
       *
       * @param data
       * @return
       */
    public static byte[] gZip(byte[] data) throws IOException {
        byte[] gZipBytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data);
            gzip.finish();
            gzip.close();
            gZipBytes = bos.toByteArray();
        }
        return gZipBytes;
    }

    /***
       * GZip压缩整数数组
       *
       * @param data
       * @return
       */
    public static byte[] gZip(int[] data) throws IOException {
        byte[] gZipBytes;
        byte[] bytesData = new byte[data.length * Integer.SIZE / Byte.SIZE];
        for (int i = 0; i < data.length; i++) {
            NumberUtil.copyLongToBytes(data[i], 4, bytesData, i * 4);
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(bytesData);
            gzip.finish();
            gzip.close();
            gZipBytes = bos.toByteArray();
        }
        return gZipBytes;
    }

    /***
       * GZip解压字节数组
       *
       * @param data
       * @return
       */
    public static byte[] unGZip(byte[] data) throws IOException {
        byte[] unGZipBytes;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             GZIPInputStream gzip = new GZIPInputStream(bis);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int num;
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            unGZipBytes = baos.toByteArray();
            baos.flush();
        }
        return unGZipBytes;
    }
}
