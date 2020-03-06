package cn.weli.svideo.module.linkedme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cn.weli.svideo.BuildConfig;
import cn.weli.svideo.R;
import cn.weli.svideo.common.helper.ProtocolHelper;

public class UriSchemeProcessActivity extends AppCompatActivity {
    private static final String TAG = "UriSchemeProcessActivit";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startApp();
    }

    private void startApp() {
        Intent intentForPackage = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intentForPackage != null) {
            intentForPackage.setFlags(getIntent().getFlags());
            startActivity(intentForPackage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri data = getIntent().getData();
        // App打开后无广告展示及登录等条件限制，直接在此处调用以下方法跳转到具体页面，若有条件限制请参考Demo
        // 防止跳转后一直停留在该页面
        boolean alive = (getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0;
        if (alive) {
            try {
                if (data != null) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "onResume: " + data.toString());
                    }
                    ProtocolHelper.handleProtocolEvent(this, data.toString());
                }
                finish();
                overridePendingTransition(0, R.anim.alpha_gone);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }
}
