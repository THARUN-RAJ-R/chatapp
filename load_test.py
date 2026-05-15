import time
from datetime import datetime
import json
import uuid
import threading
import requests
import websocket

# --- Configuration ---
BASE_URL = "https://chatapp-backend-1-shcw.onrender.com"
WS_URL   = "wss://chatapp-backend-1-shcw.onrender.com/ws"

SENDER_1_PHONE = "+910000000001"
SENDER_2_PHONE = "+910000000002"
RECEIVER_PHONE = "+918838074480"

# --- Helper to create STOMP frames ---
def create_stomp_frame(command, headers, body=""):
    lines = [command]
    for k, v in headers.items():
        lines.append(f"{k}:{v}")
    lines.append("")
    lines.append(body)
    return "\n".join(lines) + "\x00"

def format_time():
    now = datetime.now()
    return now.strftime("%d/%m/%Y %Hh:%Mm:%Ss.") + f"{now.microsecond // 1000:03d}ms"

# --- STOMP Client Logic ---
class StompClient:
    def __init__(self, user_id, label):
        self.user_id = user_id
        self.label   = label
        self.ws = websocket.WebSocket()
        self.ws.connect(WS_URL)
        self.connected = False

        connect_frame = create_stomp_frame("CONNECT", {
            "accept-version": "1.1,1.2",
            "heart-beat": "0,0",
            "X-User-Id": self.user_id
        })
        self.ws.send(connect_frame)

        response = self.ws.recv()
        if "CONNECTED" in response:
            self.connected = True
            print(f"[{self.label}] STOMP Connected!")
            self.start_heartbeat()
        else:
            print(f"[{self.label}] Failed to connect: {response}")

    def start_heartbeat(self):
        def heartbeat_loop():
            while self.connected:
                time.sleep(10)
                if self.connected:
                    try:
                        hb_frame = create_stomp_frame("SEND", {
                            "destination": "/app/heartbeat",
                            "content-type": "application/json"
                        }, "{}")
                        self.ws.send(hb_frame)
                    except:
                        pass
        t = threading.Thread(target=heartbeat_loop)
        t.daemon = True
        t.start()

    def send_message(self, chat_id, text):
        payload = {
            "chatId": chat_id,
            "content": text,
            "type": "TEXT",
            "tempId": str(uuid.uuid4())
        }
        send_frame = create_stomp_frame("SEND", {
            "destination": "/app/chat.send",
            "content-type": "application/json"
        }, json.dumps(payload))
        self.ws.send(send_frame)

    def close(self):
        self.connected = False
        self.ws.close()

# --- Per-Sender Task (runs in its own thread) ---
def run_sender(sender_phone, label, chat_id, count, delay, log_file):
    print(f"\nLogging in {label}: {sender_phone}")
    res = requests.post(f"{BASE_URL}/api/auth/login", json={"phone": sender_phone}).json()
    sender_id = res['data']['id']

    client = StompClient(sender_id, label)

    for i in range(1, count + 1):
        content = f"[{label}] Msg {i}"
        formatted_time = format_time()
        log_entry = f"Triggered: '{content}' at {formatted_time}\n"

        with open(log_file, "a", encoding="utf-8") as f:
            f.write(log_entry)

        print(f"[{label}] " + log_entry.strip())
        client.send_message(chat_id, content)
        time.sleep(delay)

    client.close()
    print(f"\n[{label}] ✅ Done! {count} messages sent.")

# --- Main Orchestration ---
def run_test():
    # Reset log file
    open("sender_push_test.txt", "w", encoding="utf-8").close()

    print("Logging in Receiver...")
    res_recv = requests.post(f"{BASE_URL}/api/auth/login", json={"phone": RECEIVER_PHONE}).json()
    receiver_id = res_recv['data']['id']

    print("Logging in Sender 1...")
    res1 = requests.post(f"{BASE_URL}/api/auth/login", json={"phone": SENDER_1_PHONE}).json()
    sender1_id = res1['data']['id']

    print("Logging in Sender 2...")
    res2 = requests.post(f"{BASE_URL}/api/auth/login", json={"phone": SENDER_2_PHONE}).json()
    sender2_id = res2['data']['id']

    print("Creating Direct Chat: Sender1 -> Receiver")
    chat1 = requests.post(
        f"{BASE_URL}/api/chats/direct",
        headers={"X-User-Id": sender1_id},
        json={"targetUserId": receiver_id}
    ).json()['data']['id']

    print("Creating Direct Chat: Sender2 -> Receiver")
    chat2 = requests.post(
        f"{BASE_URL}/api/chats/direct",
        headers={"X-User-Id": sender2_id},
        json={"targetUserId": receiver_id}
    ).json()['data']['id']

    print(f"\nChat 1 ({SENDER_1_PHONE}): {chat1}")
    print(f"Chat 2 ({SENDER_2_PHONE}): {chat2}")
    print("\n--- STARTING DUAL-SENDER 250+250 OFFLINE BURST TEST ---")
    print("Make sure receiver phone is OFFLINE!\n")

    # Run both senders in parallel threads
    t1 = threading.Thread(target=run_sender, args=(
        SENDER_1_PHONE, "SENDER_1", chat1, 1000, 0, "sender_push_test.txt"
    ))
    t2 = threading.Thread(target=run_sender, args=(
        SENDER_2_PHONE, "SENDER_2", chat2, 1000, 0, "sender_push_test.txt"
    ))

    t1.start()
    t2.start()

    t1.join()
    t2.join()

    print("\n🎉 Both senders completed! 'sender_push_test.txt' updated.")

if __name__ == "__main__":
    run_test()
