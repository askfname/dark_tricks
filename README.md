## What this is
Dark Tricks is an Android Xposed module (written in Java) that applies a set of system and app-level tweaks — e.g., long-press power for torch, volume-key media skip while screen-off, double-tap-to-sleep on QS, hide/alter various status bar indicators, custom carrier text, and other SystemUI and app-specific hooks. It’s intended for advanced Android users running an Xposed-compatible environment (LSPosed / Riru / classic Xposed) who want to customize SystemUI and behavior across platform and specific apps.

### Stack
- **Language(s):** Java (Android)
- **Framework / runtime:** Android (Xposed framework hooks)

app/ layout (important pieces)
```
app/
  build.gradle                       Android module build file
  proguard-rules.pro                 ProGuard / R8 rules
  src/main/
    AndroidManifest.xml              Android manifest for the module/app
    java/com/darkeyes/tricks/
      Main.java                      Xposed hooks and main runtime logic (lots of hooks)
      SettingsActivity.java          UI for module settings
      SettingsFragment.java          Settings fragment
    res/                             Android resources (layouts, values, etc.)
```

How it fits together:
- The module’s runtime entry point is Main.java which implements IXposedHookZygoteInit and IXposedHookLoadPackage. It reads shared preferences (XSharedPreferences) and installs many hooks into platform classes (android, com.android.systemui) and into certain apps (e.g., com.microsoft.office.outlook, com.google.android.dialer).
- Hooks modify behavior at runtime: intercept key events, manage torch, suppress or change notifications/indicators, modify QS/footer/footers, change carrier text, suppress certain strings (call recording text), and alter gestures/gesture handlers. SettingsActivity / SettingsFragment persist preferences which Main listens for via broadcasts.

## How to run it
Prerequisites:
- JDK (Java)
- Android SDK (build tools for the targeted compileSdk)
- Gradle (wrapper included)
- A device or emulator with an Xposed-compatible runtime (LSPosed, Riru, or classic Xposed) to load module behavior (module must be installed/enabled there). Root is typically required for hooking system processes.

Build the APK:
```
# from repo root
./gradlew :app:assembleDebug
# or for release
./gradlew :app:assembleRelease
```

Install on device (example via adb):
```
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Then:
- Enable the module in your Xposed manager (LSPosed/Riru) and allow required hooks.
- Reboot the device for hooks to take effect.
- Open the module's SettingsActivity to configure the available tricks.

Notes:
- This module hooks platform/system processes — use on compatible devices and understand risks of running Xposed modules (bootloops, instability). Test carefully and keep a way to recover (safe mode / adb / recovery).
- The module uses XSharedPreferences; some preference changes are propagated by the module via a broadcast Intent ("com.darkeyes.tricks.PREFERENCES").
