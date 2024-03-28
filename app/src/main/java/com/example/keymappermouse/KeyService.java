package com.example.keymappermouse;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_CALL;
import static android.view.KeyEvent.KEYCODE_MENU;
import static android.view.KeyEvent.KEYCODE_STAR;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

import com.example.keymappermouse.util.MyFunction;
import com.example.keymappermouse.util.ToastUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class KeyService extends AccessibilityService {
    private static final String TAG = "MyService";
    private AudioManager audioManager;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public void runAsync(Runnable runnable) {
        executorService.submit(runnable);
    }

    private int linming = 20;
    private int prevKey;
    /**
     * 是否横屏
     */
    private boolean landscape = false;
    private List<Integer> ignoreKeyList = Arrays.asList(
            KEYCODE_BACK,
            KEYCODE_MENU,

            KEYCODE_STAR
    );

    private int modeIndex = 0;
    private int modeLimit = 2;
    private List<MyFunction<KeyEvent, Boolean>> modeArr = Arrays.asList(
            //开启开关
            (keyEvent) -> {
                int key = keyEvent.getKeyCode();
                if (key == prevKey) {
                    linming++;
                } else {
                    prevKey = key;
                    linming = 20;
                }

                int distanceX = 0;
                int distanceY = 0;
                if (keyEvent.getAction() == ACTION_UP) {
                    int[] position = FloatViewService.INSTANCE.printPosition();
                    switch (key) {

                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            if (landscape) {
                                distanceY += 1;
                            } else {
                                distanceX -= 1;
                            }

                            break;

                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            if (landscape) {
                                distanceY -= 1;
                            } else {
                                distanceX += 1;
                            }
                            break;

                        case KeyEvent.KEYCODE_DPAD_UP:
                            if (landscape) {
                                distanceX -= 1;
                            } else {
                                distanceY -= 1;
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            if (landscape) {
                                distanceX += 1;
                            } else {
                                distanceY += 1;
                            }
                            break;

                        case KeyEvent.KEYCODE_8:

                            //向下滑动
                            RootShellCmd.swipeUp(position[0]);
                            break;
                        case KeyEvent.KEYCODE_2:
                            //向下滑动
                            RootShellCmd.swipeDown(position[0]);
                            break;

                        case KeyEvent.KEYCODE_4:
                            //向下滑动
                            RootShellCmd.swipeRight(position[1]);
                            break;

                        case KeyEvent.KEYCODE_6:
                            //向下滑动
                            RootShellCmd.swipeLeft(position[1]);
                            break;
                        case KeyEvent.KEYCODE_ENTER:

                            Log.d(TAG, "按下屏幕: ");

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

                        case KeyEvent.KEYCODE_7:
                            decreaseVolume();
                            break;
                        case KeyEvent.KEYCODE_9:
                            increaseVolume();
                            break;
                        default:
                            return super.onKeyEvent(keyEvent);

                    }

                    FloatViewService.INSTANCE.updatPosition(distanceX * linming, distanceY * linming);
                    Log.d(TAG, "distanceX= " + distanceX * linming + ",distanceY=" + distanceY * linming);
                    FloatViewService.INSTANCE.printPosition();
                }
                return true;
            },
            //关闭开关
            (keyEvent) -> {
                return super.onKeyEvent(keyEvent);
            },

            //地下城模式
            (keyEvent) -> {
                return super.onKeyEvent(keyEvent);
            }

    );

    private String[] toastArr = {
            "开启按键映射",
            "关闭按键映射",
            "地下城模式"
    };

    /**
     * 是否是穿透模式
     */
    private boolean penetrate = false;
    private long pressPenetrateTime = 0L;

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int key = event.getKeyCode();
        int action = event.getAction();
        Log.d(TAG, " MyService onKeyEvent " + event);


        //按下# 号键，表示开启穿透
        if (key == KeyEvent.KEYCODE_POUND && action == KeyEvent.ACTION_DOWN) {
            penetrate = true;
            pressPenetrateTime = System.currentTimeMillis();
            Log.d(TAG, "# 按下 ");
            return true;
        }
        //松开#号键表示关闭穿透
        if (key == KeyEvent.KEYCODE_POUND && action == KeyEvent.ACTION_UP) {
            penetrate = false;

            //如果按下时间和松开时间很短，认为是正常按下#,则穿透
            long interval = System.currentTimeMillis() - pressPenetrateTime;
            Log.d(TAG, "# 按下时长为： " + interval);
            if (interval < 200) {
                return super.onKeyEvent(event);
            } else {
                return true;
            }
        }
        //不处理直接穿透的按键
        //按下#号键时直接穿透
        if (penetrate || ignoreKeyList.contains(key) || FloatViewService.INSTANCE == null) {
            Log.d(TAG, "穿透了 ");
            return super.onKeyEvent(event);
        }

        if (key == KeyEvent.KEYCODE_CALL && event.getAction() == ACTION_UP) {
            Log.d(TAG, "切换模式了 ");
            modeIndex = (++modeIndex) % modeLimit;
            ToastUtil.show(toastArr[modeIndex]);
            return true;
        }

        Log.d(TAG, "走处理了 ");
        MyFunction<KeyEvent, Boolean> function = modeArr.get(modeIndex);
        return function.apply(event);


    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // 检查屏幕方向是否发生了改变
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "切换到横屏");
            landscape=true;
            // 处理横屏逻辑
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "切换到竖屏");
            // 处理竖屏逻辑
            landscape=false;
        }


    }

    private void increaseVolume() {
        // 增加系统音量（默认调整媒体音量）
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    private void decreaseVolume() {
        // 减少系统音量（默认调整媒体音量）
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onCreate() {
        Log.d("key", "keyservice::onCreate");
        super.onCreate();

        // 获取系统音频管理器
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub
    }

}