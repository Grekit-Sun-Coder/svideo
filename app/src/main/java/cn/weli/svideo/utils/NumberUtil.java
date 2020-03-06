package cn.weli.svideo.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-09-18
 * @see [class/method]
 * @since [1.0.0]
 */
public class NumberUtil {

    /**
     * 格式化视频数量
     *
     * @param size 数量
     * @return 格式化数据
     */
    public static String formatVideoNumber(long size) {
        try {
            BigDecimal value = new BigDecimal(size);
            BigDecimal format = new BigDecimal(10000.0);
            BigDecimal result = value.divide(format).setScale(1, BigDecimal.ROUND_FLOOR);
            String numStr = String.valueOf(result.doubleValue());
            if (numStr.endsWith(".0")) {
                numStr = numStr.substring(0, numStr.indexOf("."));
            }
            return numStr;
        } catch (Exception e) {
            return String.valueOf(size);
        }
    }

    public static String getRandom(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return String.valueOf(s);
    }

    /**
     * 格式化文件，将long转换为文件大小
     **/
    public static String FormetFileSize(long file) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (file < 1024) {
            fileSizeString = df.format((double) file) + "B";
        } else if (file < 1048576) {
            fileSizeString = df.format((double) file / 1024) + "K";
        } else if (file < 1073741824) {
            fileSizeString = df.format((double) file / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) file / 1073741824) + "G";
        }
        if (fileSizeString.equals(".00B")) {
            fileSizeString = "0.00B";
        }
        return fileSizeString;
    }

}
