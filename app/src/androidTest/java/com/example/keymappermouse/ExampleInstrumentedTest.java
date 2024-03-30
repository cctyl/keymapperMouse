package com.example.keymappermouse;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.keymappermouse", appContext.getPackageName());


        String packageName = appContext.getPackageName(); // 获取当前应用的包名
        try {
            PackageManager pm = appContext.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0); // 获取应用信息
            String apkPath = ai.sourceDir; // 获取APK文件路径
            Log.d("APKPath", "APK路径: " + apkPath);
        } catch (PackageManager.NameNotFoundException e) {
            // 应用未安装或无法获取信息，处理异常
            e.printStackTrace();
        }

    }
}