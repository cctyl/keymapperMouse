base=/system

path=`pm path com.example.keymappermouse`
path=${path:8}

export CLASSPATH=$path
rm /sdcard/adbserver.log
exec /system/xbin/nohup app_process /system/bin com.example.keymappermouse.server.AdbProcess >> ./adbserver.log 2>&1 &
#exec  app_process /system/bin com.example.keymappermouse.server.AdbProcess