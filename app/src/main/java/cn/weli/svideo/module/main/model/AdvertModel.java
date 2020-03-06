package cn.weli.svideo.module.main.model;

import java.util.HashMap;

import cn.etouch.retrofit.RetrofitManager;
import cn.etouch.retrofit.transfer.HttpRxFun;
import cn.etouch.retrofit.transfer.RxComposer;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.helper.ApiHelper;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.main.model.api.AdvertService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * 广告相关model
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-01-11
 * @see cn.weli.svideo.module.video.ui.VideoPlayFragment
 * @since [1.0.0]
 */
public class AdvertModel {

    private static AdvertService sAdvertService;

    private static DisposableObserver sAdObserver;

    /**
     * 鲤刷刷自行上报广告行为
     *
     * @param space 位置
     * @param type  行为
     */
    public static void reportAdEvent(String space, String type) {
        if (!WlVideoAppInfo.getInstance().hasLogin()) {
            return;
        }
        if (StringUtil.isNull(space) || StringUtil.isNull(type)) {
            return;
        }
        if (sAdvertService == null) {
            sAdvertService = RetrofitManager.getInstance().getDefaultRetrofit().create(
                    AdvertService.class);
        }
        HashMap<String, String> map = new HashMap<>(16);
        map.put("space", space);
        map.put("action_type", type);
        ApiHelper.addCommonParams(map);
        sAdObserver = sAdvertService.reportAdEvent(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleHttpSubscriber<>());
    }

    public static void clear() {
        if (sAdvertService != null) {
            sAdvertService = null;
        }
        if (sAdObserver != null && !sAdObserver.isDisposed()) {
            sAdObserver.dispose();
        }
    }
}