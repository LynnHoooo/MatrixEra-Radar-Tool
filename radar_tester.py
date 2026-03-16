# -*- coding: utf-8 -*-
import sys
import serial
import serial.tools.list_ports
import csv
import datetime
import socket
import json
from PyQt6.QtWidgets import (QApplication, QMainWindow, QWidget, QVBoxLayout, 
                             QHBoxLayout, QLabel, QPushButton, QComboBox, 
                             QTextEdit, QFrame, QGridLayout)
from PyQt6.QtCore import QThread, pyqtSignal, Qt, QTimer
from PyQt6.QtGui import QFont, QColor, QPalette

# --- 协议解析核心逻辑 ---
class SYProtocolParser:
    """
    R60ABD1 SY 协议解析器
    格式: 53 59 [控制字] [标识字] [长度H] [长度L] [数据] [校验] 54 43
    """
    def __init__(self):
        self.buffer = bytearray()
        
    def parse(self, new_data):
        self.buffer.extend(new_data)
        packets = []
        
        while len(self.buffer) >= 10:  # 最小包长 (53 59 ... 54 43)
            # 查找包头
            header_idx = self.buffer.find(b'\x53\x59')
            if header_idx == -1:
                self.buffer.clear()
                break
            
            if header_idx > 0:
                del self.buffer[:header_idx]
            
            if len(self.buffer) < 8:
                break
            
            # 计算数据长度
            data_len = (self.buffer[4] << 8) | self.buffer[5]
            packet_len = 6 + data_len + 1 + 2  # 头+长+数据+校验+尾
            
            if len(self.buffer) < packet_len:
                break
            
            packet = self.buffer[:packet_len]
            
            # 校验尾部
            if packet[-2:] == b'\x54\x43':
                # 校验和检查
                checksum = sum(packet[:-3]) & 0xFF
                if checksum == packet[-3]:
                    packets.append(self.process_packet(packet))
                else:
                    packets.append({"error": "Checksum Error", "raw": packet.hex()})
            
            del self.buffer[:packet_len]
            
        return packets

    def process_packet(self, packet):
        control = packet[2]
        ident = packet[3]
        data = packet[6:-3]
        
        res = {"type": "unknown", "control": f"0x{control:02x}", "ident": f"0x{ident:02x}", "data": data.hex()}
        
        # 根据协议文档逻辑映射
        if control == 0x80:
            res["type"] = "Presence/Movement"
            if ident == 0x01: res["val"] = "有人" if data[0] == 0x01 else "无人"
            elif ident == 0x02: 
                states = ["无人", "静止有人", "活跃有人"]
                res["val"] = states[data[0]] if data[0] < 3 else f"未知({data[0]})"
            elif ident == 0x03: res["val"] = f"体动参数: {data[0]}"
            elif ident == 0x04: res["val"] = f"距离: {(data[0]<<8 | data[1])} cm"
            elif ident == 0x05: res["val"] = "方位数据"
            
        elif control == 0x81:
            res["type"] = "Breathing"
            if ident == 0x01: 
                states = ["未知", "正常", "呼吸过高", "呼吸过低", "无"]
                res["val"] = f"状态: {states[data[0]] if data[0]<5 else data[0]}"
            elif ident == 0x02: res["val"] = f"{data[0]} 次/分"
            elif ident == 0x05: res["val"] = "呼吸波形数据"
            elif ident == 0x0B: res["val"] = f"低缓呼吸判读: {data[0]}"

        elif control == 0x84:
            res["type"] = "Sleep"
            if ident == 0x01: res["val"] = "入床" if data[0] == 0x01 else "离床"
            elif ident == 0x02:
                states = ["深睡", "浅睡", "清醒", "无"]
                res["val"] = f"睡眠状态: {states[data[0]] if data[0]<4 else data[0]}"
            
        elif control == 0x85:
            res["type"] = "HeartRate"
            if ident == 0x02: res["val"] = f"{data[0]} BPM"
            elif ident == 0x05: res["val"] = "心率波形数据"
            
        elif control == 0x07:
            if ident == 0x07:
                res["type"] = "RadarRange"
                res["val"] = "范围内" if data[0] == 0x01 else "范围外"
                
        return res

