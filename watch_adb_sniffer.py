import subprocess
import json
import socket
import re
import sys
import threading

# UDP 转发地址 (本地您的 雷达上位机)
UDP_IP = "127.0.0.1"
UDP_PORT = 9999

def start_adb_logcat():
    print("🚀 正在启动 手机 ADB 日志嗅探器 (免修改 Android 源码)...")
    print("请确保：\n 1. 手机已用 Type-C 线连接电脑\n 2. 手机开启了【USB 调试】\n 3. 手机屏幕上正在运行 Youhong (2208) 测试 App")
    
    # 清理旧日志日志
    subprocess.run(["adb", "logcat", "-c"], shell=True)

    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    
    # 启动 logcat，并只过滤我们需要的 Tag (比如 info 或者是心率相关的)
    # 因为我们在 MainActivity 看到：Log.e("info",map.toString());
    process = subprocess.Popen(['adb', 'logcat', 'info:E', '*:S'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, encoding='utf-8', errors='ignore')
    
    print("\n✅ 嗅探器已就绪，正在静默监听手环数据...\n")
    
    while True:
        line = process.stdout.readline()
        if not line:
            break
            
        # 简单清洗并提取里面有用的心率/血氧
        # 友宏字典大概内容: {type=..., heartRate=75, bloodOxygen=98, ...}
        # 这里用正则做宽泛的截获
        
        hr_match = re.search(r'(heartRate|hr|HeartRate)=?(\d+)', line, re.IGNORECASE)
        spo2_match = re.search(r'(bloodOxygen|spo2|BloodOxygen|oxygen)=?(\d+)', line, re.IGNORECASE)
        
        if hr_match or spo2_match:
            hr_val = hr_match.group(2) if hr_match else "0"
            spo2_val = spo2_match.group(2) if spo2_match else "0"
            
            # 如果两个都是 0 ，可能捕捉到错误的空包，跳过
            if hr_val == "0" and spo2_val == "0":
                continue

            packet = {
                "hr": int(hr_val),
                "spo2": int(spo2_val)
            }
            
            print(f"📡 拦截到手环数据: {packet} => 正在转发给雷达上位机...")
            try:
                # 转发给我们的 radar_tester.py
                sock.sendto(json.dumps(packet).encode('utf-8'), (UDP_IP, UDP_PORT))
            except Exception as e:
                pass

if __name__ == '__main__':
    try:
        start_adb_logcat()
    except KeyboardInterrupt:
        print("\n⏹ 已退出嗅探器。")
    except Exception as e:
        print(f"❌ 运行出错，请检查是否已安装 adb 及环境变量中配置了 adb: {str(e)}")
