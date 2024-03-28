package com.example.keymappermouse;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

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
        ServiceShellUtils.ServiceShellCommandResult serviceShellCommandResult = ServiceShellUtils.execCommand(String.format(swipeUpCmd,x,x), true);
        Log.d(TAG, " 向上滑动"+ serviceShellCommandResult);
    }

    public static void swipeRight(int y) {
        y = yD - y;
        ServiceShellUtils.ServiceShellCommandResult serviceShellCommandResult = ServiceShellUtils.execCommand(String.format(swipeRightCmd,y,y), true);
        Log.d(TAG, " 向上滑动"+ serviceShellCommandResult);
    }

    public static void swipeLeft(int y) {
        y = yD - y;
        ServiceShellUtils.ServiceShellCommandResult serviceShellCommandResult = ServiceShellUtils.execCommand(String.format(swipeLeftCmd,y,y), true);
        Log.d(TAG, " 向上滑动"+ serviceShellCommandResult);
    }

    public static void swipeDown(int x) {
        x = xD - x;
        ServiceShellUtils.ServiceShellCommandResult serviceShellCommandResult = ServiceShellUtils.execCommand(String.format(swipeDownCmd,x,x), true);
        Log.d(TAG, " 向下滑动"+ serviceShellCommandResult);
    }

    /**
     * 模拟坐标点击
     *
     * @param x
     * @param y
     */
    public static void simulateTap(int x, int y) {

        x = xD - x- (measuredWidth/2);
        if (landscape){
            y = yD - y+(measuredHeight/2);
        }else {
            y = yD - y+statuBarHeight+(measuredHeight/2);
        }

        String format = String.format(swipeCmd, x, y, x, y, 300);
        ServiceShellUtils.ServiceShellCommandResult serviceShellCommandResult = ServiceShellUtils.execCommand(format, true);
        Log.d(TAG, format+" 点击了： " + x + "," + y + "，执行结果为：" + serviceShellCommandResult);

        //按下之后需要马上移动一下


        /*
            长按 500 500 这个位置1000毫秒
            input swipe 500 500 500 500 1000

            从500 500 滑动到 600 500 ，花费500毫秒时间
            input swipe 500 500 600 500 500
         */
    }


}