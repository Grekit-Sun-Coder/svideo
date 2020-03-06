package cn.weli.svideo.common.helper.picture;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.tools.PictureFileUtils;

import cn.weli.svideo.R;

/**
 * 图片选择帮助类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-24
 * @see [class/method]
 * @since [1.0.0]
 */
public class PictureHelper {

    public static void startSingleSelect(Activity activity) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .loadImageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .isWeChatStyle(false)// 是否开启微信图片选择风格
                .setPictureStyle(getPictureParameterStyle(activity))// 动态自定义相册主题
                .setPictureCropStyle(getPictureCropParameterStyle(activity))// 动态自定义裁剪主题
                .setPictureWindowAnimationStyle(getPictureWindowAnimationStyle())// 自定义相册启动退出动画
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)// 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .isSingleDirectReturn(true)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .previewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(true)// 是否裁剪
                .compress(true)// 是否压缩
                .compressQuality(80)// 图片压缩后输出质量 0~ 100
                .synOrAsy(true)//同步false或异步true 压缩 默认同步
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                .circleDimmedLayer(true)// 是否圆形裁剪
                .rotateEnabled(false)
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .openClickSound(false)// 是否开启点击声音
                .cutOutQuality(90)// 裁剪输出质量 默认100
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private static PictureParameterStyle getPictureParameterStyle(Context context) {
        // 相册主题
        PictureParameterStyle parameterStyle = new PictureParameterStyle();
        // 是否改变状态栏字体颜色(黑白切换)
        parameterStyle.isChangeStatusBarFontColor = false;
        // 是否开启右下角已完成(0/9)风格
        parameterStyle.isOpenCompletedNumStyle = false;
        // 是否开启类似QQ相册带数字选择风格
        parameterStyle.isOpenCheckNumStyle = false;
        // 相册状态栏背景色
        parameterStyle.pictureStatusBarColor = ContextCompat.getColor(context, R.color.color_14181B);
        // 相册列表标题栏背景色
        parameterStyle.pictureTitleBarBackgroundColor = ContextCompat.getColor(context, R.color.color_14181B);
        // 相册列表标题栏右侧上拉箭头
        parameterStyle.pictureTitleUpResId = R.drawable.picture_icon_arrow_up;
        // 相册列表标题栏右侧下拉箭头
        parameterStyle.pictureTitleDownResId = R.drawable.picture_icon_arrow_down;
        // 相册文件夹列表选中圆点
        parameterStyle.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval;
        // 相册返回箭头
        parameterStyle.pictureLeftBackIcon = R.drawable.icon_back;
        // 标题栏字体颜色
        parameterStyle.pictureTitleTextColor = ContextCompat.getColor(context, R.color.color_E7E7E7);
        // 相册右侧取消按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
        parameterStyle.pictureCancelTextColor = ContextCompat.getColor(context, R.color.color_E7E7E7);
        // 相册列表勾选图片样式
        parameterStyle.pictureCheckedStyle = R.drawable.picture_checkbox_selector;
        // 相册列表底部背景色
        parameterStyle.pictureBottomBgColor = ContextCompat.getColor(context, R.color.color_14181B);
        // 已选数量圆点背景样式
        parameterStyle.pictureCheckNumBgStyle = R.drawable.picture_num_oval;
        // 相册列表底下预览文字色值(预览按钮可点击时的色值)
        parameterStyle.picturePreviewTextColor = ContextCompat.getColor(context, R.color.picture_color_fa632d);
        // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
        parameterStyle.pictureUnPreviewTextColor = ContextCompat.getColor(context, R.color.picture_color_white);
        // 相册列表已完成色值(已完成 可点击色值)
        parameterStyle.pictureCompleteTextColor = ContextCompat.getColor(context, R.color.picture_color_fa632d);
        // 相册列表未完成色值(请选择 不可点击色值)
        parameterStyle.pictureUnCompleteTextColor = ContextCompat.getColor(context, R.color.picture_color_white);
        // 预览界面底部背景色
        parameterStyle.picturePreviewBottomBgColor = ContextCompat.getColor(context, R.color.picture_color_grey);
        // 外部预览界面删除按钮样式
        parameterStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete;
        // 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
        parameterStyle.pictureOriginalControlStyle = R.drawable.picture_original_wechat_checkbox;
        // 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
        parameterStyle.pictureOriginalFontColor = ContextCompat.getColor(context, R.color.color_white);
        // 外部预览界面是否显示删除按钮
        parameterStyle.pictureExternalPreviewGonePreviewDelete = true;
        // 设置NavBar Color SDK Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP有效
        parameterStyle.pictureNavBarColor = Color.parseColor("#393a3e");
        return parameterStyle;
    }

    private static PictureCropParameterStyle getPictureCropParameterStyle(Context context) {
        PictureCropParameterStyle cropParameterStyle = new PictureCropParameterStyle(
                ContextCompat.getColor(context, R.color.color_14181B),
                ContextCompat.getColor(context, R.color.color_14181B),
                ContextCompat.getColor(context, R.color.color_E7E7E7), false);
        return cropParameterStyle;
    }

    private static PictureWindowAnimationStyle getPictureWindowAnimationStyle() {
        PictureWindowAnimationStyle animationStyle = new PictureWindowAnimationStyle();
        animationStyle.ofAllAnimation(R.anim.picture_anim_up_in, R.anim.picture_anim_down_out);
        return animationStyle;
    }

    /**
     * 清除缓存
     */
    public static void clearPicCache(Context context) {
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PictureFileUtils.deleteAllCacheDirFile(context);
        }
    }
}