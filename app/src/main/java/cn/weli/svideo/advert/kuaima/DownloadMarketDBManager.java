package cn.weli.svideo.advert.kuaima;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import cn.etouch.logger.Logger;

public class DownloadMarketDBManager {

    public static final String TableName = "download_cache";
    public static final String KEY_ROWId = "id";
    public static final String KEY_netUrl = "net_url";//
    public static final String KEY_pkg = "pkg";//
    public static final String KEY_name = "name";//
    /**
     * 由服务器返回，最后需要回传给服务器
     */
    public static final String KEY_apkId = "apk_id";
    /**
     * 该应用来源的广告类型，对应广告接口的rtp value
     */
    public static final String KEY_adRTP = "ad_rtp";
    public static final String KEY_down_time = "down_time";
    public static final String KEY_install_time = "install_time";
    public static final String KEY_install_callbacks = "install_callbacks";
    public static final String KEY_file_path = "file_path";
    public static final String[] columns = new String[]{KEY_ROWId,
            KEY_netUrl, KEY_pkg, KEY_apkId, KEY_adRTP, KEY_down_time, KEY_install_time};
    public static final String Create_table = "CREATE TABLE " + TableName
            + "(id INTEGER PRIMARY KEY  AUTOINCREMENT, " + KEY_netUrl
            + " TEXT , " + KEY_pkg + " TEXT ," + KEY_name + " TEXT ,"
            + KEY_apkId + " LONG DEFAULT 0,"
            + KEY_adRTP + " TEXT ,"
            + KEY_down_time + " LONG DEFAULT 0,"
            + KEY_install_time + " LONG DEFAULT 0,"
            + KEY_install_callbacks + " TEXT ,"
            + KEY_file_path + " TEXT " + ");";
    private static final String TableName2 = "reward_cache";
    public static final String KEY_type = "type";
    public static final String KEY_value = "value";
    private static final String Create_table2 = "CREATE TABLE " + TableName2
            + "(id INTEGER PRIMARY KEY  AUTOINCREMENT, " + KEY_type + " LONG DEFAULT 0, " + KEY_value + " TEXT " + ");";
    private static DatabaseHelper mDbHelper = null;
    private static SQLiteDatabase mDb = null;
    private static DownloadMarketDBManager dBManagerInstance = null;
    private static final String DATABASE_NAME = "DownloadMarket.db";
    private static final int DATABASE_VERSION = 4;
    private static Context mCtx;

