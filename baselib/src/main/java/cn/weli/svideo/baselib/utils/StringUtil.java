package cn.weli.svideo.baselib.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串相关工具类 包括：判断字符串是否为null或者长度为0；判断字符串是否为null或者全为空格；
 * null 转为长度为0的字符串； 返回字符串长度； 首字母大写； 首字母小写； 转化为半角字符； 转化为全角字符串
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @see StringUtil
 * @since [1.0.0]
 */

public class StringUtil {

    /**
     * 空字符串
     */
    public static final String EMPTY_STR = "";
    /**
     * 空格字符串
     */
    public static final String SPACE_STR = " ";
    /**
     * 空格字符串
     */
    public static final String BARS_STR = "--";
    /**
     * .
     */
    public static final String POINT_STR = ".";
    /**
     * 字符串
     */
    public static final String LINE_STR = "_";
    /**
     * 换行
     */
    public static final String ENTER_STR = "\n";
    /**
     * 等号
     */
    public static final String EQUALS_STR = "=";
    /**
     * 问好
     */
    public static final String QUESTION_STR = "?";

    private StringUtil() {
        throw new UnsupportedOperationException("can not call the constructor");
    }

    /**
     * 判断字符串是否为null，"null"或者长度为0.
     *
     * @param str 待校验字符串
     * @return {@code true}: 空<br> {@code false}: 不为空
     */
    public static boolean isNull(String str) {
        return str == null || str.length() == 0 || str.trim().equalsIgnoreCase("null");
    }

    /**
     * 比较两个字符串是否相等
     *
     * @param a 字符串1
     * @param b 字符串2
     * @return 返回比较结果
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) {
            return true;
        }
        if ((a == null || isEmpty(a)) && (b == null || isEmpty(b))) {
            return true;
        }
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 处理带有分隔符的字符串转化
     *
     * @param str       被分解的字符串
     * @param separator 分隔符
     * @return 返回值
     */
    public static String[] splitStr(String str, String separator) {
        if (StringUtil.isNull(str)) {
            return null;
        }
        if (StringUtil.isEmpty(separator) || !str.contains(separator)) {
            return new String[]{str};
        }
        return str.split(separator);
    }
    /**
     *  比较两个字符串是否相等
     * @param a 字符串1
     * @param b 字符串2
     * @param mark 分割符
     * @return 返回比较结果
     */
//    public static boolean equals(CharSequence a, CharSequence b, String mark) {
//        if (a == b) {
//            return true;
//        }
//        if ((a == null || isEmpty(a)) && (b == null || isEmpty(b))) {
//            return true;
//        }
//        int length;
//        if (a != null && b != null && (length = a.length()) == b.length()) {
//            if (a instanceof String && b instanceof String) {
//                if(mark == null){
//                    return a.equals(b);
//                }else {
//                    Set<String> a1 = new HashSet<>(Arrays.asList(((String) a).split(mark)));
//                    Set<String> b1 = new HashSet<>(Arrays.asList(((String) b).split(mark)));
//                    return a1.containsAll(b1);
//                }
//            } else {
//                for (int i = 0; i < length; i++) {
//                    if (a.charAt(i) != b.charAt(i)) {
//                        return false;
//                    }
//                }
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 判断字符串是否为null或长度为0.
     *
     * @param string 待校验字符串
     * @return {@code true}: 空<br> {@code false}: 不为空
     */
    public static boolean isEmpty(CharSequence string) {
        return string == null || string.length() == 0;
    }

    /**
     * 过滤null
     * @param string 待校验字符串
     * @return 避免null
     */
    public static String avoidEmpty(String string) {
        if (isEmpty(string)) {
            return EMPTY_STR;
        }
        return string;
    }
    /**
     * 判断字符串是否为null或全为空格.
     *
     * @param string 待校验字符串
     * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
     */
    public static boolean isSpace(String string) {
        return (string == null || string.trim().length() == 0);
    }

    /**
     * null转为长度为0的字符串.
     *
     * @param string 待转字符串
     * @return string为null转为长度为0字符串，否则不改变
     */
    public static String null2Length0(String string) {
        return string == null ? "" : string;
    }

    /**
     * 返回字符串长度.
     *
     * @param string 字符串
     * @return null返回0，其他返回自身长度
     */
    public static int length(CharSequence string) {
        return string == null ? 0 : string.length();
    }

    /**
     * 首字母大写.
     *
     * @param string 待转字符串
     * @return 首字母大写字符串
     */
    public static String upperFirstLetter(String string) {
        if (isEmpty(string) || !Character.isLowerCase(string.charAt(0))) {
            return string;
        }
        return String.valueOf((char) (string.charAt(0) - 32)) + string.substring(1);
    }

    /**
     * 首字母小写.
     *
     * @param string 待转字符串
     * @return 首字母小写字符串
     */
    public static String lowerFirstLetter(String string) {
        if (isEmpty(string) || !Character.isUpperCase(string.charAt(0))) {
            return string;
        }
        return String.valueOf((char) (string.charAt(0) + 32)) + string.substring(1);
    }

