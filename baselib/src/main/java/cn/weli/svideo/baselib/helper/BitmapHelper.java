package cn.weli.svideo.baselib.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.etouch.logger.Logger;
import cn.weli.svideo.baselib.utils.ScreenUtil;


/**
 * Bitmap相关方法
 *
 * @author jianglei, Peng.Gao
 * @version [1.0.3]
 */
public class BitmapHelper {

    private static final int DEFAULT_MAX_SIZE = 100;

    /**
     * drawable转化成bitmap.
     *
     * @param mDrawable 待转化的drawable
     * @return 转化后的bitmap
     */
    public static Bitmap makeDrawableToBitmap(Drawable mDrawable) {
        int width = mDrawable.getIntrinsicWidth();
        int height = mDrawable.getIntrinsicHeight();
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas ca = new Canvas(bitmap);
        mDrawable.setBounds(0, 0, width, height);
        mDrawable.draw(ca);
        return bitmap;
    }

    /**
     * 从本地路径获取bitmap.
     *
     * @param path    文件路径
     * @param options 为空，完全解析bitmap，不为空则解析图片的options
     * @return 对应的bitmap
     */
    public static Bitmap getBitmapFromLocal(String path, BitmapFactory.Options options) {
        Bitmap bitmap = null;
        try {
            File image = new File(path);
            FileInputStream is = new FileInputStream(image);
            bitmap = BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 转换图片成圆形.
     *
     * @param bitmap 传入Bitmap对象
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dstLeft, dstTop, dstRight, dstBottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dstLeft = 0;
            dstTop = 0;
            dstRight = width;
            dstBottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dstLeft = 0;
            dstTop = 0;
            dstRight = height;
            dstBottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dstLeft, (int) dstTop, (int) dstRight, (int) dstBottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 从本地路径获取bitmap.
     *
     * @param filePath 文件路径
     * @param width    需要显示的宽
     * @param height   需要显示的高
     * @return bitmap
     */
    public static Bitmap readBitmapFromLocal(String filePath, float width, float height) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            int w = options.outWidth;
            int h = options.outHeight;
            int scale = 1;
            if (w > width || h > height) {
                if (w > h) {
                    scale = Math.round(w / width);
                } else {
                    scale = Math.round(h / height);
                }
            }
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            Bitmap mBitmap = BitmapFactory.decodeFile(filePath, options);
            int degree = 0;
            try {
                ExifInterface exifInterface = new ExifInterface(filePath);
                int orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            return Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(),
                    matrix, true);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 图片压缩.
     */
    public static Bitmap decodeFile(String fPath, Context context) throws FileNotFoundException {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inDither = false; // Disable Dithering mode
        BitmapFactory.decodeFile(fPath, opts);
        final int REQUIRED_SIZE = ScreenUtil.getScreenWidth(context);
        if (opts.outWidth > REQUIRED_SIZE) {
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap srcBitmap = BitmapFactory.decodeFile(fPath, opts);
            if (srcBitmap == null) {
                throw new FileNotFoundException("the file path" + fPath + " is not found");
            }
            return srcBitmap;
        } else {
            float widthRatio = (float) REQUIRED_SIZE / opts.outWidth;
            Matrix matrix = new Matrix();
            matrix.postScale(widthRatio, widthRatio);
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap srcBitmap = BitmapFactory.decodeFile(fPath, opts);
            if (srcBitmap == null) {
                throw new FileNotFoundException("the file path" + fPath + " is not found");
            }
            Bitmap bm = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
            return bm;
        }
    }

    /**
     * 图片压缩.
     *
     * @param resID 图片资源ID
     */
    public static Bitmap decodeResource(int resID, Context context) throws FileNotFoundException {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inDither = false; // Disable Dithering mode
        BitmapFactory.decodeResource(context.getResources(), resID, opts);
        final int REQUIRED_SIZE = ScreenUtil.getScreenWidth(context);
        if (opts.outWidth > REQUIRED_SIZE) {
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap srcBitmap = BitmapFactory.decodeResource(context.getResources(), resID, opts);
            if (srcBitmap == null) {
                throw new FileNotFoundException(
                        "the file Resources resID" + resID + " is not found");
            }
            return srcBitmap;
        } else {
            float widthRatio = (float) REQUIRED_SIZE / opts.outWidth;
            Matrix matrix = new Matrix();
            matrix.postScale(widthRatio, widthRatio);
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap srcBitmap = BitmapFactory.decodeResource(context.getResources(), resID, opts);
            if (srcBitmap == null) {
                throw new FileNotFoundException(
                        "the file Resources resID" + resID + " is not found");
            }
            return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }


    }

    /**
     * 通过file获取bitmap.
     */
    public static Bitmap file2Bitmap(File file) {
        Bitmap bitmap = null;
        if (file != null) {
            bitmap = BitmapFactory.decodeFile(file.getPath());
        }
        return bitmap;
    }

    /**
     * 把bitmap 转file
     */
    public static File bitmap2File(Bitmap bitmap, String filePath, String targetFilepath) {
        File file = new File(targetFilepath);//将要保存图片的路径
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            } else {
                file.delete();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(targetFilepath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            file = new File(filePath);
            Logger.w("bitmap2File error is [" + e.getMessage() + "]");
        }
        return file;
    }

    /**
     * 压缩图片
     * 把bitmap转换成String.
     *
     * @param filePath 文件路径
     * @return Base64生成的String
     */
    public static String fileToBase64Code(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        String result = Base64.encodeToString(b, Base64.NO_WRAP);
        baos.reset();
        if (!bm.isRecycled()) {
            bm.recycle();
        }
        return result;
    }

    /**
     * 计算图片的缩放值.
     *
     * @param options   Bitmap的option
     * @param reqWidth  需要的宽度
     * @param reqHeight 需要的高度
     * @return 缩放比
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示.
     *
     * @param filePath 文件路径
     * @return 对应的bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 1080, 1920);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        int degree = readPictureDegree(filePath);//获取相片拍摄角度
        if (degree != 0) {//旋转照片角度，防止头像横着显示
            bitmap = rotateBitmap(bitmap, degree);
        }
        try {
            int quality = 100;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

            while (baos.toByteArray().length / 1024 > 200) {
                if (quality == 50) {
                    break;
                }
                baos.reset();
                quality -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            }
            Logger.e("getSmallBitmap ---  baos.toByteArray().length=" + baos.toByteArray().length);
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            bitmap = BitmapFactory.decodeStream(isBm, null, null);
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos1);
            Logger.e("getSmallBitmap1 ---  baos1.toByteArray().length="
                    + baos1.toByteArray().length);
        } catch (Exception e) {
            Logger.w("get small pic error");
        }
        return bitmap;
    }

    public static File compressImage(String filePath, String targetPath) {
        return compressImage(filePath, targetPath, DEFAULT_MAX_SIZE);
    }

    public static File compressImage(String filePath, String targetPath, int maxSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 960, 1280);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        int degree = readPictureDegree(filePath);//获取相片拍摄角度
        if (degree != 0) {//旋转照片角度，防止头像横着显示
            bitmap = rotateBitmap(bitmap, degree);
        }
        File file = new File(targetPath);
        try {
            int quality = 100;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

            while (baos.toByteArray().length / 1024 > maxSize) {
                baos.reset();
                quality -= 5;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            }

            if (!file.exists()) {
                file.getParentFile().mkdirs();
            } else {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Logger.w("get small pic error");
            file = new File(filePath);
        }
        Logger.d("File origin path is [" + filePath + "]"
                + "\nbefore size is [" + String.valueOf(getFileSize(new File(filePath)) + "]"
                + "\nafter size is [" + String.valueOf(getFileSize(file)) + "]"));
        return file;
    }

    /**
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 计算缩放比
     *
     * @param bitWidth  当前图片宽度
     * @param bitHeight 当前图片高度
     */
    public static int getRatioSize(int bitWidth, int bitHeight) {
        // 图片最大分辨率
        int imageHeight = 1280;
        int imageWidth = 960;
        // 缩放比
        int ratio = 1;
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageWidth;
        } else if (bitWidth < bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight;
        }
        // 最小比率为1
        if (ratio <= 0) {
            ratio = 1;
        }
        return ratio;
    }

    public static long getFileSize(File file) {
        try {
            long size = 0;
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
                fis.close();
            } else {
                file.createNewFile();
                Logger.w("文件不存在!");
            }
            return size;
        } catch (Exception e) {
            Logger.e("get file size error");
        }
        return 0;
    }

    /**
     * 将不规则bitmap剪裁成正方形
     *
     * @param bitmap     即将剪裁的原文件
     * @param edgeLength 想要的正方形边长
     **/
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap) {
            return null;
        }
        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            int longerEdge = (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;//
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;
            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;
            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }
        return result;
    }

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        //把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    /**
     * 获取照片角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    /**
     * 根据bitmap保存图片到本地
     *
     * @param bitmap
     * @return
     */
    public static String saveBitmapToLocal(String path, Bitmap bitmap, String userName) {
        if (null == bitmap) {
            return null;
        }
        String filePath;
        FileOutputStream fileOutput = null;
        File imgFile;
        try {
            File desDir = new File(path);
            if (!desDir.exists()) {
                desDir.mkdirs();
            }
            imgFile = new File(path, userName + ".png");
            imgFile.createNewFile();
            fileOutput = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutput);
            fileOutput.flush();
            filePath = imgFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            filePath = null;
        } catch (IOException e) {
            e.printStackTrace();
            filePath = null;
        } finally {
            if (null != fileOutput) {
                try {
                    fileOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     */
    public static Bitmap readBitMap(Context context, int resId, int inSampleSize) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = inSampleSize;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
