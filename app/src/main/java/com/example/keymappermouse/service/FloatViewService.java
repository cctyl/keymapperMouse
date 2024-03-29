package com.example.keymappermouse.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.example.keymappermouse.R;
import com.example.keymappermouse.util.RootShellCmd;
//import android.widget.Toast;

public class FloatViewService extends Service {
    private static final String TAG = "FloatViewService";
    private FrameLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private Button mFloatView;
    private int screenWidth, screenHeight;
    private View menuView;

    private int mTouchSlop;
    public static FloatViewService INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
        INSTANCE = this;
        DisplayMetrics dm = FloatViewService.this.getResources().getDisplayMetrics();

        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - statuBarHeight(FloatViewService.this) - mFloatView.getMeasuredHeight() / 2;

        Log.d(TAG, "screenWidth= " + screenWidth + "," + "screenHeight=" + screenHeight);

        Log.d(TAG, "measuredHeight(): "+mFloatView.getMeasuredHeight()+", MeasuredWidth="+mFloatView.getMeasuredWidth());
        RootShellCmd.xD = screenWidth;
        RootShellCmd.yD = screenHeight;
        RootShellCmd.statuBarHeight = statuBarHeight(FloatViewService.this);
        RootShellCmd.measuredHeight = mFloatView.getMeasuredHeight();
        RootShellCmd.measuredWidth = mFloatView.getMeasuredWidth();
        mTouchSlop = ViewConfiguration.get(FloatViewService.this).getScaledTouchSlop();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics dm = FloatViewService.this.getResources().getDisplayMetrics();

        screenWidth = dm.widthPixels;

        // 检查屏幕方向是否发生了改变
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "切换到横屏");
            // 处理横屏逻辑
            screenHeight = dm.heightPixels  - mFloatView.getMeasuredHeight() / 2;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "切换到竖屏");
            // 处理竖屏逻辑
            screenHeight = dm.heightPixels - statuBarHeight(FloatViewService.this) - mFloatView.getMeasuredHeight() / 2;
        }


        RootShellCmd.xD = screenWidth;
        RootShellCmd.yD = screenHeight;
        RootShellCmd.statuBarHeight = 0;
        RootShellCmd.measuredHeight = mFloatView.getMeasuredHeight();
        RootShellCmd.measuredWidth = mFloatView.getMeasuredWidth();

        Log.d(TAG, "切换后：screenWidth= " + screenWidth + "," + "screenHeight=" + screenHeight+",statuBarHeight= "+RootShellCmd.statuBarHeight);

        // 根据需要处理其他配置变更，如屏幕尺寸、键盘可用性等
    }

    public void updatPosition(int distanceX, int distanceY) {
        wmParams.x = wmParams.x - distanceX;
        wmParams.y = wmParams.y - distanceY;


        // 边界检查
        if (wmParams.x < 0) {
            wmParams.x = 0; // 限制 x 值不小于 0
        } else if (wmParams.x > screenWidth) {
            wmParams.x = screenWidth;
        }

        if (wmParams.y < 0) {
            wmParams.y = 0; // 限制 y 值不小于 0
        } else if (wmParams.y > screenHeight) {
            wmParams.y = screenHeight;
        }
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
    }


    public int[] printPosition() {
        int absoluteX = wmParams.x;
        int absoluteY = wmParams.y;
        //此处的实际位置，与屏幕位置，存在差距
        //差距在于，y方向少了20的状态栏高度，以及鼠标自身的10高度
        //这里获得的0,0 实际是0,20
        Log.d(TAG, (screenWidth-absoluteX) + "," + (screenHeight- absoluteY));
        return new int[]{absoluteX, absoluteY};
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) FloatViewService.this.getSystemService(Context.WINDOW_SERVICE);

        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        wmParams.windowAnimations = android.R.style.Animation_Dialog;

        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(FloatViewService.this);
        mFloatLayout = (FrameLayout) inflater.inflate(R.layout.floatball, null);
        mWindowManager.addView(mFloatLayout, wmParams);

        mFloatView = mFloatLayout.findViewById(R.id.float_image);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        Log.d(TAG, screenWidth + "," + screenHeight);

    }


    void mRemoveView(View v) {
        if (mWindowManager != null && v != null && v.isShown())
            mWindowManager.removeViewImmediate(v);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mRemoveView(mFloatLayout);
        mRemoveView(menuView);
        wmParams = null;
        mWindowManager = null;
        menuView = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int statuBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


}