import requests
import json
import os
from dotenv import load_dotenv

load_dotenv()

API_KEY = os.getenv("ELEVENLABS_API_KEY")

payload = {
    "name": "Calmify Companion",
    "conversation_config": {
        "agent": {
            "first_message": (
                "Hey, you did so well! I'm really proud of you. "
                "Can you tell me what made you feel overwhelmed?"
            ),
            "language": "en",
            "prompt": {
                "prompt": (
                    "You are Calmify, a warm AI companion for children with autism "
                    "who just finished a calming exercise.\n\n"
                    "Follow this exact flow (3 turns max):\n"
                    "Turn 1 (first_message): Already sent — you asked what happened.\n"
                    "Turn 2: After they answer, validate their feeling in one short sentence, "
                    "then give ONE simple tip they can try next time.\n"
                    "Turn 3: Say something like: You're doing amazing. "
                    "When you're ready, tap 'I'm done talking' and you'll see some options "
                    "on screen if you ever want extra support. I'm always here for you!\n\n"
                    "Then STOP. Do not continue the conversation after turn 3.\n\n"
                    "Rules:\n"
                    "- Max 2 short sentences per turn\n"
                    "- Simple child-friendly language\n"
                    "- Warm and encouraging\n"
                    "- No medical jargon\n"
                    "- If they don't want to talk, say that's completely okay and end"
                ),
                "llm": "gpt-4o-mini",
                "temperature": 0.7,
            },
        }
    },
}

AGENT_ID = "agent_5601kk6df874eq0snhytmfwm3erq"

resp = requests.patch(
    f"https://api.elevenlabs.io/v1/convai/agents/{AGENT_ID}",
    headers={
        "xi-api-key": API_KEY,
        "Content-Type": "application/json",
    },
    json=payload,
)

print("Status:", resp.status_code)
print("Response:", json.dumps(resp.json(), indent=2))
