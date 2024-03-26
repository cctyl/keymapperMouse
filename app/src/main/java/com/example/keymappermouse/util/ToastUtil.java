package com.example.keymappermouse.util;

import android.content.Context;
import android.widget.Toast;

import com.example.keymappermouse.Application;


public class ToastUtil {

    public static void  show(String desc){
        Toast.makeText(Application.getInstance(), desc, Toast.LENGTH_SHORT).show();
    }
}