# --- 串口读取线程 ---
class SerialThread(QThread):
    data_received = pyqtSignal(list)
    raw_log = pyqtSignal(str)

    def __init__(self, port, baud=115200):
        super().__init__()
        self.port = port
        self.baud = baud
        self.running = True
        self.parser = SYProtocolParser()
        self.ser = None

    def run(self):
        try:
            self.ser = serial.Serial(self.port, self.baud, timeout=0.1)
            while self.running:
                if self.ser.in_waiting > 0:
                    data = self.ser.read(self.ser.in_waiting)
                    self.raw_log.emit(data.hex(' ').upper())
                    packets = self.parser.parse(data)
                    if packets:
                        self.data_received.emit(packets)
                self.msleep(10)
            if self.ser and self.ser.is_open:
                self.ser.close()
        except Exception as e:
            self.raw_log.emit(f"Error: {str(e)}")
        finally:
            self.ser = None

    def send_data(self, data):
        if self.ser and self.ser.is_open:
            try:
                self.ser.write(data)
                return True
            except Exception as e:
                self.raw_log.emit(f"Write Error: {str(e)}")
        return False

# --- UDP 接收线程 (配合手机 App 数据中转) ---
class UdpServerThread(QThread):
    data_received = pyqtSignal(dict)
    raw_log = pyqtSignal(str)
    
    def __init__(self, port=9999):
        super().__init__()
        self.port = port
        self.running = True
        self.sock = None
        
    def run(self):
        try:
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            self.sock.bind(('0.0.0.0', self.port))
            self.sock.settimeout(0.5)
            self.raw_log.emit(f"UDP服务已开启，请将手机 App 的转发地址填为本机的 9999 端口")
            while self.running:
                try:
                    data, addr = self.sock.recvfrom(1024)
                    text = data.decode('utf-8')
                    # 例如手机发来的格式: {"hr": 75, "spo2": 98}
                    parsed = json.loads(text)
                    self.data_received.emit(parsed)
                except socket.timeout:
                    pass
                except Exception as e:
                    self.raw_log.emit(f"UDP 解析异常: {str(e)}")
            self.sock.close()
        except Exception as e:
            self.raw_log.emit(f"UDP 绑定错误 (端口可能被占用): {str(e)}")
            
    def stop(self):
        self.running = False

