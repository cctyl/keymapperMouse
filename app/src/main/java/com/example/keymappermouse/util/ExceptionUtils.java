package com.example.keymappermouse.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionUtils {
    /**
     * 将给定异常的堆栈跟踪转换为字符串。
     *
     * @param throwable 要转换的异常
     * @return 异常堆栈跟踪的字符串表示形式
     */
    public static String getStackTraceAsString(Throwable throwable) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        throwable.printStackTrace(ps);
        ps.flush();
        return baos.toString();
    }
}