    /**
     * 转化为半角字符.
     *
     * @param string 待转字符串
     * @return 半角字符串
     */
    public static String toDBC(String string) {
        if (isEmpty(string)) {
            return string;
        }
        char[] chars = string.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 转化为全角字符.
     *
     * @param string 待转字符串
     * @return 全角字符串
     */
    public static String toSBC(String string) {
        if (isEmpty(string)) {
            return string;
        }
        char[] chars = string.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 判断是否为字母.
     */
    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0;
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1.
     *
     * @param s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static int length(String s) {
        if (s == null) {
            return 0;
        }
        char[] c = s.toCharArray();
        int len = 0;
        for (char aC : c) {
            len++;
            if (!isLetter(aC)) {
                len++;
            }
        }
        return len;
    }


    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为1,英文字符长度为0.5.
     *
     * @param s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static double getLength(String s) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        //进位取整
        return Math.ceil(valueLength);
    }


    /**
     * 按照字符串长度切分字符串为两行.
     */
    public static String[] splitByByteSize(String content, int size) {
        String[] arr = new String[2];
        for (int i = 0; i < size; i++) {
            if (length(content.substring(0, i)) == size || length(
                    content.substring(0, i)) == size - 1) {
                arr[0] = content.substring(0, i);
                arr[1] = getDisplayStr(content.substring(i, content.length() - 1), size);
                break;
            }
        }
        return arr;

    }

    /**
     * 获取字符串的长度.
     */
    public static int getWordCount(String s) {
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        return s.length();
    }

    /**
     * 获得字符串指定的长度的可显示的部分，末尾以...结束.
     */
    public static String getDisplayStr(String s, int displayLength) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        int length = 0;
        int index = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255) {
                length++;
                if (length > displayLength) {
                    index = i;
                    break;
                }
            } else {
                length += 2;
                if (length > displayLength) {
                    index = i;
                    break;
                }
            }
        }
        if (getWordCount(s) > displayLength) {
            return s.substring(0, index).concat("...");
        } else {
            return s;
        }
    }

    /**
     * 判断传入的字符串是否全为数字.
     */
    public static boolean isAllNum(String str) {
        if (isNull(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断传入的字符串是否全为中文汉字.
     */
    public static boolean isAllChinese(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        Pattern p = Pattern.compile("^[\u4e00-\u9fa5|·•]{0,}$");
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是表情.
     */
    public static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (
                codePoint <= 0xFFFD));
    }

    /**
     * String数组转ArrayList.
     */
    public static ArrayList<String> array2List(String[] strArray) {
        ArrayList<String> list = new ArrayList<>();
        for (String str : strArray) {
            list.add(str);
        }
        return list;
    }

    /**
     * 格式化double类型的数字（添加分隔符）保留两位小数.
     *
     * @param number 需要格式化的数 如：12345678.90
     * @return 格式化数字 如：12,345,678.90
     */
    public static String formatDouble(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");//格式化设置
        return decimalFormat.format(number);
    }

    /**
     * 格式化double类型的数字（添加分隔符）且四舍五入取整数.
     *
     * @param number 如：12345678.90
     * @return 如：12,345,679
     */
    public static String formatSeparator(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");//格式化设置
        return decimalFormat.format(number);
    }

    /**
     * 格式化double类型的数字保留两位小数.
     *
     * @param number 如：12345678
     * @return 如：12345678.00
     */
    public static String formatDoubleWithoutSeparator(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");//格式化设置
        return decimalFormat.format(number);
    }

    /**
     * 格式化金额
     * 最新交互规则：接收以分为单位的金额数值，double类型；返回以元为单位String格式金额
     * 处理规则：1,千分位格式；2,整元不显示小数，非整元显示两位小数.
     *
     * @param number 以分为单位的金额数值
     * @return 以元为单元的金额
     */
    public static String formatSeparatorForMoney(double number) {
        return formatDouble(number / 100);
    }

    /**
     * 格式化金额
     * 最新交互规则：接收以分为单位的金额数值，double类型；返回以元为单位String格式金额
     * 处理规则：1,千分位格式；2,整元不显示小数，非整元显示两位小数.
     *
     * @param number 以分为单位的金额数值
     * @return 以元为单元的金额
     */
    public static String formatWithoutSeparatorForMoney(double number) {
        return formatDoubleWithoutSeparator(number / 100);
    }

    /**
     * 检测邮箱地址是否合法.
     *
     * @return true合法 false不合法
     */
    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email.trim())) {
            return false;
        }
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 检测手机号是否合法.
     *
     * @return true合法 false不合法
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber.trim())) {
            return false;
        }
        Pattern p = Pattern.compile("^(1[3-9][0-9]{9})");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }

    /**
     * 对未脱敏手机号码做脱敏处理(隐藏中间4位)不符合规则及返回其本身.
     *
     * @param phone 真实手机号 如：13012345678
     * @return 处理后的手机号 如：130****5678
     */
    public static String formatPhone(String phone) {
        if (phone != null && isAllNum(phone) && phone.length() == 11) {
            return phone.substring(0, 3) + "****" + phone.substring(7, 11);
        } else {
            return phone;
        }
    }

    /**
     * 对姓名做脱敏处理，规则是2-5位汉字，不符合直接返回.
     *
     * @param name 如：张三丰
     * @return 如：**丰
     */
    public static String formatName(String name) {
        StringBuffer buffer = new StringBuffer();
        if (name.length() < 2 || name.length() > 5) {
            return name;
        } else {
            if (name.length() == 2) {
                buffer.append("*").append(name.charAt(name.length() - 1));
                return buffer.toString();
            } else if (name.length() == 3) {
                buffer.append("*").append("*").append(name.charAt(name.length() - 1));
                return buffer.toString();
            } else if (name.length() == 4) {
                buffer.append("*").append("*").append(name.charAt(name.length() - 2)).append(
                        name.charAt(name.length() - 1));
                return buffer.toString();
            } else {
                buffer.append("*").append("*").append("*").append(
                        name.charAt(name.length() - 2)).append(name.charAt(name.length() - 1));
                return buffer.toString();
            }
        }
    }

    /**
     * 判断是否是16进制的颜色字符串.
     *
     * @param colorStr 需要判断的颜色字符串
     * @return 是否是16进制的颜色字符串
     */
    public static boolean isHexadecimalColorStr(String colorStr) {
        String regString = "^#[0-9a-fA-F]{6,8}$";
        return Pattern.matches(regString, colorStr);
    }

    /**
     * 验证码验证
     * 大于等于4位的通过.
     */
    public static boolean isVerificationCode(String str) {
        boolean b = false;
        if (!isNull(str) && isAllNum(str) && str.length() >= 4) {
            b = true;
        }
        return b;
    }

    /**
     * 判断字符串长度是否达到指定长度.
     *
     * @param value  输入字符串长度
     * @param length 指定的长度
     * @return true 符合规则
     */
    public static boolean checkStringLength(String value, int length) {
        return !isNull(value) && value.length() == length;
    }


    /**
     * 计算内容字数，一个汉字=两个英文字母，一个中文标点=两个英文标点
     */
    public static int calculateLength(String c) {
        int len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len++;
            } else {
                len += 2;
            }
        }
        return len;
    }

    public static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }

    /**
     * 折扣数值转换
     * @param discount 折扣
     * @return 转换结果
     */
    public static String formatDiscountNumber(double discount) {
        String discountStr = new DecimalFormat("0.0").format(discount);
        if (discountStr.endsWith(".0")) {
            discountStr = discountStr.substring(0, discountStr.indexOf("."));
        }
        return discountStr;
    }

    /**
     * 读取字符串第i次出现特定符号的位置
     */
    public static int getCharacterPosition(String string, int i, String character) {
        try {
            Matcher slashMatcher = Pattern.compile(character).matcher(string);
            int mIdx = 0;
            while (slashMatcher.find()) {
                mIdx++;
                if (mIdx == i) {
                    break;
                }
            }
            return slashMatcher.start();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 根据错误字段指定字体高亮
     *
     * @return 字符串
     */
    public static SpannableStringBuilder getHighLightText(String text, List<String> keywords, int color) {
        if (isNull(text)) {
            return new SpannableStringBuilder(StringUtil.EMPTY_STR);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        if (keywords == null || keywords.isEmpty()) {
            return builder;
        }
        for (String keyword : keywords) {
            if (!isNull(keyword)) {
                String current = text;
                int base = 0;
                int start;
                do {
                    start = current.indexOf(keyword);
                    int end;
                    if (start >= 0) {
                        end = start + keyword.length();
                        builder.setSpan(new ForegroundColorSpan(color),
                                base + start, base + end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        current = current.substring(end);
                        base += end;
                    }
                } while (start >= 0 && current.length() > 0);
            }
        }
        return builder;
    }

    /**
     * 根据错误字段指定字体加粗
     *
     * @return 字符串
     */
    public static SpannableStringBuilder getBlodText(String text, String keyword) {
        if (isNull(text)) {
            return new SpannableStringBuilder(StringUtil.EMPTY_STR);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        if (!isNull(keyword)) {
            String current = text;
            int base = 0;
            int start;
            do {
                start = current.indexOf(keyword);
                int end;
                if (start >= 0) {
                    end = start + keyword.length();
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            base + start, base + end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    current = current.substring(end);
                    base += end;
                }
            } while (start >= 0 && current.length() > 0);
        }
        return builder;
    }

    /**
     * 根据错误字段是否字体高亮
     *
     * @return 是否字体高亮
     */
    public static boolean hasHighLightTxt(String text, List<String> keywords) {
        if (isNull(text)) {
            return false;
        }
        if (keywords == null || keywords.isEmpty()) {
            return false;
        }
        for (String keyword : keywords) {
            if (!isNull(keyword)) {
                int start;
                start = text.indexOf(keyword);
                if (start >= 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
