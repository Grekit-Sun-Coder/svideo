package cn.weli.svideo.push;

import android.content.Context;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import org.json.JSONObject;

import cn.etouch.logger.Logger;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.helper.ApiHelper;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.main.model.MainModel;

/**
 * 推送Service
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-05
 * @see [class/method]
 * @since [1.0.0]
 */
public class WlVideoIntentService extends GTIntentService {
    @Override
    public void onReceiveServicePid(Context context, int i) {

    }

    @Override
    public void onReceiveClientId(Context context, String cid) {
        //获取到个推cid之后即上传，服务器推送都基于此来给设备发送推送消息
        Logger.d("Receive push cid is [" + cid + "]");
        ApiHelper.setCid(cid);
        MainModel mainModel = new MainModel();
        mainModel.appInit(cid, new SimpleHttpSubscriber<Object>() {
            @Override
            public void onPostExecute() {
                mainModel.cancelAppInit();
            }
        });
        StatisticsAgent.push(context, StatisticsUtils.EventName.PUSH_REGISTER, StatisticsUtils.CID.CID_1, StatisticsUtils.MD.MD_9);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String taskId = msg.getTaskId();
        String messageId = msg.getMessageId();
        byte[] payload = msg.getPayload();
        if (payload != null) {
            String data = new String(payload);
            Logger.d("Receive push message ：[" + data + "]");
            try {
                JSONObject object = new JSONObject(data);
                JSONObject msgObject = object.optJSONObject("c");
                if (msgObject != null) {
                    WlPushManager.handlerMsgFromServerPush(context, object.toString(), messageId, taskId);
                    StatisticsAgent.push(context, StatisticsUtils.EventName.PUSH_MSG_RECEIVE, StatisticsUtils.CID.CID_1, StatisticsUtils.MD.MD_9);
                }
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b) {

    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {

    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {

    }
}
