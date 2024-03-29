package com.example.keymappermouse.server;

import android.os.Looper;
import android.util.Log;

import com.example.keymappermouse.server.ShellUtil;
import com.example.keymappermouse.server.SocketClient;
import com.example.keymappermouse.server.SocketService;

public class AdbProcess {
    private static String TAG="AdbProcess";
    public static SocketClient socketClient;
    public static void main(String[] args) {
        // 利用looper让线程循环
        Looper.prepareMainLooper();
        System.out.println("*****************adb server starting****************");
        // 开一个子线程启动服务
        new Thread(SocketService::new).start();
        Looper.loop();
    }


}
