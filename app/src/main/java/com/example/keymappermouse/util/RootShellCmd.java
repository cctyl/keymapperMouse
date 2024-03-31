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


    public static int xD = 235;
    public static int yD = 315;
    public static boolean root = false;
    //状态栏高度
    public static int statuBarHeight = 20;
    //鼠标宽高
    public static int measuredHeight = 10;
    public static int measuredWidth = 10;
    public static boolean landscape = false;

    private static String swipeCmd = "input swipe %s %s %s %s %s";
    private static String swipeDownCmd = "input swipe %s 110 %s 206 500";
    private static String swipeUpCmd = "input swipe %s 260 %s 110 500";
    private static String swipeRightCmd = "input swipe 24 %s 216 %s 500";
    private static String swipeLeftCmd = "input swipe 216 %s 24 %s 500";

    public static void swipeUp(int x) {
        x = xD - x;
        execCmd(String.format(swipeUpCmd,x,x));
    }

    public static void swipeRight(int y) {
        y = yD - y;
        execCmd(String.format(swipeRightCmd,y,y));
    }

    public static void swipeLeft(int y) {
        y = yD - y;
        execCmd(String.format(swipeLeftCmd,y,y));
    }

    public static void swipeDown(int x) {
        x = xD - x;
        execCmd(String.format(swipeDownCmd,x,x));
    }


    /**
     * 短按
     * @param x
     * @param y
     */
    public static void simulateTap(int x, int y){
        simulateTap(x,y,100);
    }

    public static void simulateTapLong(int x, int y){
        simulateTap(x,y,600);
    }
    /**
     * 模拟坐标点击
     *
     * @param x
     * @param y
     */
    public static void simulateTap(int x, int y,long tapTime) {

        x = xD - x- (measuredWidth/2);
        if (landscape){
            y = yD - y+(measuredHeight/2);
        }else {
            y = yD - y+statuBarHeight+(measuredHeight/2);
        }

        String format = String.format(swipeCmd, x, y, x, y, tapTime);

        execCmd(format);
    }


    public static void execCmd(String cmd){
        Log.d(TAG, "执行命令: "+cmd);
        if (root){
            ServiceShellUtils.ServiceShellCommandResult serviceShellCommandResult = ServiceShellUtils.execCommand(cmd, true);
            Log.d(TAG, "执行结果: "+serviceShellCommandResult);
        }else {

            try {
                Log.d(TAG, "放入命令: "+cmd);
                SocketClient.cmdQueue.put(cmd);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}