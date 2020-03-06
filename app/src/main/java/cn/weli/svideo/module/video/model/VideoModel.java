package cn.weli.svideo.module.video.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.etouch.cache.CacheManager;
import cn.etouch.retrofit.RetrofitManager;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.etouch.retrofit.response.EmptyResponseBean;
import cn.etouch.retrofit.transfer.HttpRxFun;
import cn.etouch.retrofit.transfer.RxComposer;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.CacheConstant;
import cn.weli.svideo.common.helper.ApiHelper;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.video.model.api.VideoService;
import cn.weli.svideo.module.video.model.bean.VideoBean;
import cn.weli.svideo.module.video.model.bean.VideoPathBean;
import cn.weli.svideo.module.video.presenter.VideoPlayPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * 视频相关M层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-13
 * @see VideoPlayPresenter
 * @since [1.0.0]
 */
public class VideoModel {

    private DisposableObserver mVideoListObserver;

    private DisposableObserver mVideoPathObserver;

    private DisposableObserver mVideoPraiseObserVer;

    private DisposableObserver mVideoPraiseListObserver;

    private DisposableObserver mVideoReportObserVer;

    private VideoService mVideoService;

    public VideoModel() {
        mVideoService = RetrofitManager.getInstance().getDefaultRetrofit().create(VideoService.class);
    }

    /**
     * 请求视频列表
     *
     * @param videoId 视频id
     * @param subscriber 请求回调
     */
    public void requestVideoList(String videoId, HttpSubscriber<List<VideoBean>> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        if (!StringUtil.isNull(videoId)) {
            map.put("item_id", videoId);
        }
        ApiHelper.addCommonParams(map);
        mVideoListObserver = mVideoService
                .getVideoList(map, ApiHelper.getRequestWrapper())
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 请求视频地址
     *
     * @param itemId     视频id
     * @param subscriber 请求回调
     */
    public void requestVideoPath(long itemId, HttpSubscriber<VideoPathBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("item_id", String.valueOf(itemId));
        ApiHelper.addCommonParams(map);
        mVideoPathObserver = mVideoService
                .getVideoPath(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 请求视频地址
     *
     * @param itemId     视频id
     * @param subscriber 请求回调
     */
    public void requestVideoPraise(long itemId, HttpSubscriber<EmptyResponseBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("itemId", String.valueOf(itemId));
        ApiHelper.addCommonParams(map);
        mVideoPraiseObserVer = mVideoService
                .videoPraise(String.valueOf(itemId), map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 请求视频地址
     *
     * @param itemId     视频id
     * @param subscriber 请求回调
     */
    public void requestVideoUnPraise(long itemId, HttpSubscriber<EmptyResponseBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("itemId", String.valueOf(itemId));
        ApiHelper.addCommonParams(map);
        mVideoPraiseObserVer = mVideoService
                .videoUnPraise(String.valueOf(itemId), map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 请求视频列表
     *
     * @param subscriber 请求回调
     */
    public void requestVideoPraiseList(int page, HttpSubscriber<List<VideoBean>> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        ApiHelper.addCommonParams(map);
        mVideoPraiseListObserver = mVideoService
                .getVideoPraiseList(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 举报视频
     */
    public void requestVideoReport(String itemId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("itemId", itemId);
        ApiHelper.addCommonParams(map);
        mVideoReportObserVer = mVideoService
                .videoReport(itemId, map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleHttpSubscriber<>());
    }

    /**
     * 分享视频
     */
    public void requestVideoShare(String itemId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("item_id", itemId);
        ApiHelper.addCommonParams(map);
        mVideoReportObserVer = mVideoService
                .videoShare(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleHttpSubscriber<>());
    }

    /**
     * 保存缓存的点赞视频列表数据
     *
     * @param list 列表数据
     */
    public void saveCachePraiseList(ArrayList<VideoBean> list) {
        CacheManager.getInstance().saveSerializable(CacheConstant.CACHE_PRAISE_LIST, list);
    }

    /**
     * 获取缓存的点赞视频列表数据
     *
     * @return 点赞视频列表数据
     */
    public ArrayList<VideoBean> getCachePraiseList() {
        return CacheManager.getInstance().loadSerializable(CacheConstant.CACHE_PRAISE_LIST);
    }

    public void cancelVideoListRequest() {
        if (mVideoListObserver != null && !mVideoListObserver.isDisposed()) {
            mVideoListObserver.dispose();
        }
    }

    public void cancelVideoPathRequest() {
        if (mVideoPathObserver != null && !mVideoPathObserver.isDisposed()) {
            mVideoPathObserver.dispose();
        }
    }

    public void cancelVideoPraiseListRequest() {
        if (mVideoPraiseListObserver != null && !mVideoPraiseListObserver.isDisposed()) {
            mVideoPraiseListObserver.dispose();
        }
    }

    public void cancelVideoPraiseRequest() {
        if (mVideoPraiseObserVer != null && !mVideoPraiseObserVer.isDisposed()) {
            mVideoPraiseObserVer.dispose();
        }
    }

    public void cancelVideoReportRequest() {
        if (mVideoReportObserVer != null && !mVideoReportObserVer.isDisposed()) {
            mVideoReportObserVer.dispose();
        }
    }
}
