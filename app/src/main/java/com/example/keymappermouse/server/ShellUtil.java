package com.example.keymappermouse.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellUtil {
    private static final String COMMAND_LINE_END = "\n";
    private static final String COMMAND_EXIT = "exit\n";

    public static void runShell(String command) {
        Process process = null;
        BufferedReader bufferedReader = null;
        StringBuilder mShellCommandSB = new StringBuilder();
        System.out.println("runShell: " + command);
        mShellCommandSB.delete(0, mShellCommandSB.length());
        String[] cmd = new String[]{"/system/bin/sh", "-c", command};
        try {
            process = Runtime.getRuntime().exec(cmd);
            bufferedReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                mShellCommandSB.append(line);
            }
            System.out.println("runShell result: " + mShellCommandSB.toString());
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }

            if (process != null) {
                process.destroy();
            }
        }
    }

    public static ExecResult execute(String command) {
        return execute(new String[]{command});
    }

    public static ExecResult execute(String[] commands) {
        if (commands == null || commands.length == 0) {
            return new ExecResult(false, "empty command");
        }
        int result = -1;
        Process process = null;
        DataOutputStream dataOutputStream = null;
        BufferedReader sucResult = null, errResult = null;
        StringBuilder sucMsg = null, errMsg = null;

        try {
            // 获取shell级别的process
            process = Runtime.getRuntime().exec("sh");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) continue;
                System.out.println("execute command: " + command);
                // 执行指令
                dataOutputStream.write(command.getBytes());
                dataOutputStream.writeBytes(COMMAND_LINE_END);
                // 刷新
                dataOutputStream.flush();
            }
            dataOutputStream.writeBytes(COMMAND_EXIT);
            dataOutputStream.flush();
            result = process.waitFor();
            sucMsg = new StringBuilder();
            errMsg = new StringBuilder();
            sucResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = sucResult.readLine()) != null) {
                sucMsg.append(s);
            }
            while ((s = errResult.readLine()) != null) {
                errMsg.append(s);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭资源，防止内存泄漏
                assert dataOutputStream != null;
                dataOutputStream.close();
                assert sucResult != null;
                sucResult.close();
                assert errResult != null;
                errResult.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            process.destroy();
        }
        ExecResult execResult;
        if (result == 0) {
            execResult = new ExecResult(true, sucMsg.toString());
        } else {
            execResult = new ExecResult(false, errMsg.toString());
        }
        // 返回执行结果
        return execResult;
    }

    public static class ExecResult {
        private boolean success;
        private String message;

        public ExecResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean getSuccess() {
            return this.success;
        }

        public String getMessage() {
            return this.message;
        }
    }
}
