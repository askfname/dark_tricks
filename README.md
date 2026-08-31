## What this is
Dark Tricks is an Android Xposed module (written in Java) that applies a set of system and app-level tweaks — e.g., long-press power for torch, volume-key media skip while screen-off, double-tap-to-sleep on QS, hide/alter various status bar indicators, custom carrier text, and other SystemUI and app-specific hooks. It’s intended for advanced Android users running an Xposed-compatible environment (LSPosed / Riru / classic Xposed) who want to customize SystemUI and behavior across platform and specific apps.

This module is designed for Android 8.0, 8.1, 9, 10, 11, 12, 12.1, 13 and 14. Some tricks might not work on every version though. It has been tested on stock Android 14 on a Pixel 7 Pro. You can try it on other ROMs, but don't report bugs if something doesn't work.

Version 3.0 and up are designed for Android 14 only. All older Android version must use version 2.31.

### Here is what the module can do:

- Force dark theme even on light wallpaper (Android 8.1 only)
- Replace voice assist with phone on lockscreen (Android 8.0 and 8.1 only)
- Always show the navbar on the right when in landscape (Android 8.0, 8.1 and 9 only)
- Hide next alarm on the lockscreen and in the quick settings and the icon in the status bar
- Show 4G instead of LTE
- Hide LTE+/4G+ (always show LTE/4G)
- Hide ADB notification
- Hide VPN notification
- Hide "Network may be monitored"
- Hide the build version in the QS when developer options are enabled (Android 10+)
- Double tap to sleep on statusbar or lockscreen (Android 12+)
- Quick pulldown for quick settings (Android 12+)
- Volume keys control cursor
- Volume keys skip track
- Power key toggle torch (with proximity check)
- Prevent accidental wake up
- Less frequent notifications
- Screen off notifications only
- Change the carrier text
- Bypass Outlook device policy
- Quick unlock (Android 12+ but not Android 14)
- Show battery duration estimate on expanded QS (Android 12+)
- Force small clock on lockscreen (Android 12 and 12.1 only)
- Adjust height for back gesture (Android 12+)
- Expanded notifications (Android 12+ but not Android 14)
- Silence phone call recording warnings (Android 14 only)

There is a simple user interface that can be launched through Xposed Manager to choose what mods that you want. A reboot is needed every time you change a setting for version 2.31 and older. Starting with version 3.0, all settings can be applied at runtime (except for Show 4G and Hide LTE+/4G+ which still require a reboot).

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
