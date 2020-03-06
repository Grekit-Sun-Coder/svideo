package cn.weli.common.libs;

public class WeliLib {
    static {
        System.loadLibrary("WeliLib");
    }

    /**
     * 加密
     */
    private native String doEncrypt(String str, int type);

    /**
     * 解密
     */
    private native String doSecrypt(String str, int type);


    /**
     * AES加密
     */
    private native String doAESEncrypt(String str, String iv, int type);

    /**
     * AES解密
     */
    private native String doAESDecrypt(String str, String iv, int type);


    private static WeliLib weliLib = null;

    public static WeliLib getInstance() {
        if (weliLib == null) {
            weliLib = new WeliLib();
        }
        return weliLib;
    }

    private WeliLib() {
    }


    /**
     * 执行加密操作
     * type =0时密钥为 U&ce...dCUR（前四位和后四位数）
     * type =1时密钥为 G^*·...RCnq（前四位和后四位数）
     * type =2时密钥为 Miad...TwET（前四位和后四位数）
     * type =3时密钥为 T^.S...yRCnQ（前四位和后四位数）
     */
    public String doTheEncrypt(String str, int type) {
        String result = doEncrypt(str, type);
        return result;
    }

    /**
     * 执行解密操作
     * type =0时密钥为 U&ce...dCUR（前四位和后四位数）
     * type =1时密钥为 G^*·...RCnq（前四位和后四位数）
     * type =2时密钥为 Miad...TwET（前四位和后四位数）
     */
    public String doTheSecrypt(String str, int type) {
        String result = doSecrypt(str, type);
        return result;
    }

    /**
     * 执行AES加密操作
     * type =1时密钥为fdaed...626432 （前六位和后六位数）省钱使用
     * type =2时密钥为9bde40...daed1b （前六位和后六位数）看看使用
     *
     * @param str  要加密的文本
     * @param iv   随机向量
     * @param type 要使用的秘钥组
     */
    public String doTheAESEncrypt(String str, String iv, int type) {
        String result = doAESEncrypt(str, iv, type);
        return result;
    }

    /**
     * 执行AES加密操作
     * type =1时密钥为fdaed...626432 （前六位和后六位数）省钱使用
     * type =2时密钥为9bde40...daed1b （前六位和后六位数）看看使用
     *
     * @param str  要加密的文本
     * @param iv   随机向量
     * @param type 要使用的秘钥组
     */
    public String doTheAESDecrypt(String str, String iv, int type) {
        String result = doAESDecrypt(str, iv, type);
        return result;
    }


}
