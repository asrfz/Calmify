from flask import Flask, request, jsonify, send_file, send_from_directory, Response
from datetime import datetime
import os
from elevenlabs_service import precache_audio

app = Flask(__name__)

AUDIO_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "audio_cache")

@app.route("/")
def index():
    return send_file("frontend.html")

@app.route("/audio/<filename>")
def serve_audio(filename):
    path = os.path.join(AUDIO_DIR, filename)
    if not os.path.exists(path):
        return "", 404
    with open(path, "rb") as f:
        data = f.read()
    return Response(data, mimetype="audio/mpeg",
                    headers={"Content-Length": str(len(data)),
                             "Accept-Ranges": "none",
                             "Cache-Control": "no-cache"})

session = {
    "state": "idle",
    "grip_count": 0,
    "grip_needed": 14,
    "triggered_at": None,
    "pulse": None,
    "breathing": None,
}

@app.route("/trigger", methods=["POST"])
def trigger():
    data = request.get_json(force=True)

    if data.get("triggered"):
        session["state"] = "triggered"
        session["grip_count"] = 0
        session["triggered_at"] = datetime.now().isoformat()
        session["pulse"] = data.get("pulse")
        session["breathing"] = data.get("breathing")

        print(f"OVERSTIMULATION DETECTED -- pulse={session['pulse']} breathing={session['breathing']}")

    return jsonify({"ok": True, "state": session["state"]})

@app.route("/arduino-trigger", methods=["POST"])
def arduino_trigger():
    if session["state"] not in ("triggered", "grounding"):
        print("Grip received but no active session -- ignoring")
        return jsonify({"ok": False, "reason": "no active session"}), 400

    session["state"] = "grounding"
    session["grip_count"] += 1

    print(f"Grip {session['grip_count']}/{session['grip_needed']}")

    if session["grip_count"] >= session["grip_needed"]:
        session["state"] = "complete"
        print("GROUNDING COMPLETE")

    return jsonify({
        "ok": True,
        "state": session["state"],
        "grip_count": session["grip_count"],
        "grip_needed": session["grip_needed"],
    })

@app.route("/state", methods=["GET"])
def get_state():
    return jsonify(session)

@app.route("/reset", methods=["POST"])
def reset():
    session["state"] = "idle"
    session["grip_count"] = 0
    session["triggered_at"] = None
    session["pulse"] = None
    session["breathing"] = None
    print("Session reset")
    return jsonify({"ok": True})

if __name__ == "__main__":
    precache_audio()
    app.run(host="0.0.0.0", port=5000, debug=True)
