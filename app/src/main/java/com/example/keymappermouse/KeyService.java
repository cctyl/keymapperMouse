package com.example.keymappermouse;

import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_CALL;
import static android.view.KeyEvent.KEYCODE_MENU;
import static android.view.KeyEvent.KEYCODE_STAR;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class KeyService extends AccessibilityService {
    private static final String TAG = "MyService";
    private Instrumentation instrumentation;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public void runAsync(Runnable runnable) {
        executorService.submit(runnable);
    }

    private int linming = 5;
    private int prevKey;


    private boolean open = true;

    private List<Integer> ignoreKeyList = Arrays.asList(
            KEYCODE_BACK,
            KEYCODE_MENU,
            KEYCODE_CALL,
            KEYCODE_STAR


    );
    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int key = event.getKeyCode();


        //不处理直接穿透的按键
        if (ignoreKeyList.contains(key)){
            return super.onKeyEvent(event);
        }


        if (key == KeyEvent.KEYCODE_POUND && event.getAction() == KeyEvent.ACTION_UP) {
            open = !open;
        }
        if (!open) {
            return super.onKeyEvent(event);
        }

        if (key == prevKey) {
            linming++;
        } else {
            prevKey = key;
            linming = 5;
        }


        Log.d(TAG, " MyService onKeyEvent ");
        Log.d(TAG, event.toString());


        int distanceX = 0;
        int distanceY = 0;
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (key) {

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    distanceX -= 1;
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    distanceX += 1;
                    break;

                case KeyEvent.KEYCODE_DPAD_UP:
                    distanceY -= 1;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:

                    distanceY += 1;
                    break;

                case KeyEvent.KEYCODE_8:
                    //向下滑动
                    RootShellCmd.swipeDown();
                    break;
                case KeyEvent.KEYCODE_2:
                    //向下滑动
                    RootShellCmd.swipeUp();
                    break;

                case KeyEvent.KEYCODE_4:
                    //向下滑动
                    RootShellCmd.swipeLeft();
                    break;

                case KeyEvent.KEYCODE_6:
                    //向下滑动
                    RootShellCmd.swipeRight();
                    break;
                case KeyEvent.KEYCODE_ENTER:

                    Log.d(TAG, "按下屏幕: ");
                    int[] position = FloatViewService.INSTANCE.printPosition();

                    distanceX += 1;
                    runAsync(() -> {
                                Log.d(TAG, "点击: ");
                                RootShellCmd.simulateTap(position[0], position[1]);
                            }
                    );


                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    FloatViewService.INSTANCE.updatPosition(distanceX * linming, 0);

                    return true;
                default:
                    return super.onKeyEvent(event);

            }


            FloatViewService.INSTANCE.updatPosition(distanceX * linming, distanceY * linming);

            Log.d(TAG, "distanceX= " + distanceX + ",distanceY=" + distanceY);
            FloatViewService.INSTANCE.printPosition();
        }
        return true;
    }

    private void instrumentInput(int[] ints) {
        try {
            // 将鼠标移动到指定位置
            instrumentation.sendPointerSync(MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_MOVE,
                    ints[0],
                    ints[1],
                    0
            ));

            // 模拟点击屏幕
            instrumentation.sendPointerSync(MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN,
                    ints[0],
                    ints[1],
                    0
            ));
            instrumentation.sendPointerSync(MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_UP,
                    ints[0],
                    ints[1],
                    0
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onCreate() {
        Log.d("key", "keyservice::onCreate");
        super.onCreate();
        instrumentation = new Instrumentation();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub
    }

}