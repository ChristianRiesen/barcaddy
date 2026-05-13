# Barcaddy

A free, offline barcode and QR code wallet for Android. Keep your loyalty
cards, membership IDs, and coupons in one place and show them at the
register without an internet connection, an account, or ads.

Project homepage: <https://christianriesen.github.io/barcaddy/>

## Features

- Scan any supported 1D or 2D code with the camera, or enter the value
  by hand
- Render the stored code full-screen for the cashier to scan
- Organise cards with names, descriptions, colour palettes, and
  drag-and-drop reordering
- Import and export your collection as CSV for backup or transfer
- Works fully offline — no account, no cloud sync, no analytics

Supported formats: Code 128, Code 39, EAN-13, EAN-8, UPC-A, UPC-E, ITF,
QR, Data Matrix, PDF417, and Aztec.

## Install

The easiest way is to grab the latest APK or AAB from the [Releases
page](https://github.com/ChristianRiesen/barcaddy/releases). For tagged
releases, GitHub Actions builds and uploads a signed bundle; debug APKs
are produced for every commit on `main` and are available as workflow
artifacts.

Minimum Android version: **5.0 Lollipop (API 21)**.

## Privacy

Cards stay on the device. Barcaddy does not contact any server, has no
analytics, and requests no account. The only system permission it asks
for is `CAMERA`, used solely by the in-app scanner. See the full
[privacy policy](https://christianriesen.github.io/barcaddy/privacy.html).

## Build from source

Requirements: JDK 17 and the Android SDK (platform 35, build-tools
35.0.0). Android Studio bundles both.

```sh
./gradlew assembleDebug          # app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug           # install onto a connected device
./gradlew bundleRelease          # signed AAB (needs keystore.properties)
```

In Android Studio, open the repository root and let it sync, then run
the `app` configuration.

Release signing is configured via a `keystore.properties` file at the
repo root with `storeFile`, `storePassword`, `keyAlias`, and
`keyPassword`. The file is git-ignored; without it, `bundleRelease`
still compiles but produces an unsigned bundle.

### Wireless install (Android 11+)

On the phone, enable Developer options → **Wireless debugging**. For the
first connection, pair using a code:

```sh
adb pair PHONE_IP:PAIRING_PORT
# paste the 6-digit code shown on the phone
```

Then connect using the IP and port shown on the Wireless debugging
screen (it changes on each reboot):

```sh
adb connect PHONE_IP:CONNECT_PORT
./gradlew installDebug
```

## Tech stack

- Kotlin 2.0 + Jetpack Compose with Material 3
- Room for card storage, DataStore Preferences for settings
- ZXing + zxing-android-embedded for scanning and rendering
- minSdk 21, targetSdk 35, JDK 17
- All dependencies from Maven Central and Google; no Play Services

## CSV format

Import and export use a single CSV file. The header row is required:

```
name,value,kind,format,description,palette,monogram
```

- `name` — display name (e.g. `Whole Foods`)
- `value` — the raw barcode or QR payload
- `kind` — `BARCODE` or `QR`
- `format` — one of `CODE_128`, `CODE_39`, `EAN_13`, `EAN_8`, `UPC_A`,
  `UPC_E`, `ITF`, `QR_CODE`, `DATA_MATRIX`, `PDF_417`, `AZTEC`
- `description` — optional secondary line
- `palette` — one of `Coral`, `Forest`, `Sunshine`, `Lagoon`, `Plum`,
  `Ink`, `Clay`, `Mint`, `Rose`, `Slate`
- `monogram` — optional 1–2 character tile letters; derived from `name`
  if blank

Fields containing commas, quotes, or newlines are quoted with `"`;
embedded quotes are doubled (`""`). On import, rows whose `value`
duplicates an existing card are skipped.

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
      theme/                      Compose theme + colour palettes
      components/                 shared UI (CardRow, sheets, code image, ...)
      screens/                    Home / Form / Display / Reorder / Scan / Settings
    util/CsvIO.kt                 CSV reader/writer for import/export
docs/                             Jekyll source for the project homepage
```

## Feedback

Questions, bug reports, and feature ideas are welcome via
[GitHub Issues](https://github.com/ChristianRiesen/barcaddy/issues) or
by email to <chris.riesen@gmail.com>.
