package cn.weli.svideo.module.video.component.widget.videoheart;

import android.graphics.Paint;

/**
 * 心图的数据
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-09-19
 * @see HeartRelativeLayout
 * @since [1.0.0]
 */
public class HeartBean {
    /**
     * 第几张图 第几帧
     */
    int count = 0;
    /**
     * 透明度
     */
    int alpha;
    /**
     * X坐标点
     */
    int X;
    /**
     * Y坐标点
     */
    int Y;
    /**
     * 缩放比例
     */
    float scanle;
    /**
     * 旋转角度
     */
    int degrees;
    Paint paint;
}
