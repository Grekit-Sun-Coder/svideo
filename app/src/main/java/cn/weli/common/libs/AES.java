package cn.weli.common.libs;

import java.nio.charset.Charset;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Author: Alvin Li
 * Date: 02/11/2017
 * Time: 10:47
 */
public class AES {
    private static int AES_128 = 128;
    private static int IV_16 = 16;
    public static String ALGORITHM = "AES";
    private static String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";
    private static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * 生成初始化向量
     *
     * @return 16byte(128bit)的随机值，用16进制编码后的字符串(小写)
     */
    public static String genHexIv() {
        byte[] iv = new byte[IV_16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return Hex.encodeHexString(iv);
    }

    /**
     * 生成密钥
     *
     * @return 16byte(128bit)的随机值，用16进制编码后的字符串(小写)
     * @throws Exception
     */
    public static String genSecretKey(){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(AES_128);
            SecretKey secretKey = keyGenerator.generateKey();
            return Hex.encodeHexString(secretKey.getEncoded());
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 加密 不直接使用使用so库中方法调用
     * @param hexKey 16进制字符串表示的密钥，长度为32
     * @param hexIv 16进制字符串表示的初始化向量，长度为32
     * @param message 原文
     * @return 密文
     */
    private static String encrypt(String hexKey, String hexIv, String message){
        try{
            byte[] key = Hex.decodeHex(hexKey);
            byte[] iv = Hex.decodeHex(hexIv);
            byte[] buffer = encrypt(key, iv, message.getBytes(CHARSET));
            return Hex.encodeHexString(buffer);
        }catch (Exception e){
            e.printStackTrace();
        }
        return message;
    }

    /**
     * 解密 不直接使用使用so库中方法调用
     * @param hexKey 16进制字符串表示的密钥，长度为32
     * @param hexIv 16进制字符串表示的初始化向量，长度为32
     * @param message 密文
     * @return 原文
     */
    private static String decrypt(final String hexKey, final String hexIv, final String message){
        try {
            byte[] buffer = Hex.decodeHex(message);
            byte[] key = Hex.decodeHex(hexKey);
            byte[] iv = Hex.decodeHex(hexIv);
            return new String(decrypt(key,iv,buffer),CHARSET);
        }catch (Exception e){
            e.printStackTrace();
        }
        return message;
    }

    /**
     * 加密
     * @param key 16byte(128bit)的密钥
     * @param iv 16byte(128bit)的初始化向量
     * @param message 原文的字节数组(UTF-8编码)
     * @return 密文的字节数组(hex编码)
     * @throws Exception
     */
    private static byte[] encrypt(final byte[] key, final byte[] iv, final byte[] message) throws Exception {
        return encryptDecrpty(Cipher.ENCRYPT_MODE, key, iv, message);
    }

    /**
     * 解密
     * @param key 16byte(128bit)的密钥
     * @param iv 16byte(128bit)的初始化向量
     * @param message 密文的字节数组(hex编码)
     * @return 原文的字节数组(UTF-8编码)
     * @throws Exception
     */
    private static byte[] decrypt(final byte[] key, final byte[] iv, final byte[] message) throws Exception {
        return encryptDecrpty(Cipher.DECRYPT_MODE, key, iv, message);
    }

    /**
     * 加解密
     * @param mode 加解密的模式
     * @param key 16byte(128bit)的密钥
     * @param iv 16byte(128bit)的初始化向量
     * @param message 原文或密文的字节数组
     * @return 密文或原文的字节数组
     * @throws Exception
     */
    private static byte[] encryptDecrpty(final int mode, final byte[] key, final byte[] iv, final byte[] message) throws Exception {
        final Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
        final SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
        final IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(mode, keySpec, ivSpec);
        return cipher.doFinal(message);
    }

}
