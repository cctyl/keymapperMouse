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


    private static int xD = 235;
    private static int yD = 315;
    private static String swipeCmd = "input swipe %s %s %s %s %s";
    private static String swipeDownCmd = "input swipe 120 110 120 206 500";
    private static String swipeUpCmd = "input swipe 120 260 120 110 500";
    private static String swipeRightCmd = "input swipe 24 160 216 160 500";
    private static String swipeLeftCmd = "input swipe 216 160 24 160 500";

    public static void swipeUp() {
        ServiceShellUtils.ServiceShellCommandResult serviceShellCommandResult = ServiceShellUtils.execCommand(swipeUpCmd, true);
        Log.d(TAG, " 向上滑动"+ serviceShellCommandResult);
    }

    public static void swipeRight() {
        ServiceShellUtils.ServiceShellCommandResult serviceShellCommandResult = ServiceShellUtils.execCommand(swipeRightCmd, true);
        Log.d(TAG, " 向上滑动"+ serviceShellCommandResult);
    }

    public static void swipeLeft() {
        ServiceShellUtils.ServiceShellCommandResult serviceShellCommandResult = ServiceShellUtils.execCommand(swipeLeftCmd, true);
        Log.d(TAG, " 向上滑动"+ serviceShellCommandResult);
    }

    public static void swipeDown() {
        ServiceShellUtils.ServiceShellCommandResult serviceShellCommandResult = ServiceShellUtils.execCommand(swipeDownCmd, true);
        Log.d(TAG, " 向下滑动"+ serviceShellCommandResult);
    }

    /**
     * 模拟坐标点击
     *
     * @param x
     * @param y
     */
    public static void simulateTap(int x, int y) {

        x = xD - x;
        y = yD - y;
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