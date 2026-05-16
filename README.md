# Electricity Bill Calculator – Android App

Native Android conversion of the PWA electricity bill calculator.

## Features
- ⚡ Bimonthly billing periods (Indian electricity cycle)
- 🔢 Auto-calculates units consumed and bill amount
- 💾 Saves bill history with auto-suggest of next period
- 📋 Copy bill details to clipboard
- ⚙️ Configurable rate per unit (₹/kWh)
- 🌙 Full dark mode support
- 🗑️ Clear history option

## Build Instructions

### Option A — Android Studio (Recommended)
1. Open Android Studio (Electric Eel or newer)
2. **File → Open** → select this `ElectricityBillCalculator` folder
3. Wait for Gradle sync to complete (it downloads dependencies automatically)
4. Click **Run ▶** to run on emulator or connected device

### Option B — Command Line
Make sure you have:
- JDK 17+
- Android SDK with Build Tools 34
- `ANDROID_HOME` environment variable set

```bash
# On Mac/Linux
./gradlew assembleDebug

# On Windows
gradlew.bat assembleDebug
```

The APK will be at:
`app/build/outputs/apk/debug/app-debug.apk`

> **Note:** If `gradlew` is missing or gives errors, open the project in Android Studio first — it will automatically set up the Gradle wrapper.

## Tech Stack
- Kotlin
- Material Design 3
- ViewBinding
- Gson (JSON persistence)
- SharedPreferences (local storage)
- RecyclerView
- Fragment + BottomNavigationView

## Package
`com.electricitybill.calculator`
