package com.example.keymappermouse;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
//import android.widget.Toast;

public class FloatViewService extends Service {
    private static final String TAG = "FloatViewService";
    private FrameLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager, wm;
    private Button mFloatView;
    private int screenWidth, screenHeight;
    private View menuView;
    private DisplayMetrics outMetrics;
    private WindowManager.LayoutParams menuWmParams;
    private final int LEFT_FLAG   = 0;
    private final int TOP_FLAG    = 1;
    private final int BOTTOM_FLAG = 2;
    private final int RIGHT_FLAG  = 3;
    private int mTouchSlop;
    private boolean isClick;
    public static FloatViewService INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
        INSTANCE = this;
        DisplayMetrics dm = FloatViewService.this.getResources().getDisplayMetrics();

        screenWidth  = dm.widthPixels;
        screenHeight = dm.heightPixels - statuBarHeight(FloatViewService.this) - mFloatView.getMeasuredHeight() / 2;
        mTouchSlop   = ViewConfiguration.get(FloatViewService.this).getScaledTouchSlop();

    }

    public void updatPosition(int distanceX,int distanceY){
        wmParams.x = wmParams.x - distanceX;
        wmParams.y = wmParams.y - distanceY;

        // 边界检查
//        if (wmParams.x < 0) {
//            wmParams.x = 0; // 限制 x 值不小于 0
//        } else if (wmParams.x + mFloatLayout.getWidth() > screenWidth) {
//            wmParams.x = screenWidth - mFloatLayout.getWidth(); // 限制 x 值不超过屏幕宽度减去 View 的宽度
//        }
//
//        if (wmParams.y < 0) {
//            wmParams.y = 0; // 限制 y 值不小于 0
//        } else if (wmParams.y + mFloatLayout.getHeight() > screenHeight) {
//            wmParams.y = screenHeight - mFloatLayout.getHeight(); // 限制 y 值不超过屏幕高度减去 View 的高度
//        }

        // 边界检查
        if (wmParams.x < 0) {
            wmParams.x = 0; // 限制 x 值不小于 0
        } else if (wmParams.x >240) {
            wmParams.x =40;
        }

        if (wmParams.y < 0) {
            wmParams.y = 0; // 限制 y 值不小于 0
        } else if (wmParams.y >292){
            wmParams.y = 292;
        }
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
    }


    public int[] printPosition(){
        int absoluteX =wmParams. x ;

        int absoluteY =wmParams. y ;

        Log.d(TAG, absoluteX+","+absoluteY);

        return new int[]{absoluteX,absoluteY};
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager   = (WindowManager) FloatViewService.this.getSystemService(Context.WINDOW_SERVICE);

        wmParams.type    = LayoutParams.TYPE_PHONE;
        wmParams.format  = PixelFormat.RGBA_8888;
        wmParams.flags   = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        wmParams.windowAnimations = android.R.style.Animation_Dialog;

        wmParams.x = 0;
        wmParams.y = 150;
        wmParams.width  = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(FloatViewService.this);
        mFloatLayout = (FrameLayout) inflater.inflate(R.layout.floatball, null);
        mWindowManager.addView(mFloatLayout, wmParams);

        mFloatView   = (Button) mFloatLayout.findViewById(R.id.float_image);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        Log.d(TAG, screenWidth+","+screenHeight);

    }



    private void createMenuView(int mGravity) {
        if (wm == null)           wm = (WindowManager) FloatViewService.this.getSystemService(Context.WINDOW_SERVICE);
        if (menuWmParams == null) menuWmParams = new WindowManager.LayoutParams();

        menuWmParams.type   = WindowManager.LayoutParams.TYPE_PHONE;
        menuWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        menuWmParams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        menuWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        menuWmParams.windowAnimations = android.R.style.Animation_Dialog;

        if(outMetrics == null) outMetrics = new DisplayMetrics();

        menuWmParams.x = outMetrics.widthPixels;
        menuWmParams.y = outMetrics.heightPixels;

        menuWmParams.width  = WindowManager.LayoutParams.WRAP_CONTENT;
        menuWmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        menuWmParams.format = PixelFormat.RGBA_8888;
        wm.addView(menuView, menuWmParams);
    }




    private OnTouchListener menuTouch = new OnTouchListener() {
        @Override
        public boolean onTouch(View arg0, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_OUTSIDE:
                    mFloatLayout.setVisibility(View.VISIBLE);
                    mRemoveView(menuView);
                    break;
            }
            return false;
        }
    };

    void mRemoveView(View v) {
        if (mWindowManager != null && v != null && v.isShown())
            mWindowManager.removeViewImmediate(v);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mRemoveView(mFloatLayout);
        mRemoveView(menuView);

        wmParams       = null;
        mWindowManager = null;

        menuWmParams = null;
        menuView     = null;
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