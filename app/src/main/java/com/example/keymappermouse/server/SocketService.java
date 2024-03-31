package com.example.keymappermouse.server;

import android.util.Log;

import com.example.keymappermouse.util.ExceptionUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketService {

    private static final int PORT = 10500;
    private static final String TAG = "SocketService";


    public SocketService() {
        try {
            System.out.println("尝试启动adb server: ");
            // 利用ServerSocket类启动服务，然后指定一个端口
            ServerSocket serverSocket = new ServerSocket(PORT);

            System.out.println("server running " + PORT + " port");
            ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(3);

            // 新建一个线程池用来并发处理客户端的消息
            ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 3, 5000, TimeUnit.MILLISECONDS, queue);
            //不断的接受新的连接
            while (true) {

                Socket socket = serverSocket.accept();
                System.out.println("new socket connecting...");
                // 接收到新消息
                executor.execute(new MsgProcess(socket));
            }

        } catch (Exception e) {
            String stackTraceString = ExceptionUtils.getStackTraceAsString(e);
            System.out.println("Caught an exception:" + stackTraceString);
            System.out.println("SocketServer create Exception:" + e);
        }
    }

    class MsgProcess implements Runnable {
        Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        private Thread writeThread;
        private Thread readThread;


        private void disconnect(){
            System.out.println("断开连接: ");
            try {
                if (bufferedReader != null) {
                    System.out.println("bufferedReader.close");
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bufferedWriter != null) {
                    System.out.println("bufferedWriter.close");
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (socket != null) {
                    System.out.println("socket.close");
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (writeThread != null) {
                System.out.println(" writeThread.interrupt");
                writeThread.interrupt();
            }

            if (readThread!=null){

                System.out.println(" readThread.interrupt");
                readThread.interrupt();
            }

        }

        public MsgProcess(Socket s) {
            socket = s;
        }

        public void run() {
            try {
                // 通过流读取内容
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                //不断的响应心跳
                writeThread = new Thread(() -> {
                    while (true) {
                        try {
                            bufferedWriter.write("ok");
                            bufferedWriter.flush();
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            System.out.println("writeThread exception:"+e.getMessage());
                            e.printStackTrace();
                            disconnect();
                            break;
                        }
                    }

                });

                readThread = new Thread(() -> {
                    //读取接受到的数据并执行
                    while (true) {
                        String line = null;
                        try {
                            line = bufferedReader.readLine();
                            if (line == null) {
                                Thread.sleep(20);
                                continue;
                            }
                            System.out.println("server receive: " + line);
                            ShellUtil.ExecResult execute = ShellUtil.execute(line);
                            System.out.println("execute result is ：" + execute);
                        } catch (IOException | InterruptedException e) {

                            System.out.println("readThread stop:"+e.getMessage());
                            e.printStackTrace();
                            disconnect();
                            break;
                        }

                    }
                });
                readThread.start();
                writeThread.start();

            } catch (Exception e) {
                System.out.println("socket connection error：" + e);
                disconnect();
            }
        }
    }


}
