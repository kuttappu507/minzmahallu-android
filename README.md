# Minz Mahallu Management System — Android (Native)

Native **Kotlin + Jetpack Compose** port of the [MMS desktop app](https://github.com/kuttappu507/minzmahallu-electron), redesigned **phone-first**: every screen, list and dialog is built for a small Android screen.

Same modules, same SQLite schema/migrations, same PBKDF2-SHA256 auth, same business rules as the desktop app — with a shell that feels like a real Android app.

## What makes this port Android-native

| Design | Details |
|--------|---------|
| **Island menu** | The desktop sidebar is replaced by a floating **menu capsule** (bottom-centre, thumb-reachable). Tapping it springs up a rounded **island popup**: every module as an icon tile with its name, grouped by section, filterable, with the signed-in user + logout + theme on the panel. |
| **Real icons** | Hand-crafted vector icon set (one per module) — no desktop-style letter tiles. |
| **Bottom sheets** | Every dialog (forms, receipts, confirms) rises from the bottom edge with a drag handle, Material-style. |
| **Phone top bar** | 62 dp app bar: logo · page title · search · alerts · language pill. |
| **Back button** | Android back closes the island / search / dialogs — never the app. |
| **Edge-to-edge** | Status-bar + gesture-navigation insets respected; nothing hides under system UI. |
| **Malayalam first-class** | Every label bilingual (EN/മലയാളം) with the latest desktop translations, including നികാഹ് wording and പരിപാലകൻ (custodian). |
| **Touch-first** | Scrollable filter chips and action rows — long Malayalam labels never clip. |

## Modules (parity with desktop, WhatsApp removed)

Core · Dashboard · Families · Members · Staff · Committee · Subscriptions · Donations · Accounting · Assets · Nikah Register · Death Register · Welfare · Certificates · Tokens · Reports · Settings · Users · Audit Log · Backup & Restore

## Data parity with desktop v2.0.5

- **Separate money books** — donation receipts `PREFIX/DN/yy/MM/NNN`, subscription receipts `PREFIX/SB/yy/MM/NNN`; the two series can never share a number.
- **Mark Overdue that works** — flags accounts carrying past-month arrears (the old "period_end < today" rule could never fire); payments clear the flag.
- **Clear All Data** — Settings → Danger Zone wipes all business records (children-before-parents, keeps users/settings/plans/categories) for a clean production start.
- **Production seed** — no demo data, no default password; first run creates the Administrator.

## Architecture

```
UI (custom Compose, phone-first)  →  MmsRepository (window.mms.* facade)
                                  →  AuthService (PBKDF2-SHA256, lockout, setup)
                                  →  DatabaseManager (schema.sql + migrations + seed)
                                  →  SQLite (mms.db)
```

- **SQL** assets copied from Electron `resources/sql/` (schema, seed, V002–V036 migrations)
- **i18n** JSON assets regenerated from the desktop string tables (script: `scripts/export-android-i18n.mjs` in the tooling workspace)
- Min SDK 26 · Target SDK 35 · Package `com.mms.minzmahallu`

## Build

### Local

```bash
# JDK 17 + Android SDK required
./gradlew assembleDebug     # → app/build/outputs/apk/debug/
./gradlew assembleRelease   # → app/build/outputs/apk/release/
```

### CI

Push to GitHub — workflow **Build Android APK** produces debug + release APK artifacts; main-branch pushes publish a GitHub release with both.

## Updates install in-place

Releases are signed with the **committed fixed keystore** (`keystore/`, alias `mms-release`) so every future release installs **over** the previous one — no uninstall needed. The key is intentionally public for this community-sideloaded app; if you fork for Play Store, replace it with your own private keystore.

> One-time note: if a **previous** Minz Mahallu app from another build setup is installed, Android blocks the switch to a different signature — uninstall the old one once, then all future updates install in-place.

## First launch

1. Splash loads the schema into the app-private `mms.db`
2. **Initial Setup** creates the Administrator (no default password)
3. Sign in and use all modules offline

## License

MIT
