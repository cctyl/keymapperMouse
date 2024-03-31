base=/system

# 在设备shell中执行
for pid in $(ps | grep 'app_process' | awk '{print $2}')
do
    kill -9 $pid
done

path=`pm path com.example.keymappermouse`
path=${path:8}

export CLASSPATH=$path

exec /system/xbin/nohup app_process /system/bin com.example.keymappermouse.server.AdbProcess >> ./adbserver.log 2>&1 &
#exec  app_process /system/bin com.example.keymappermouse.server.AdbProcess