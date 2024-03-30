package com.example.keymappermouse.server;

import android.util.Log;

import com.example.keymappermouse.util.StopbilityThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SocketClient {
    private static final String TAG = "HackRoot SocketClient";
    private static final int PORT = 10500;
    private static PrintWriter printWriter;
    public static final BlockingQueue<String> cmdQueue = new ArrayBlockingQueue<>(2);
    private static StopbilityThread stopbilityThread;


    public static void initSocketClient() {
        if (stopbilityThread!=null){
            stopbilityThread.stop();
        }else {
            stopbilityThread = new StopbilityThread();
        }

        stopbilityThread.start(
                () -> {
                    Log.d(TAG, "开始建立连接: ");
                    Socket socket = new Socket();
                    try {
                        // 与hackserver建立连接
                        socket.connect(new InetSocketAddress("127.0.0.1", PORT), 3000);
                        socket.setSoTimeout(3000);

                        printWriter = new PrintWriter(socket.getOutputStream(), true);
                        Log.d(TAG, "建立连接成功: ");
                        while (true) {
                            String cmd = cmdQueue.take();
                            Log.d(TAG, "获得并发生命令: " + cmd);
                            // 发送指令
                            printWriter.println(cmd);
                            printWriter.flush();

                        }

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        try {
                            socket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        printWriter.close();
                        Log.d(TAG, "client send fail: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
        );
    }


}
