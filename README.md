# NfcLab — NFC HCE Example (ready-to-build)

This repository contains an NFC Host Card Emulation (HCE) example Android app (com.labtools.nfc) and a GitHub Actions workflow that builds a signed release APK and uploads it to a GitHub Release.

Important: to produce a signed release you must provide keystore secrets to the repository. See COMMENT.md for full keystore creation and secret instructions.

Quick steps to produce a signed release:
1. Add Gradle wrapper to repo (if missing):
   - Locally run: `./gradlew wrapper --gradle-version 8.4` then commit `gradlew`, `gradlew.bat`, and `gradle/wrapper/*`.
2. Create a keystore locally and base64-encode it (see COMMENT.md).
3. Add repository secrets:
   - KEYSTORE_BASE64 (base64-encoded keystore, single-line)
   - KEYSTORE_PASSWORD
   - KEY_ALIAS
   - KEY_PASSWORD
   - (Optional) GOOGLE_PLAY_SERVICE_ACCOUNT_JSON for Play publish
4. Push branch `add-nfclab` (or push to `main`/`master`).
5. Monitor Actions → "Build Signed Release APK and Create Release".
6. Download the signed APK from Releases.

Security:
- Do NOT commit keystore files or passwords. Keep offline backups and rotate if compromised.