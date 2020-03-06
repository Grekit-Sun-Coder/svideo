package cn.weli.svideo.push;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.etouch.logger.Logger;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.utils.GsonUtil;
import cn.weli.svideo.module.main.model.bean.PushBean;
import cn.weli.svideo.module.main.ui.MainActivity;
import cn.weli.svideo.module.main.ui.SplashActivity;
import cn.weli.svideo.module.main.view.IMainView;

/**
 * 个推多厂商推送适配中转activity，用于处理小米、华为通知点击事件跳转
 * <p>
 * 注意manifest中exported为true否则华为系统可能无法打开
 *
 * @author liheng
 * @date 18/5/22
 * 需求文档 http://pm.etouch.cn/task-view-10035.html
 */
public class ThirdPushActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            final String data = getIntent().getStringExtra("parm1");
            if (!TextUtils.isEmpty(data)) {
                try {
                    PushBean pushBean = (PushBean) GsonUtil.fromJsonToObject(data, PushBean.class);
                    Intent intent = new Intent();
                    if (WlVideoAppInfo.getInstance().isAppRunning()) {
                        Logger.d("当app已经运行，跳转到MainActivity");
                        // 当app已经运行，跳转到MainActivity
                        intent.setClass(this, MainActivity.class);
                    } else {
                        Logger.d("若app未运行，先跳转到SplashActivity");
                        // 若app未运行，先跳转到SplashActivity
                        intent.setClass(this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    String protocol = pushBean.c.b;
                    intent.putExtra(IMainView.EXTRA_PROTOCOL, protocol);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
