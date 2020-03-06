package cn.weli.svideo.baselib.utils;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * JXH
 * 2018/9/26
 */
public class MD5Util {
    /**
     * 判断下载的apk文件md5值是否和传入一致
     *
     * @param apkPath 下载文件路径
     * @param apkMd5  接口返回md5
     * @return
     */
    public static boolean isMatchApkMd5(String apkPath, String apkMd5) {
        if (TextUtils.isEmpty(apkMd5)) {
            return true;
        }
        if (TextUtils.isEmpty(apkPath)) {
            return false;
        }
        String md5 = "";
        try {
            FileInputStream fis = new FileInputStream(apkPath);
            md5 = getMD5(file2byte(fis));//之前这里用的是阿里里面的一个方法，这样需要测试一下，加了一个自己写的方法
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return apkMd5.equalsIgnoreCase(md5);
    }


    /**
     * 获取md5字符串
     */
    public static String getMD5(byte[] input) {
        return bytesToHexString(MD5(input));
    }

    /**
     * 转换成字节
     *
     * @param fis
     * @return
     */
    public static byte[] file2byte(FileInputStream fis) {
        byte[] buffer = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * Converts a byte array into a String hexidecimal characters
     * <p/>
     * null returns null
     */
    private static String bytesToHexString(byte[] bytes) {
        if (bytes == null)
            return null;
        String table = "0123456789abcdef";
        StringBuilder ret = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            int b;
            b = 0x0f & (bytes[i] >> 4);
            ret.append(table.charAt(b));
            b = 0x0f & bytes[i];
            ret.append(table.charAt(b));
        }
        return ret.toString();
    }


    /**
     * 计算给定 byte [] 串的 MD5
     */
    private static byte[] MD5(byte[] input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (md != null) {
            md.update(input);
            return md.digest();
        } else
            return null;
    }
}


