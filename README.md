### 本项目为 Dark Tricks Mod 版本，主要修复了 Android 15+ 设备上电源键开启手电筒的功能以及设置界面的汉化，原版地址 https://github.com/darkeyes84/dark_tricks

## 这是什么

Dark Tricks 是一个 Android Xposed 模块（使用 Java 编写），用于实现一系列系统级和应用级的功能调整，例如：长按电源键开启手电筒、息屏状态下使用音量键切换媒体曲目、在快速设置面板（QS）中双击息屏、隐藏/修改各种状态栏指示器、自定义运营商文字，以及针对 SystemUI 和特定应用的其他 Hook 功能。

该模块面向使用 Xposed 兼容环境（LSPosed / Riru / 经典 Xposed）的高级 Android 用户，用于对 SystemUI 以及系统平台和特定应用的行为进行自定义。

本模块设计支持 Android 8.0、8.1、9、10、11、12、12.1、13 和 14。不过，部分功能可能无法在所有 Android 版本上正常工作。该模块已在原生 Android 14 的 Pixel 7 Pro 上进行测试。你也可以在其他 ROM 上尝试使用，但如果某些功能无法正常工作，请不要提交 Bug 报告。

**3.0 及更高版本仅针对 Android 14 设计。所有旧版 Android 必须使用 2.31 版本。**

### 模块功能

* 即使使用浅色壁纸，也强制启用深色主题（仅 Android 8.1）
* 在锁屏界面将语音助手替换为电话功能（仅 Android 8.0 和 8.1）
* 横屏时始终在右侧显示导航栏（仅 Android 8.0、8.1 和 9）
* 隐藏锁屏界面和快速设置中的下一个闹钟，同时隐藏状态栏中的闹钟图标
* 将 LTE 显示为 4G
* 隐藏 LTE+/4G+，始终显示 LTE/4G
* 隐藏 ADB 调试通知
* 隐藏 VPN 通知
* 隐藏“网络可能受到监控”提示
* 开启开发者选项时，隐藏快速设置面板中的系统版本信息（Android 10+）
* 在状态栏或锁屏界面双击息屏（Android 12+）
* 快速下拉打开快速设置面板（Android 12+）
* 使用音量键控制光标
* 使用音量键切换上一首/下一首歌曲
* 电源键切换手电筒（带距离传感器检测）
* 防止意外唤醒屏幕
* 降低通知出现的频率
* 仅显示息屏通知
* 修改运营商名称显示文字
* 绕过 Outlook 设备策略限制
* 快速解锁（Android 12+，但不支持 Android 14）
* 在展开的快速设置面板中显示电池剩余时间估算（Android 12+）
* 强制在锁屏界面使用小号时钟（仅 Android 12 和 12.1）
* 调整返回手势的触摸区域高度（Android 12+）
* 展开通知（Android 12+，但不支持 Android 14）
* 静音电话录音警告提示（仅 Android 14）

模块提供了一个简单的用户界面，可以通过 Xposed 管理器启动，用于选择需要启用的功能。
对于 **2.31 及更早版本**，每次修改设置后都需要重启设备才能生效。
从 **3.0 版本开始**，所有设置均可在运行时直接生效，但“显示 4G”和“隐藏 LTE+/4G+”这两个功能仍然需要重启设备。

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
