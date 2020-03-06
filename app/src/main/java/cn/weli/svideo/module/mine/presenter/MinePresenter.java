package cn.weli.svideo.module.mine.presenter;

import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.mine.model.MsgModel;
import cn.weli.svideo.module.mine.model.bean.RedPointBean;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;
import cn.weli.svideo.module.mine.view.IMineView;

/**
 * 我的P层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see [class/method]
 * @since [1.0.0]
 */
public class MinePresenter implements IPresenter {

    private IMineView mView;

    private MsgModel mMsgModel;

    public MinePresenter(IMineView view) {
        mView = view;
        mMsgModel = new MsgModel();
    }

    /**
     * 处理fragment页面展示
     *
     * @param hide 是否对用户可见
     */
    public void handleUserVisible(boolean hide) {
        if (hide) {
            mView.handleFragmentHide();
        } else {
            mView.handleFragmentShow();
        }
    }

    /**
     * 检查登录状态
     */
    public void checkLoginStatus() {
        if (!WlVideoAppInfo.getInstance().hasLogin()) {
            mView.startToLogin();
            return;
        }
        mView.showUserInfo(WlVideoAppInfo.getInstance().getUserInfoBean());
    }

    /**
     * 查询消息
     */
    public void queryMsg() {
        if (!WlVideoAppInfo.getInstance().hasLogin()) {
            return;
        }
        mMsgModel.getRedPoint(RedPointBean.LOCATION_MSG_CENTER, new SimpleHttpSubscriber<RedPointBean>() {
            @Override
            public void onResponseSuccess(RedPointBean redPointBean) {
                mView.setRedPointShow(redPointBean.isShow());
            }
        });
    }

    /**
     * 检查用户性质，是否在黑名单中
     */
    public void checkUserProperty() {
        if(WlVideoAppInfo.getInstance().getUserInfoBean().blacklist_info != null){
            mView.setBlacklistTxt(WlVideoAppInfo.getInstance().getUserInfoBean().blacklist_info.blacklist
                    == UserInfoBean.IN_BLACKLIST);
        }
    }

    /**
     * 设置黑名单tip
     */
    public void setTip(){
        if(WlVideoAppInfo.getInstance().getUserInfoBean().blacklist_info != null){
            String tip = WlVideoAppInfo.getInstance().getUserInfoBean().blacklist_info.avatar_tip;
            if(StringUtil.isNull(tip)){
                mView.setBlacklistTip(StringUtil.EMPTY_STR,false);
            }else {
                mView.setBlacklistTip(tip,true);
            }
        }
    }

    @Override
    public void clear() {
        mMsgModel.cancelRedPoint();
    }
}
