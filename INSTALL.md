
SubMgr - Ready Project (Source + GitHub Actions)

Contents:
 - Full Android source code (Kotlin + Jetpack Compose) in 'app' module
 - GitHub Actions workflow at .github/workflows/android-apk.yml

How to use:
1. Create a new GitHub repository.
2. Upload (commit) the contents of this ZIP to the repository root.
3. On GitHub, go to Actions -> find 'Android APK Builder' workflow and run it (or push to main).
4. After the workflow completes, open the workflow run and download artifact named 'debug-apk' which contains the debug APK.

Notes:
- The workflow builds a debug APK using system Gradle (no Gradle wrapper required).
- If you prefer to build locally with './gradlew', tell me and I will add Gradle wrapper files.
- If you want a signed release APK, you'll need to add a keystore and update the workflow to use GitHub Secrets for signing.

If you'd like, I can:
 - add Gradle wrapper files so './gradlew assembleDebug' works locally,
 - modify the workflow to produce a signed release (you provide keystore via GitHub Secrets),
 - or build and deliver the APK for you via a CI I set up (requires permissions).

