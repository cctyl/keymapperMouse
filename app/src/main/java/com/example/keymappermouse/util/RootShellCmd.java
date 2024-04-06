package com.example.keymappermouse.util;

import android.util.Log;

import com.example.keymappermouse.server.SocketClient;

/**
 * 用root权限执行Linux下的Shell指令
 *
 * @author jzj
 * @since 2014-09-09
 */
public class RootShellCmd {

    private static final String TAG = "RootShellCmd";


    public static int screenWidth = 235;
    public static int screenHeight = 315;
    public static boolean root = false;
    //状态栏高度
    public static int statuBarHeight = 20;
    //鼠标宽高
    public static int measuredHeight = 10;
    public static int measuredWidth = 10;
    public static boolean landscape = false;

    private static String swipeCmd = "input swipe %s %s %s %s %s";
    private static String swipeDownCmd = "input swipe %s 110 %s 206 150";
    private static String swipeUpCmd = "input swipe %s 260 %s 110 150";
    private static String swipeRightCmd = "input swipe 24 %s 216 %s 150";
    private static String swipeLeftCmd = "input swipe 216 %s 24 %s 150";
    private static String recentAppCmd = "input keyevent --longpress KEYCODE_HOME";

    public static void swipeUp(int x, int y) {

        int[] position = handleClickPosition(x, y);
        x = position[0];
        y = position[1];
        execCmd(String.format(swipeCmd, x, y, x, y - 100, 150));
    }

    public static void swipeRight(int y,int x) {

        int[] position = handleClickPosition(x, y);
        x = position[0];
        y = position[1];
        execCmd(String.format(swipeCmd, x, y, x+100, y, 150));
    }

    public static void swipeLeft(int y,int x) {



        int[] position = handleClickPosition(x, y);
        x = position[0];
        y = position[1];
        execCmd(String.format(swipeCmd, x, y, x-100, y, 150));
    }

    public static void swipeDown(int x,int y) {



        int[] position = handleClickPosition(x, y);
        x = position[0];
        y = position[1];
        execCmd(String.format(swipeCmd, x, y, x, y + 100, 150));
    }

    public static void recentApp() {
        execCmd(recentAppCmd);
    }


    /**
     * 短按
     *
     * @param x
     * @param y
     */
    public static void simulateTap(int x, int y) {
        simulateTap(x, y, 100);
    }

    public static void simulateTapLong(int x, int y) {
        simulateTap(x, y, 600);
    }

    private static int[] handleClickPosition(int x,int y){
        x = screenWidth - x - (measuredWidth / 2);
        if (landscape) {
            y = screenHeight - y - (measuredHeight / 2);
        } else {
            y = screenHeight - y  - (measuredHeight / 2);
        }

        x = Math.max(x, 0);
        y = Math.max(y, 0);

        x = Math.min(x,screenWidth);
        y = Math.min(y,screenHeight);

        return new int[]{x,y};
    }

    /**
     * 模拟坐标点击
     *
     * @param x
     * @param y
     */
    public static void simulateTap(int x, int y, long tapTime) {




        int[] position = handleClickPosition(x, y);
        x = position[0];
        y = position[1];

        Log.d(TAG, String.format("当前位置(wmParams) %s,%s ,点击时的坐标： %s,%s,屏幕宽：%s，高%s,状态栏高度：%s," +
                        "悬浮鼠标宽度：%s，高度：%s",
               x,
            y,
                x,
                y,


                RootShellCmd.screenWidth,
                RootShellCmd.screenHeight,
                RootShellCmd.statuBarHeight,
                RootShellCmd.measuredHeight,
                RootShellCmd.measuredWidth)
        );


        String format = String.format(swipeCmd, x, y, x, y, tapTime);

        execCmd(format);
    }


    public static void execCmd(String cmd) {
        Log.d(TAG, "执行命令: " + cmd);
        if (root) {
            ServiceShellUtils.ServiceShellCommandResult serviceShellCommandResult = ServiceShellUtils.execCommand(cmd, true);
            Log.d(TAG, "执行结果: " + serviceShellCommandResult);
        } else {

            try {
                Log.d(TAG, "放入命令: " + cmd);
                SocketClient.cmdQueue.put(cmd);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}