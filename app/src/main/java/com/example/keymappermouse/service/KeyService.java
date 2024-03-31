package com.example.keymappermouse.service;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_MENU;
import static android.view.KeyEvent.KEYCODE_STAR;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.example.keymappermouse.server.SocketClient;
import com.example.keymappermouse.util.Function;
import com.example.keymappermouse.util.RootShellCmd;
import com.example.keymappermouse.util.ToastUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            KEYCODE_MENU
    );

    /**
     * 是否开启按键映射
     */
    private boolean openMapper = true;

    private int modeIndex = 0;
    private List<Function<KeyEvent, Boolean>> modeArr = Arrays.asList(
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

                        case KeyEvent.KEYCODE_5:
                            //长按
                            runAsync(() -> {
                                        Log.d(TAG, "点击: "+position[0]+","+position[1]);
                                        RootShellCmd.simulateTapLong(position[0], position[1]);
                                    }
                            );
                            break;
                        case KeyEvent.KEYCODE_ENTER:
                            Log.d(TAG, "按下屏幕: ");
                            runAsync(() -> {
                                        Log.d(TAG, "点击: "+position[0]+","+position[1]);
                                        RootShellCmd.simulateTap(position[0], position[1]);
                                    }
                            );
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            distanceX += 1;
                            FloatViewService.INSTANCE.updatPosition(distanceX, 0);
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

            //地下城模式
            (keyEvent) -> {
                return super.onKeyEvent(keyEvent);
            }

    );

    private String[] toastArr = {
            "普通模式",
            "地下城模式"
    };

    /**
     * 是否是穿透模式
     */
    private boolean penetrate = false;
    private long pressPenetrateTime = 0L;

    /**
     * call是否被按下
     */
    private boolean callPress = false;
    private long callPressTime = 0L;
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

        if (key == KeyEvent.KEYCODE_CALL && event.getAction() == ACTION_DOWN ) {
            callPressTime = System.currentTimeMillis();
            callPress = true;
            return true;
        }

        if (key == KeyEvent.KEYCODE_CALL && event.getAction() == ACTION_UP) {
            callPress = false;
            //快速的按，说明是切换模式
            if (System.currentTimeMillis()-callPressTime<200){
                openMapper = !openMapper;
                ToastUtil.show("按键映射：" + (openMapper ? "开" : "关"));
            }
            return true;
        }


        //按下call的时候按下*，则切换模式
        if (key == KEYCODE_STAR && event.getAction() == ACTION_DOWN && callPress){
            Log.d(TAG, "切换模式了 ");
            modeIndex = (++modeIndex) % toastArr.length;
            ToastUtil.show(toastArr[modeIndex]);
            return true;
        }
        if (key == KEYCODE_STAR && event.getAction() == ACTION_DOWN){
            //如果只是单纯的按下*，则不处理
            return  super.onKeyEvent(event);
        }




        Log.d(TAG, "走处理了 ");
        if (openMapper) {
            Function<KeyEvent, Boolean> function = modeArr.get(modeIndex);
            return function.apply(event);
        } else {
            //关闭按键映射总开关时
            return super.onKeyEvent(event);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 检查屏幕方向是否发生了改变
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "切换到横屏");
            landscape = true;
            // 处理横屏逻辑
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "切换到竖屏");
            // 处理竖屏逻辑
            landscape = false;
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