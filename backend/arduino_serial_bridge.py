import serial
import requests

SERIAL_PORT = "COM4"   # CHANGE THIS
BAUD_RATE = 115200

FLASK_URL = "http://127.0.0.1:5000/arduino-trigger"

def main():
    ser = serial.Serial(SERIAL_PORT, BAUD_RATE, timeout=1)
    print(f"Listening to Arduino on {SERIAL_PORT}...")

    while True:
        try:
            line = ser.readline().decode(errors="ignore").strip()

            if not line:
                continue

            print("Arduino:", line)

            if "SQUEEZE / GRIP DETECTED" in line:
                payload = {
                    "grip_detected": True
                }

                r = requests.post(FLASK_URL, json=payload, timeout=2)
                print("Sent to Flask:", r.status_code, r.text)

        except Exception as e:
            print("Error:", e)

if __name__ == "__main__":
    main()