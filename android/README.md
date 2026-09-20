# FHWS Transport — Android app

Native Android wrapper around the FHWS Transport web application, built with
**Kotlin + Jetpack Compose**. The app is a WebView shell: it loads the live
Laravel backend and gives you the same **Student**, **Staff** and **Admin**
experiences you get in the browser.

This folder is a self-contained Gradle project inside the monorepo. The rest
of the repository is the Laravel web app; nothing here affects it.

## What's in the app

- Full-screen WebView pointing at the FHWS Transport server
  (default: https://fhwstransportv2-1.onrender.com).
- Sessions (cookies), JavaScript and local storage are enabled, so signing in
  on the Student, Staff or Admin portal works exactly as on the web.
- Internal navigation stays in the app; external links open the phone browser.
- Bottom navigation bar: **Home**, **Reload**, **Server**.
- **Server** screen lets you change the server URL (e.g. to a dev deploy),
  reset to the default, and clear cookies/cache.
- Back button walks the in-app history and exits when you're at the root.
- Load progress bar, and a retry screen if the server can't be reached.
- Branding: FHWS purple (#993399), adapted launcher icon.
- minSdk 26 (Android 8.0+), targetSdk 35.

## Building the APK

You need [Android Studio](https://developer.android.com/studio) (Ladybug or
newer) which bundles a JDK, the Android SDK and Gradle.

1. Open the repo root in Android Studio (File → Open, select
   `FHWSTransportV4Andriod`, then open the `android` project — or just open
   the `android` folder directly).
2. Let Gradle sync finish (it downloads AGP 8.7.3, Kotlin 2.0.21 and the
   Compose libraries automatically — the first sync takes a few minutes).
3. Run on a device or emulator: **Run ▶** (green triangle), or
   Build → **Build App Bundle(s) / APK(s) → Build APK(s)**.
   The APK lands in `android/app/build/outputs/apk/debug/`.

From the command line (JDK 17+ required):

```bash
cd android
./gradlew :app:assembleDebug      # -> app/build/outputs/apk/debug/app-debug.apk
./gradlew :app:bundleRelease      # -> signed AAB for Play Store (needs signing config)
```

## Signing in

Use the demo accounts:

| Role    | Email                  | Password  |
|---------|------------------------|-----------|
| Admin   | admin@cput.ac.za       | admin123  |
| Staff   | staff@cput.ac.za       | staff123  |
| Student | student@mycput.ac.za   | student123|

After signing in the matching portal loads automatically (students request
trips, staff track readiness, admins manage everything).

## Changing the server

Open **Server** (bottom bar) → edit the URL → **Save & connect**. The app
reloads against that server. Use **Reset to default** to go back to the live
Render deployment. **Clear cookies & cache** signs you out of the current
server.

## Project layout

```
android/
├── build.gradle.kts            # plugin versions (AGP, Kotlin, Compose)
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   └── wrapper/                # fixed at Gradle 8.9
└── app/
    ├── build.gradle.kts        # app module config
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/fhws/transport/
        │   ├── MainActivity.kt
        │   ├── ServerSettings.kt            # persisted server URL
        │   └── ui/
        │       ├── App.kt                   # scaffold, nav, app shell
        │       ├── SettingsScreen.kt        # server URL + cache controls
        │       ├── WebApp.kt                # WebView composable
        │       └── theme/Theme.kt           # #993399 Material 3 theme
        └── res/                             # strings, themes, launcher icon
```