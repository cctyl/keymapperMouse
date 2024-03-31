package com.example.keymappermouse.server;

import android.util.Log;

import com.example.keymappermouse.util.StopbilityThread;
import com.example.keymappermouse.util.ToastUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static Thread readThread;


    public static void initSocketClient() {
        if (stopbilityThread != null) {
            stopbilityThread.stop();
        } else {
            stopbilityThread = new StopbilityThread();
        }

        stopbilityThread.start(
                () -> {
                    Log.d(TAG, "线程启动: ");
                    //一旦出现异常，就重新建立连接
                    while (true) {
                        Log.d(TAG, "开始建立连接: ");
                        socket = new Socket();
                        try {
                            // 与hackserver建立连接
                            socket.connect(new InetSocketAddress("127.0.0.1", PORT), 300);
                            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            printWriter = new PrintWriter(socket.getOutputStream(), true);
                            Log.d(TAG, "建立连接成功: ");

                            //不断的读取输入流
                            readThread = new Thread(() -> {
                                  while (true){
                                      String s = null;
                                      try {
                                          s = bufferedReader.readLine();
                                          Log.d(TAG, "收到消息: "+s);
                                      } catch (IOException e) {
                                          e.printStackTrace();
                                      }finally {

                                          try {
                                              if (bufferedReader!=null){
                                                  bufferedReader.close();
                                              }
                                          } catch (IOException e) {
                                              e.printStackTrace();
                                          }

                                          disconnectSocket();
                                          break;
                                      }
                                  }
                              });
                            readThread.start();



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
                                Log.d(TAG, "socket.close     ");
                                socket.close();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                            if (printWriter != null) {
                                Log.d(TAG, "printWriter.close     ");
                                printWriter.close();
                            }

                            if (readThread!=null){
                                readThread.interrupt();
                            }
                            Log.d(TAG, "client send fail: " + e.getMessage());

                        }

                        if (stopbilityThread.stop){
                            //如果主动停止，那么不再创建
                            break;
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );
    }

    public static void disconnectSocket(){
        stopbilityThread.stop();
    }

}
