package cn.weli.svideo.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.hwangjr.rxbus.RxBus;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import cn.etouch.logger.Logger;
import cn.weli.svideo.baselib.helper.PackageHelper;
import cn.weli.svideo.common.constant.BusinessConstants;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-24
 * @see [class/method]
 * @since [1.0.0]
 */
public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {

    private IWXAPI mWXAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWXAPI = WXAPIFactory.createWXAPI(this,
                PackageHelper.getMetaData(this, BusinessConstants.MetaData.Wx_APP_ID));
        mWXAPI.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWXAPI.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Logger.d("WXLogin onResp OK");
                if (baseResp instanceof SendAuth.Resp) {
                    SendAuth.Resp newResp = (SendAuth.Resp) baseResp;
                    //获取微信传回的code
                    String code = newResp.code;
                    RxBus.get().post(new WxAuthorizeEvent(code));
                    Logger.d("WXLogin onResp code = " + code);
                }

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Logger.d("WXLogin onResp ERR_USER_CANCEL ");
                //发送取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Logger.d("WXLogin onResp ERR_AUTH_DENIED");
                //发送被拒绝
                break;
            default:
                Logger.d("WXLogin onResp default errCode " + baseResp.errCode);
                //发送返回
                break;
        }
        finish();
    }

}

