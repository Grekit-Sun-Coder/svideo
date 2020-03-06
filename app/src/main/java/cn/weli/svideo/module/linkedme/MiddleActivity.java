package cn.weli.svideo.module.linkedme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.microquation.linkedme.android.LinkedME;
import com.microquation.linkedme.android.util.LinkProperties;

import java.util.HashMap;

import cn.weli.svideo.common.helper.ProtocolHelper;

public class MiddleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        LinkProperties linkProperties = intent.getParcelableExtra(LinkedME.LM_LINKPROPERTIES);
        HashMap<String, String> controlParams = linkProperties.getControlParams();
        if (controlParams != null) {
            String action = controlParams.get("action");
            ProtocolHelper.handleProtocolEvent(this, action);
        }
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LinkedME.getInstance().clearSessionParams();
    }
}
