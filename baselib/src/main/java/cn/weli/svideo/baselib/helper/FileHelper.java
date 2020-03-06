package cn.weli.svideo.baselib.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import cn.etouch.logger.Logger;
import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * File 帮助类 + 对文件系统的一些操作，包括文件的读写操作,主要是完成下载中的保存下载文件的功能
 *
 * @author jianglei
 * @version [1.0.0]
 */
public class FileHelper {

    /**
     * App临时文件路径.
     */
    private static final String APP_PATH = "/wlvideo/tmp/";

    /**
     * App更新文件路径.
     */
    public static final String APK_PATH = "/wlvideo/apk/";

    /**
     * sdcard根路径.
     */
    public static String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * 图片缓存路径.
     */
    public static final String TEMP_PATH = SD_PATH + APP_PATH;

    public static final String TEMP_DIR = Environment.getExternalStorageDirectory().getPath() + "/system/";

    private static Context sContext;

    /**
     * 注意在Application初始化的时候赋值，否则可能会导致内存泄漏
     */
    public static void init(Context context) {
        sContext = context;
    }

    /**
     * 从Assert中的.properties文件中获取Urls字符串
     *
     * @param filename 读取数据的文件名称
     * @param porperty 读取数据的属性
     * @return Urls结果字符串，如果处理失败，返回""空字符串
     */
    public static String getUrls(String filename, String porperty) {
        String result = "";
        if (sContext == null || filename == null || filename.isEmpty() || porperty == null
                || porperty.isEmpty()) {
            return result;
        }
        Properties props = new Properties();
        try {
            BufferedInputStream bi = new BufferedInputStream(sContext.getAssets().open(filename));
            props.load(bi);
            if (props.containsKey(porperty)) {
                result = props.getProperty(porperty);
            }
            bi.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从Raw中的filename文件中获取Urls字符串.
     *
     * @param filename 读取数据的文件名称
     * @param porperty 读取数据的属性
     * @return Urls结果字符串，如果处理失败，返回""空字符串
     */
    public static String getUrls(int filename, String porperty) {
        String result = "";
        if (sContext == null || porperty == null || porperty.isEmpty()) {
            return result;
        }
        Properties props = new Properties();
        try {
            BufferedInputStream bi = new BufferedInputStream(
                    sContext.getResources().openRawResource(filename));
            props.load(bi);
            if (props.containsKey(porperty)) {
                result = props.getProperty(porperty);
            }
            bi.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从Raw中的filename文件中获取Urls字符串.
     *
     * @param props    Properties类，加载Properties文件
     * @param property 读取数据的属性
     */
    public static String getUrls(Properties props, String property) {
        String result = "";
        if (props == null || property == null || property.isEmpty()) {
            return result;
        }

        if (props.containsKey(property)) {
            result = props.getProperty(property);
        }

        return result;
    }

    /**
     * <isFileExist/>
     * 是否存在此文件.
     *
     * @param file 判断是否存在的文件
     * @return 存在返回true，否则返回false
     **/
    public static boolean isFileExist(final File file) {
        boolean isExist = false;
        // 在无SD卡时file会为空
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            isExist = true;
        } else {
            isExist = false;
        }
        return isExist;
    }

    /**
     * 是否下载成功.
     *
     * @param url 头像地址
     * @return 是否成功下载头像
     */
    public static boolean isHeadImgDownloadSuccess(String url, String path) {
        if (StringUtil.isNull(url)) {
            return false;
        }
        File head = new File(path);
        try {
            BitmapHelper.file2Bitmap(head);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除目录下所有文件(不删除文件夹)
     *
     * @param filePath 文件目录
     */
    public static void delete(String filePath) {
        File f = new File(filePath);
        if (!f.exists() || f.listFiles().length <= 0) {
            return;
        }
        for (File file : f.listFiles()) {
            if (file.isFile()) {
                delete(file);
            }
        }
    }

    /**
     * 可以删除文件夹.
     */
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (File childFile : childFiles) {
                delete(childFile);
            }
            file.delete();
        }
    }

    /**
     * 删除这个文件
     * 或者一个文件夹
     *
     * @param file 可以是文件路径或者文件夹
     */
    public static void deleteFilePath(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFilePath(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * file 转换成byte流.
     *
     * @param filePath 文件路径
     * @return 文件转换成的byte[]
     */
    public static byte[] getBytesFromFilePath(String filePath) {
        if (StringUtil.isNull(filePath)) {
            return null;
        }
        File f = new File(filePath);
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * file 转换成byte流.
     */
    public static byte[] getBytesFromFile(File file) {
        try {
            FileInputStream stream = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据路径获得图片并压缩，返回byte数组用于上传服务器.
     */
    public static byte[] getSmallBitmapByte(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 960, 1600);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        if (bitmap == null) {
            Logger.e("the method getSmallBitmapByte has error,bitmap is null,so break up!");
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 压缩到指定分辨率的方法 以1280X960为例
     *
     * @param file       源文件
     * @param targetPath 目标路径
     **/
    public static File scaleFile(File file, String targetPath) {
        long fileSize = file.length();
        final long fileMaxSize = 200 * 1024;//超过200K的图片需要进行压缩
        if (fileSize > fileMaxSize) {
            try {
                byte[] bytes = getBytesFromFile(file);//将文件转换为字节数组
                BitmapFactory.Options options = new BitmapFactory.Options();
                //仅仅解码边缘区域
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                //得到宽高
                int width = options.outWidth;
                int height = options.outHeight;
                float scaleWidth = 0f;
                float scaleHeight = 0f;
                Matrix matrix = new Matrix();
                if (width > height) {
                    scaleWidth = (float) 1280 / width;
                    scaleHeight = (float) 960 / height;

                } else {
                    scaleWidth = (float) 960 / width;
                    scaleHeight = (float) 1280 / height;
                }
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                int degree = BitmapHelper.readPictureDegree(file.getPath());//获取相片拍摄角度
                if (degree != 0) {//旋转照片角度，防止头像横着显示
                    bitmap = BitmapHelper.rotateBitmap(bitmap, degree);
                }
                matrix.postScale(scaleWidth, scaleHeight);//执行缩放
                Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
                        false);
                if (resizeBitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int quality = 100;
                    resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    //限制压缩后图片最大为200K，否则继续压缩
                    while (baos.toByteArray().length > fileMaxSize) {
                        baos.reset();
                        quality -= 10;
                        resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                    }
                    baos.close();
                    File targetFile = new File(targetPath);
                    targetFile.mkdirs();
                    if (targetFile.exists()) {
                        boolean flag = targetFile.delete();
                    }
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    fos.write(baos.toByteArray());
                    fos.flush();
                    fos.close();
                    return targetFile;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return file;
        }
    }

    /**
     * 计算图片的缩放值.
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static String getAreaInfoFromAssets() {
        try {
            InputStreamReader isr = new InputStreamReader(sContext.getAssets().open(
                    "area.json"), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            return builder.toString();
        } catch (Exception e) {
            Logger.e("Get area info from assets error is [" + e.getMessage() + "]");
        }
        return StringUtil.EMPTY_STR;
    }
//
//    public static ArrayList<CityPickerBean.HotCityBean> getHotCityInfoFromXml() {
//        ArrayList<CityPickerBean.HotCityBean> hotList = new ArrayList<>();
//        String[] city = sContext.getResources().getStringArray(R.array.hot_city);
//        String[] cityId = sContext.getResources().getStringArray(R.array.hot_city_id);
//        for (int i = 0; i < city.length; i++) {
//            hotList.add(new CityPickerBean.HotCityBean(city[i], cityId[i]));
//        }
//        return hotList;
//    }

    /**
     * 删除文件
     *
     * @param desPath  目标路径
     * @param fileName 文件名
     */
    public static void deleteFile(String desPath, String fileName) {
        File file = new File(desPath, fileName);
        file.delete();
    }

    /**
     * 重命名文件
     *
     * @param desPath 目标路径
     * @param oldName 原文件名
     * @param newName 新文件名
     */
    public static void renameFile(String desPath, String oldName, String newName) {
        File file = new File(desPath, oldName);
        file.renameTo(new File(desPath, newName));
    }

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 写入文件
     *
     * @param content
     */
    public static void writeDeviceData4Q(String content, String name) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        File dir = new File(TEMP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(TEMP_DIR + name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(file));
            out.write(content.getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
        }
    }

    public static String getDeviceData4Q(String name) {
        String content = "";
        File file = new File(TEMP_DIR + name);
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file)));
                content = reader.readLine();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(content)) {
            content = "";
        }
        return content;
    }

    /**
     * 读取assets本地json
     *
     * @param fileName
     * @param context
     * @return
     */
    public static String getJson(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
