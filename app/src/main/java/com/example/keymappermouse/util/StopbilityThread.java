package com.example.keymappermouse.util;

import android.util.Log;

public class StopbilityThread {
    private static final String TAG = "StopbilityThread";
    private Thread innerThread;
    /**
     * start 方法是否正在被执行
     */
    private volatile boolean start = false;
    /**
     * 停止标记
     */
    private volatile boolean stop = false;
    public void start(Runnable runnable) {
        if (start) {
            return;
        }
        synchronized (this) {
            if (start) {
                return;
            }
            start = true;
        }
        innerThread = new Thread(() -> {
            while (true) {
                if (stop) {
                    Log.d(TAG, "线程退出");
                    destroy();
                    break;
                }
                try {
                    Log.d(TAG, innerThread.getName() + "线程运行中");

                    runnable.run();

                } catch (Exception e) {
                    e.printStackTrace();
                    //出现异常就终止
                    stop = true;
                }
            }
        }, "t1");
        innerThread.start();
    }

    /**
     * 销毁时，处理一些事情
     * 比如把开关设置为false
     */
    private void destroy() {
        start = false;
    }

    /**
     * 停止线程
     */
    public void stop() {
        //设置标记
        stop = true;
        //如果线程还在休眠，那叫醒，让他停止
        if (innerThread!=null){
            innerThread.interrupt();
            Log.d(TAG, innerThread.getName() + "线程终止");
        }

    }

}
