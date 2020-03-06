package cn.weli.common.libs;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * @author ljg
 * 该类为辅助类，不可删除；代码中不可修改、调用该类中的任何方法
 */
public class DES {
    /** 指定加密算法为DESede */
    private static final String ALGORITHM = "DESede";
    Cipher cipher_en = null;
    Cipher cipher_de = null;

    /**禁止在程序中再调用该方法(该类服务于Lib)，请调用Lib  20130701*/
    private DES(String deskey) {
        try {
            // DES算法要求有一个可信任的随机数
            SecureRandom sr = new SecureRandom();
            // 从原始密匙数据创建一个DESKeySpec对象
            DESedeKeySpec dks = new DESedeKeySpec(deskey.getBytes("utf-8"));
            // 创建密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey key = keyFactory.generateSecret(dks);
            // Cipher对象实际完成加密操作
            cipher_en = Cipher.getInstance(ALGORITHM);
            // 用密匙初始化Cipher对象
            cipher_en.init(Cipher.ENCRYPT_MODE, key, sr);
            cipher_de = Cipher.getInstance(ALGORITHM);
            cipher_de.init(Cipher.DECRYPT_MODE, key, sr);
        } catch (Exception e) {

        }
    }
    /** 加密方法*/
    private String encrypt(String str){
        try{
            // 现在，获取数据并加密
            byte data[] = str.getBytes("utf-8");
            // 正式执行加密操作
            byte[] encryptedData = cipher_en.doFinal(data);
            return byte2hex(encryptedData);
        }catch(Exception e){
            e.printStackTrace();
        }
        return str;
    }

    /*** 解密方法*/
    private String decrypt(String encryptedData){
        try{
            // 正式执行解密操作
            byte decryptedData[] = cipher_de.doFinal(hex2byte(encryptedData));
            return new String(decryptedData, "utf-8");
        }catch(Exception e){
            e.printStackTrace();
        }
        return encryptedData;
    }
    private String byte2hex(byte[] b) {
        StringBuffer sb = new StringBuffer();
        String tmp = "";
        for (int i = 0; i < b.length; i++) {
            tmp = Integer.toHexString(b[i] & 0XFF);
            if (tmp.length() == 1) {
                sb.append("0" + tmp);
            } else {
                sb.append(tmp);
            }
        }
        return sb.toString();
    }
    private byte[] hex2byte(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1) {
            return null;
        }
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }
    private static String getSignInfo(Context ctx) {
        try {
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return getMD5(sign.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private static String getMD5(byte[] source) {
        String s = null;
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };// 用来将字节转换成16进制表示的字符
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16
            // 进制需要 32 个字符
            int k = 0;// 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16
                // 进制字符的转换
                byte byte0 = tmp[i];// 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>
                // 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换
            }
            s = new String(str);// 换后的结果转换为字符串
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

}
