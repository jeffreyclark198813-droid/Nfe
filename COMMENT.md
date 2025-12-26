# Signed release flow - required next steps

This branch includes the project and a GitHub Actions workflow that will build a signed release APK and create a GitHub Release.
You MUST add the following repository secrets in GitHub (Settings → Secrets and variables → Actions):

- KEYSTORE_BASE64  : base64-encoded contents of your JKS/PKCS12 keystore (single-line, no newlines)
- KEYSTORE_PASSWORD: keystore store password
- KEY_ALIAS        : alias of the key inside the keystore
- KEY_PASSWORD     : password for the private key alias

How to create and encode a keystore locally:

1) Create keystore (example):
```
keytool -genkeypair -v -keystore release-keystore.jks -storetype JKS -alias my-release-key -keyalg RSA -keysize 2048 -validity 10000 -storepass "YourStorePassword" -keypass "YourKeyPassword" -dname "CN=Your Name, OU=Your Unit, O=Your Org, L=City, ST=State, C=US"
```

2) Base64 encode (macOS/Linux):
```
base64 release-keystore.jks | tr -d '\n' > release-keystore.jks.base64
```

Then paste the content of `release-keystore.jks.base64` into the `KEYSTORE_BASE64` secret.

Notes:
- Never commit keystore or passwords into the repo.
- Keep offline backups of the keystore and passwords.
- After adding secrets, push the branch (recommended `add-nfclab`) and the workflow will build a signed release APK and create a Release containing `NfcLab-app-release.apk`.