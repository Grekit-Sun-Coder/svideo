package cn.weli.svideo.common.helper;

import android.content.Context;

import com.bun.miitmdid.core.IIdentifierListener;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.supplier.IdSupplier;

import org.json.JSONException;
import org.json.JSONObject;

import cn.weli.analytics.AnalyticsDataAPI;
import cn.weli.svideo.baselib.helper.FileHelper;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.common.constant.SharePrefConstant;

/**
 * 获取aaid和oaid
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-13
 * @see MiitDeviceHelper
 * @since [1.0.0]
 */
public class MiitDeviceHelper implements IIdentifierListener {

    private Context context;

    private static MiitDeviceHelper instance;

    public MiitDeviceHelper(Context context) {
        this.context = context;
    }

    public static MiitDeviceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MiitDeviceHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void initDevice() {
        if (!SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_HAD_DEVICE, false)) {
            callFromReflect(context);
        }
    }

    private int callFromReflect(Context cxt) {
        return MdidSdkHelper.InitSdk(cxt, true, this);
    }

    @Override
    public void OnSupport(boolean isSupport, IdSupplier _supplier) {
        if (_supplier == null) {
            return;
        }
        try {
            String oaid = _supplier.getOAID();
            String aaid = _supplier.getAAID();
            SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_HAD_DEVICE, true);
            if (oaid == null) {
                oaid = "";
            }
            if (aaid == null) {
                aaid = "";
            }
            SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_DEVICE_AAID, aaid);
            SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_DEVICE_OAID, oaid);
            JSONObject info = new JSONObject();
            try {
                info.put("oaid", oaid);
                info.put("aaid", aaid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            FileHelper.writeDeviceData4Q(info.toString(), "device");
            AnalyticsDataAPI.sharedInstance(context).setCommonData(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        _supplier.shutDown();
    }
}
