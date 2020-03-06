package cn.weli.svideo.advert.download;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.WlVideoApplication;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.main.ui.SplashActivity;


/**
 * 下载通知栏点击进入的页面
 * JXH
 * 2018/9/26
 */
public class DialogActivity extends AppBaseActivity<DialogPresenter, IDialogView> implements IDialogView {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!WlVideoAppInfo.getInstance().isAppRunning()) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    protected Class<DialogPresenter> getPresenterClass() {
        return DialogPresenter.class;
    }

    @Override
    protected Class<IDialogView> getViewClass() {
        return IDialogView.class;
    }
}
