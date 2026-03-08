#include <Arduino_LSM9DS1.h>

float lastMag = 0.0;

// tuning values
const float smallShakeThreshold = 0.05;   // lower = more sensitive
const int windowMs = 500;                 // how long to watch
const int neededEvents = 8;               // how many tiny shakes needed

int shakeCount = 0;
unsigned long windowStart = 0;
bool initialized = false;

void setup() {
  Serial.begin(115200);
  while (!Serial);

  if (!IMU.begin()) {
    Serial.println("Failed to initialize IMU!");
    while (1);
  }

  windowStart = millis();
  Serial.println("Micro-vibration squeeze detection ready");
}

void loop() {
  float x, y, z;

  if (IMU.accelerationAvailable()) {
    IMU.readAcceleration(x, y, z);

    float mag = sqrt(x * x + y * y + z * z);

    if (!initialized) {
      lastMag = mag;
      initialized = true;
      return;
    }

    float delta = fabs(mag - lastMag);

    // count tiny repeated shakes
    if (delta > smallShakeThreshold) {
      shakeCount++;
    }

    // every time window, evaluate
    if (millis() - windowStart >= windowMs) {
      Serial.print("shakeCount = ");
      Serial.println(shakeCount);

      if (shakeCount >= neededEvents) {
        Serial.println("SQUEEZE / GRIP DETECTED");
      }

      shakeCount = 0;
      windowStart = millis();
    }

    lastMag = mag;
  }

  delay(20); // ~50 Hz sampling
}