package com.example.keymappermouse.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketService {

    private static  final int PORT = 10500;


    public SocketService() {
        try {
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
            System.out.println("SocketServer create Exception:" + e);
        }
    }

    class MsgProcess implements Runnable {
        Socket socket;

        public MsgProcess(Socket s) {
            socket = s;
        }

        public void run() {
            try {
                // 通过流读取内容
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                    try {
                        String line = bufferedReader.readLine();
                        System.out.println("server receive: " + line);

                        ShellUtil.ExecResult execute = ShellUtil.execute(line);
                        System.out.println("execute result is ：" + execute);

                    } catch (IOException e) {
                        System.out.println("execute error");
                        e.printStackTrace();
                        break;
                    }

                }

                bufferedReader.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("socket connection error：" + e);
            }
        }
    }


}
