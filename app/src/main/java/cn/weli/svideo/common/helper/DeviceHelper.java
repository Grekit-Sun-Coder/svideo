package cn.weli.svideo.common.helper;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.SharePrefConstant;

/**
 * 同中华万年历一样，imei、imsi、mac地址存储到system目录下，在用户卸载时不清除，保证这些值不变，保证设备的唯一性
 */
public class DeviceHelper {

    private static final String TAG = "DeviceUtil";
    private static final String TEMP_DIR = Environment.getExternalStorageDirectory().getPath() + "/system/";
    private static String temp = "dev_info";
    public static final String IMEI = "imei";
    public static final String IMSI = "imsi";
    public static final String MAC = "mac";

    /**
     * 获取imei、imsi、mac
     *
     * @return
     */
    public static String getDeviceInfo() {
        String macImeiImsi = "";
        File file = new File(TEMP_DIR + temp);
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file)));
                macImeiImsi = reader.readLine();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(macImeiImsi)) {
            macImeiImsi = "";
        }
        return macImeiImsi;
    }

    /**
     * 写入imei、imsi、mac
     *
     * @param content
     */
    public static void writeDeviceInfo(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        File dir = new File(TEMP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(TEMP_DIR + temp);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(file));
            out.write(content.getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
        }
    }

    /**
     * 写入相应key、value值
     *
     * @param key
     * @param value
     */
    public static void writeValue(String key, String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        String content = getDeviceInfo();
        JSONObject object = null;
        try {
            if (TextUtils.isEmpty(content)) {
                object = new JSONObject();
            } else {
                object = new JSONObject(content);
            }
            object.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (object == null) {
            return;
        }
        File dir = new File(TEMP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(TEMP_DIR + temp);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(file));
            out.write(object.toString().getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
        }
    }

    public static String getDeviceValue(String key) {
        String value = "";
        String deviceInfo = getDeviceInfo();
        if (!TextUtils.isEmpty(deviceInfo)) {
            try {
                JSONObject jsonObject = new JSONObject(deviceInfo);
                value = jsonObject.optString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 初始化imei、imsi、mac到文件中
     * 此方法应该放在READ_PHONE_STATE权限获取到之后再调用
     */
    public static void initDeviceInfo(Context context) {
        File file = new File(TEMP_DIR + temp);
        if (file.exists()) {
            return;
        }
        JSONObject info = new JSONObject();
        try {
            info.put(IMEI, ApiHelper.getImei());
            info.put(IMSI, ApiHelper.getIMSI(context));
            info.put(MAC, ApiHelper.getMac());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        writeDeviceInfo(info.toString());
    }

}