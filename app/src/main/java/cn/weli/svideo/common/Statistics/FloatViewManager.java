package cn.weli.svideo.common.Statistics;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.util.LinkedList;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.utils.DensityUtil;

public class FloatViewManager {
    private static FloatViewManager mInstance;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private Context mContext;
    private View rootView;
    private RecyclerView listView;
    private ImageView iv_tongji_switch, iv_tongji_clear;
    private FloatViewAdapter mAdapter;
    private boolean isAdded = false;

    private boolean miniMode = true;

    private LinkedList<EventDataBean> mData = new LinkedList<>();

    private FloatViewManager(Context context) {
        this.mContext = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        rootView = createFloatView();
    }

    public static FloatViewManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (FloatViewManager.class) {
                if (mInstance == null) {
                    mInstance = new FloatViewManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private View createFloatView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_float_view, null);

        listView = rootView.findViewById(R.id.listView);
        iv_tongji_switch = rootView.findViewById(R.id.iv_tongji_switch);
        iv_tongji_clear = rootView.findViewById(R.id.iv_tongji_clear);
        listView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new FloatViewAdapter(mContext, mData);
        listView.setAdapter(mAdapter);
        return rootView;
    }

    public void addWindow() {
        if (isAdded) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_PHONE);
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.RGBA_8888;
        isAdded = true;
        windowManager.addView(rootView, params);

        if (miniMode) {
            turnOff();
        } else {
            turnOn();
        }

        iv_tongji_switch.setImageResource(R.drawable.debug_black);
        iv_tongji_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listView.getVisibility() == View.VISIBLE) {
                    miniMode = true;
                    turnOff();

                } else {
                    miniMode = false;
                    turnOn();
                }
            }
        });

        iv_tongji_switch.setOnTouchListener(new View.OnTouchListener() {

            float touchDownY = 0;
            float startY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchDownY = event.getRawY();
                    startY = params.y;
                }
                params.y = (int) (startY + event.getRawY() - touchDownY);
                //刷新
                windowManager.updateViewLayout(rootView, params);
                return false;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });


        iv_tongji_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.size() > 0) {
                    mData.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void removeView() {
        windowManager.removeView(rootView);

    }

    private void turnOn() {

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DensityUtil.dp2px(mContext, 250);
        params.y = 0;
        params.x = 0;
        params.gravity = Gravity.TOP;
        windowManager.updateViewLayout(rootView, params);

        iv_tongji_switch.setImageResource(R.drawable.debug_green);
        rootView.setBackgroundColor(mContext.getResources().getColor(R.color.color_60_black));
        listView.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams lps = (RelativeLayout.LayoutParams) iv_tongji_switch.getLayoutParams();
        lps.topMargin = DensityUtil.dp2px(mContext, 60);
        iv_tongji_switch.setLayoutParams(lps);

    }

    private void turnOff() {
        params.width = DensityUtil.dp2px(mContext, 30);
        params.height = DensityUtil.dp2px(mContext, 30);
        params.y = DensityUtil.dp2px(mContext, 60);
        params.x = 0;
        params.gravity = Gravity.TOP | Gravity.RIGHT;

        windowManager.updateViewLayout(rootView, params);

        iv_tongji_switch.setImageResource(R.drawable.debug_black);
        rootView.setBackgroundColor(mContext.getResources().getColor(R.color.color_transparent));
        listView.setVisibility(View.GONE);

        RelativeLayout.LayoutParams lps = (RelativeLayout.LayoutParams) iv_tongji_switch.getLayoutParams();
        lps.topMargin = 0;
        iv_tongji_switch.setLayoutParams(lps);

    }

    public void addEvent(EventDataBean adEventBean) {
        mData.add(adEventBean);
        mAdapter.notifyItemInserted(mData.size() - 1);
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.scrollToPosition(mData.size() - 1);
            }
        });
    }

}
