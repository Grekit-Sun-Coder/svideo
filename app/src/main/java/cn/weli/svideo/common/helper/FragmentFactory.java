package cn.weli.svideo.common.helper;

import android.support.v4.app.Fragment;

import cn.weli.svideo.module.mine.ui.MinePageFragment;
import cn.weli.svideo.module.task.ui.TaskPageFragment;
import cn.weli.svideo.module.video.ui.VideoPlayFragment;

/**
 * Fragment工厂类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see FragmentFactory
 * @since [1.0.0]
 */
public class FragmentFactory {

    public final static String FLAG_FRAGMENT_NONE = "fragment_none";
    public static final String FLAG_FRAGMENT_VIDEO = "fragment_video";
    public static final String FLAG_FRAGMENT_MINE = "fragment_mine";
    public static final String FLAG_FRAGMENT_TASK = "fragment_task";

    /**
     * 工厂方法，获取Fragment实例
     *
     * @param state Fragment标签
     */
    public static Fragment getFragment(String state) {
        Fragment result = null;
        switch (state) {
            case FLAG_FRAGMENT_VIDEO:
                result = new VideoPlayFragment();
                break;
            case FLAG_FRAGMENT_MINE:
                result = new MinePageFragment();
                break;
            case FLAG_FRAGMENT_TASK:
                result = new TaskPageFragment();
                break;
            default:
                break;
        }
        return result;
    }
}
