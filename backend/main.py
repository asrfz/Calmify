from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route("/vitals", methods=["POST"])
def vitals():
    data = request.get_json(force=True)
    print("Received vitals:", data)
    return jsonify({"ok": True})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)