    //    CREATE TABLE reward_cache(id INTEGER PRIMARY KEY  AUTOINCREMENT, type LONG , value TEXT);
//    CREATE TABLE reward_cache(id INTEGER PRIMARY KEY  AUTOINCREMENT, type LONG DEFAULT 0, value TEXT );
//    CREATE TABLE download_cache(id INTEGER PRIMARY KEY  AUTOINCREMENT, net_url TEXT , pkg TEXT ,name TEXT ,apk_id LONG DEFAULT 0,ad_rtp TEXT ,down_time LONG DEFAULT 0,install_time LONG DEFAULT 0,install_callbacks TEXT ,file_path TEXT );
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Create_table);
            db.execSQL(Create_table2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TableName);
            db.execSQL("DROP TABLE IF EXISTS " + TableName2);
            onCreate(db);
        }
    }

    private DownloadMarketDBManager(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public static DownloadMarketDBManager open(Context ctx) throws SQLException {
        if (dBManagerInstance != null) {
            if (mDb == null) {
                mDb = mDbHelper.getWritableDatabase();
            }
        } else {
            dBManagerInstance = new DownloadMarketDBManager(ctx);
            mDb = mDbHelper.getWritableDatabase();
        }
        return dBManagerInstance;
    }

    public void close() {
        // mDbHelper.close();
        dBManagerInstance = null;
        mDb.close();
    }

    /**
     * 插入一条值
     ***/
    public void inster(int type, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("value", value);
        contentValues.put("type", type);
        mDb.insert(TableName2, null, contentValues);
    }

    /**
     * 查询表中是否有值为@params value 的一条数据
     **/
    public boolean queryIfExist(String vaule) {
        //执行数据库语句
        Cursor cursor = mDb.query(TableName2, null,
                KEY_value + " = ? ", new String[]{vaule}, null, null, null);
        boolean ifExit = false;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ifExit = true;
            }
            cursor.close();
        }
        return ifExit;
    }

    /**
     * 获取下载时间
     */
    public long getDownLoadTime(String name) {
        Cursor cursor = mDb.query(TableName, new String[]{KEY_down_time},
                KEY_name + " LIKE ? ", new String[]{name}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
            cursor.close();
        }
        return 0;
    }

    /**
     * 向表更新或插入一条数据
     */
    public void insertOrUpdateOneDataToCache(String netUrl, String pkg, String name, long apkid, String ad_RTP, long downTime, long installTime, String installCallback, String filePath) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_netUrl, netUrl);
        cv.put(KEY_pkg, pkg);
        cv.put(KEY_name, name);
        cv.put(KEY_apkId, apkid);
        cv.put(KEY_adRTP, ad_RTP);
        cv.put(KEY_down_time, downTime);
        cv.put(KEY_install_time, installTime);
        cv.put(KEY_install_callbacks, installCallback);
        cv.put(KEY_file_path, filePath);
        int result = mDb.update(TableName, cv,
                KEY_pkg + " LIKE ? ", new String[]{pkg});
        if (result <= 0) {
            mDb.insert(TableName, null, cv);
        }
    }

    /**
     * 更新表中的安装时间
     */
    public int updateInstallTimeToCache(String pkg, long date) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_install_time, date);
        return mDb.update(TableName, cv, KEY_pkg + " LIKE ? ", new String[]{pkg});
    }

    /**
     * 根据key删除一条缓存
     */
    public void deleteOneCache(String pkg) {
        mDb.delete(TableName, KEY_pkg + " LIKE ? ",
                new String[]{pkg});
    }

    /**
     * 获取已经安装激活的应用
     */
    public Cursor getAllInstalledApp() {
        return mDb.query(TableName, columns, KEY_install_time + "!=0", null, null, null, null);
    }

    /**
     * 删除所有在time时间前安装的应用数据
     */
    public void deleteDataWhichInstallBeforeTime(long time) {
        mDb.delete(TableName, KEY_install_time + "< ? ",
                new String[]{time + ""});
    }

    /**
     * 删除所有在time时间前下载的应用数据
     */
    public void deleteDataWhichDownloadBeforeTime(long time) {
        mDb.delete(TableName, KEY_down_time + "< ? ",
                new String[]{time + ""});
    }


    /**
     * 获取安装时间
     */
    public long getInstallTime(String pkg) {
        Cursor cursor = mDb.query(TableName, new String[]{KEY_install_time},
                KEY_pkg + " LIKE ? ", new String[]{pkg}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
            cursor.close();
        }
        return 0;
    }

    /**
     * 获取文件 path
     */
    public String getFilePath(String name) {
        Cursor cursor = mDb.query(TableName, new String[]{KEY_file_path},
                KEY_name + " LIKE ? ", new String[]{name}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            cursor.close();
        }
        return "";
    }

    /**
     * 获取安装回调地址
     */
    public @Nullable
    ArrayList<String> getInstallCallback(String pkg) {
        Cursor cursor = mDb.query(TableName, new String[]{KEY_install_callbacks},
                KEY_pkg + " LIKE ? ", new String[]{pkg}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String string = cursor.getString(0);
                Gson gson = new Gson();
                if (!TextUtils.isEmpty(string)) {

                    try {
                        return gson.fromJson(string, new TypeToken<ArrayList<String>>() {
                        }.getType());
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                cursor.close();
            }
        }
        return null;
    }
}
