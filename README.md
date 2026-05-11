# Barcaddy

Free, offline barcode and QR code wallet for Android. Store loyalty cards,
membership IDs, and coupons; show them at the register without internet,
account, or ads.

## Status

Early scaffold. The UI is implemented end-to-end (home / scan / form /
display / reorder / settings) and codes encode through ZXing for real,
scannable output. The project compiles against Android API 35 and runs on
API 21+ (Android 5.0 Lollipop and newer).

## Stack

- **Kotlin 2.0** + **Jetpack Compose** with Material 3
- **Room** for card storage
- **DataStore Preferences** for settings
- **ZXing** + **zxing-android-embedded** for code scanning and rendering
  (no Google Play Services dependency)
- **minSdk 21**, **targetSdk 35**, **JDK 17** for build

## Building

The repo doesn't ship the Gradle wrapper jar. Generate it once with a
local Gradle install (8.5+):

```sh
gradle wrapper --gradle-version 8.10.2
```

Then:

```sh
./gradlew assembleDebug          # builds app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug           # if a device/emulator is attached
```

In Android Studio: **File â Open** the repo root and let it sync. Run
the `app` configuration on a device or emulator.

### Wireless install to a phone (Android 11+)

On the phone: Developer options → **Wireless debugging** → On.

First-time pairing — tap **Pair device with pairing code** to get a
pairing port and 6-digit code:

```sh
adb pair 192.168.1.6:PAIRING_PORT
# paste the 6-digit code shown on the phone
```

Then connect using the IP:port shown on the main Wireless debugging
screen (different port, changes on reboot):

```sh
adb connect 192.168.1.6:CONNECT_PORT
adb devices                       # should list it as "device"
./gradlew installDebug
```

Or push a prebuilt APK directly:

```sh
adb -s 192.168.1.6:CONNECT_PORT install -r app/build/outputs/apk/debug/app-debug.apk
```

### Dependencies

All dependencies are pulled from Maven Central + Google. No proprietary
SDKs.

## Project layout

```
app/                              Android application module
  src/main/java/com/christianriesen/barcaddy/
    MainActivity.kt               entry point, navigation host
    BarcaddyApp.kt                Application class, DI wiring
    data/                         Room entity, DAO, repository, settings
    nav/                          route constants
    ui/
      MainViewModel.kt            top-level VM exposing cards + settings
      theme/                      Compose theme + color palettes
      components/                 shared UI (CardRow, sheets, code image, ...)
      screens/                    Home / Form / Display / Reorder / Scan / Settings
    util/CsvIO.kt                 CSV reader/writer for import/export
barcaddy/                         Original Claude Design handoff (HTML/JSX)
```

## CSV format

Columns (header is required): `name,value,kind,format,description,palette,monogram`

- `name` â display name (e.g. `Whole Foods`)
- `value` â the raw barcode/QR payload
- `kind` â `BARCODE` or `QR`
- `format` â one of `CODE_128`, `CODE_39`, `EAN_13`, `EAN_8`, `UPC_A`, `UPC_E`,
  `ITF`, `QR_CODE`, `DATA_MATRIX`, `PDF_417`, `AZTEC`
- `description` â optional secondary line
- `palette` â one of `Coral`, `Forest`, `Sunshine`, `Lagoon`, `Plum`, `Ink`, `Clay`, `Mint`, `Rose`, `Slate`
- `monogram` â optional 1â2 char tile letters (auto-derived from `name` if blank)

Fields are quoted with `"` if they contain commas or newlines; embedded
quotes are doubled (`""`).

## Privacy

All cards stay on the device â there is no cloud sync, no account, no
analytics, no network calls. The only system permission requested is
`CAMERA`, used solely for the in-app scanner.