# --- UI 界面 ---
class RadarTesterApp(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("R60ABD1 矩阵纪元多模融合测试工具 V1.0")
        self.resize(1000, 700)
        self.serial_thread = None
        self.udp_thread = None
        self.is_recording = False
        self.csv_file = None
        self.csv_writer = None
        self.init_ui()

    def init_ui(self):
        # 整体暗色调
        self.setStyleSheet("""
            QMainWindow { background-color: #121212; }
            QLabel { color: #E0E0E0; font-family: 'Segoe UI', sans-serif; }
            QPushButton { 
                background-color: #333; color: white; border-radius: 5px; 
                padding: 8px; border: 1px solid #444;
            }
            QPushButton:hover { background-color: #444; border: 1px solid #00E5FF; }
            QTextEdit { background-color: #1E1E1E; color: #00FF41; border: 1px solid #333; font-family: 'Consolas'; }
            QFrame#Card { 
                background-color: #1E1E1E; border-radius: 10px; 
                border: 1px solid #333; 
            }
        """)

        central_widget = QWidget()
        self.setCentralWidget(central_widget)
        main_layout = QHBoxLayout(central_widget)

        # --- 左侧控制面板 ---
        left_panel = QVBoxLayout()
        
        # 连接设置
        conn_group = QFrame()
        conn_group.setObjectName("Card")
        conn_vbox = QVBoxLayout(conn_group)
        conn_vbox.addWidget(QLabel("<b>串口设置</b>"))
        
        self.port_combo = QComboBox()
        self.refresh_ports()
        conn_vbox.addWidget(self.port_combo)
        
        self.btn_connect = QPushButton("连接模块")
        self.btn_connect.clicked.connect(self.toggle_connection)
        conn_vbox.addWidget(self.btn_connect)
        
        left_panel.addWidget(conn_group)

        # 指令发送组
        cmd_group = QFrame()
        cmd_group.setObjectName("Card")
        cmd_vbox = QVBoxLayout(cmd_group)
        cmd_vbox.addWidget(QLabel("<b>快速控制</b>"))
        
        cmds = [
            ("开启存在检测", "53 59 80 00 00 01 01 2E 54 43"),
            ("关闭存在检测", "53 59 80 00 00 01 00 2D 54 43"),
            ("开启心率检测", "53 59 85 00 00 01 01 33 54 43"),
            ("关闭心率检测", "53 59 85 00 00 01 00 32 54 43"),
            ("开启呼吸检测", "53 59 81 00 00 01 01 2F 54 43"),
        ]
        
        for name, hex_str in cmds:
            btn = QPushButton(name)
            btn.clicked.connect(lambda ch, h=hex_str: self.send_hex(h))
            cmd_vbox.addWidget(btn)
        
        left_panel.addWidget(cmd_group)
        
        # --- 数据录制组 ---
        record_group = QFrame()
        record_group.setObjectName("Card")
        record_vbox = QVBoxLayout(record_group)
        record_vbox.addWidget(QLabel("<b>数据收集 (Excel/CSV兼容)</b>"))
        
        self.btn_start_record = QPushButton("● 开始收集并保存")
        self.btn_start_record.clicked.connect(self.start_recording)
        self.btn_stop_record = QPushButton("■ 停止收集")
        self.btn_stop_record.clicked.connect(self.stop_recording)
        self.btn_stop_record.setEnabled(False)
        
        record_vbox.addWidget(self.btn_start_record)
        record_vbox.addWidget(self.btn_stop_record)
        left_panel.addWidget(record_group)

        # --- 手表 SDK 接入组 (UDP中转) ---
        watch_group = QFrame()
        watch_group.setObjectName("Card")
        watch_vbox = QVBoxLayout(watch_group)
        watch_vbox.addWidget(QLabel("<b>手表 SDK 接入 (UDP 中继)</b>"))
        
        # 获取本机IP以给用户提示
        try:
            s_temp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s_temp.connect(("8.8.8.8", 80))
            local_ip = s_temp.getsockname()[0]
            s_temp.close()
        except:
            local_ip = "127.0.0.1"

        self.lbl_udp_tips = QLabel(f"本机 IP: <font color='#00E5FF'>{local_ip}</font> | 端口: <b>9999</b>")
        self.lbl_udp_tips.setTextFormat(Qt.TextFormat.RichText)
        watch_vbox.addWidget(self.lbl_udp_tips)
        
        self.btn_udp = QPushButton("开启监听")
        self.btn_udp.clicked.connect(self.toggle_udp)
        watch_vbox.addWidget(self.btn_udp)
        left_panel.addWidget(watch_group)

        left_panel.addStretch()
        main_layout.addLayout(left_panel, 1)

        # --- 右侧数据展示 ---
        right_panel = QVBoxLayout()
        
        # 实时指标卡片
        display_layout = QGridLayout()
        
        self.card_presence = self.create_data_card("有人状态", "等待数据...", "#FF5252")
        self.card_hr = self.create_data_card("心率 (BPM)", "--", "#00E5FF")
        self.card_br = self.create_data_card("呼吸 (次/分)", "--", "#00E676")
        self.card_dist = self.create_data_card("距离 (cm)", "--", "#FFD740")
        self.card_watch_hr = self.create_data_card("手环心率 (BPM)", "--", "#FF4081")
        self.card_watch_spo2 = self.create_data_card("手环血氧 (%)", "--", "#7C4DFF")
        
        display_layout.addWidget(self.card_presence, 0, 0)
        display_layout.addWidget(self.card_hr, 0, 1)
        display_layout.addWidget(self.card_br, 1, 0)
        display_layout.addWidget(self.card_dist, 1, 1)
        display_layout.addWidget(self.card_watch_hr, 2, 0)
        display_layout.addWidget(self.card_watch_spo2, 2, 1)
        
        right_panel.addLayout(display_layout)

        # 日志终端
        right_panel.addWidget(QLabel("<b>实时数据日志 (Hex / Parsed)</b>"))
        self.log_output = QTextEdit()
        self.log_output.setReadOnly(True)
        right_panel.addWidget(self.log_output)
        
        main_layout.addLayout(right_panel, 3)

    def create_data_card(self, title, val, color):
        frame = QFrame()
        frame.setObjectName("Card")
        vbox = QVBoxLayout(frame)
        
        lbl_title = QLabel(title)
        lbl_title.setFont(QFont("Segoe UI", 10))
        lbl_title.setStyleSheet("color: #AAAAAA;")
        
        lbl_val = QLabel(val)
        lbl_val.setFont(QFont("Segoe UI", 24, QFont.Weight.Bold))
        lbl_val.setStyleSheet(f"color: {color};")
        lbl_val.setAlignment(Qt.AlignmentFlag.AlignCenter)
        
        vbox.addWidget(lbl_title)
        vbox.addWidget(lbl_val)
        
        # 保存引用以便更新
        frame.val_label = lbl_val
        return frame

    def start_recording(self):
        if self.is_recording: return
        try:
            filename = f"radar_data_{datetime.datetime.now().strftime('%Y%m%d_%H%M%S')}.csv"
            # 使用 utf-8-sig 编码，在 Windows 上双击文件可直接用 Excel 完美打开且中文无乱码！
            self.csv_file = open(filename, 'w', newline='', encoding='utf-8-sig')
            self.csv_writer = csv.writer(self.csv_file)
            self.csv_writer.writerow(["时间戳", "协议类别", "控制字", "标识字", "解析输出", "原始Hex数据"])
            self.is_recording = True
            
            self.btn_start_record.setEnabled(False)
            self.btn_stop_record.setEnabled(True)
            self.log_output.append(f"<font color='#00E5FF'>[录制] 正在自动安全保存至当前目录: {filename}</font>")
        except Exception as e:
            self.log_output.append(f"<font color='red'>[录制错误] 无法建立保存文件: {e}</font>")

    def stop_recording(self):
        if not self.is_recording: return
        self.is_recording = False
        if self.csv_file:
            self.csv_file.close()
            self.csv_file = None
            self.csv_writer = None
        
        self.btn_start_record.setEnabled(True)
        self.btn_stop_record.setEnabled(False)
        self.log_output.append("<font color='#00E5FF'>[录制] 已停止记录，文件安全完整写入硬盘！</font>")

    def closeEvent(self, event):
        if self.is_recording:
            self.stop_recording()
        if self.serial_thread and self.serial_thread.isRunning():
            self.serial_thread.running = False
            self.serial_thread.wait()
        if self.udp_thread and self.udp_thread.isRunning():
            self.udp_thread.stop()
            self.udp_thread.wait()
        event.accept()

    def toggle_udp(self):
        if self.udp_thread and self.udp_thread.isRunning():
            self.udp_thread.stop()
            self.udp_thread.wait()
            self.udp_thread = None
            self.btn_udp.setText("开启监听")
            self.log_output.append("<font color='gray'>已关闭 UDP 监听</font>")
        else:
            self.udp_thread = UdpServerThread(9999)
            self.udp_thread.data_received.connect(self.update_watch_data)
            self.udp_thread.raw_log.connect(self.append_log)
            self.udp_thread.start()
            self.btn_udp.setText("关闭监听")

    def update_watch_data(self, data):
        # 兼容手机传来的 JSON 数据并同步更新卡片和存档
        hr = data.get("hr", "")
        spo2 = data.get("spo2", "")
        if hr: self.card_watch_hr.val_label.setText(str(hr))
        if spo2: self.card_watch_spo2.val_label.setText(str(spo2))
        
        self.log_output.append(f"<font color='#FF4081'>[Starmax] 收取数据: hr={hr}, spo2={spo2}</font>")
        
        if self.is_recording and self.csv_writer:
            now_str = datetime.datetime.now().strftime('%H:%M:%S.%f')[:-3]
            self.csv_writer.writerow([now_str, "Starmax UDP", "-", "-", f"心率:{hr} 血氧:{spo2}", json.dumps(data)])
            self.csv_file.flush()

    def refresh_ports(self):
        self.port_combo.clear()
        ports = [p.device for p in serial.tools.list_ports.comports()]
        self.port_combo.addItems(ports)

    def toggle_connection(self):
        if self.serial_thread and self.serial_thread.isRunning():
            self.serial_thread.running = False
            self.serial_thread.wait()
            self.serial_thread = None
            self.btn_connect.setText("连接模块")
            self.log_output.append("<font color='gray'>已断开连接</font>")
        else:
            port = self.port_combo.currentText()
            if not port: return
            self.serial_thread = SerialThread(port)
            self.serial_thread.data_received.connect(self.update_data)
            self.serial_thread.raw_log.connect(self.append_log)
            self.serial_thread.start()
            self.btn_connect.setText("断开连接")
            self.log_output.clear()
            self.log_output.append(f"<font color='#00E5FF'>正在连接 {port}...</font>")

    def append_log(self, text):
        self.log_output.append(f"<font color='#444'>> {text}</font>")
        # 自动滚动
        self.log_output.moveCursor(self.log_output.textCursor().MoveOperation.End)

    def update_data(self, packets):
        for p in packets:
            if "error" in p:
                self.log_output.append(f"<font color='red'>[Error] {p['error']}: {p['raw']}</font>")
                continue
            
            # 更新 UI 卡片
            ctrl = p.get("control")
            ident = p.get("ident")
            val = p.get("val", "")
            
            self.log_output.append(f"<font color='#00E676'>[Parsed] {p['type']} ({ctrl}-{ident}): {val}</font>")
            
            if self.is_recording and self.csv_writer:
                now_str = datetime.datetime.now().strftime('%H:%M:%S.%f')[:-3]
                hex_data = p.get('data', "")
                self.csv_writer.writerow([now_str, p['type'], ctrl, ident, val, hex_data])
                self.csv_file.flush() # 实时存盘，防止串口异常或闪退时丢失数据
                
            if ctrl == "0x80":
                if ident == "0x02":
                    self.card_presence.val_label.setText(val)
                elif ident == "0x04":
                    self.card_dist.val_label.setText(val.replace("距离: ", ""))
            elif ctrl == "0x81":
                if ident == "0x02":
                     self.card_br.val_label.setText(val.replace(" 次/分", ""))
            elif ctrl == "0x85":
                if ident == "0x02":
                    self.card_hr.val_label.setText(val.replace(" BPM", ""))

    def send_hex(self, hex_str):
        if not self.serial_thread or not self.serial_thread.isRunning():
            self.log_output.append("<font color='orange'>警告: 请先连接串口</font>")
            return
        try:
            raw = bytes.fromhex(hex_str)
            if self.serial_thread.send_data(raw):
                self.log_output.append(f"<font color='#FFD740'>[TX] {hex_str}</font>")
            else:
                self.log_output.append("<font color='red'>发送失败: 串口未准备好</font>")
        except Exception as e:
            self.log_output.append(f"<font color='red'>发送异常: {e}</font>")

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = RadarTesterApp()
    window.show()
    sys.exit(app.exec())
