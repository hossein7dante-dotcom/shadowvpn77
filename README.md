# SubMgr - V2Ray/Shadowsocks Subscription Manager (Source)

This is a minimal Android project containing the source for a **subscription manager** app
that fetches a base64 subscription from `https://shadowmere.xyz/api/b64sub/`, parses `vmess://` and `ss://`
lines and lets the user enable/disable, copy or share them.

IMPORTANT: This project does **not** include any proxy core. It only manages configs and exports/shares them.

## What I included
- Minimal Gradle Kotlin DSL build files (no Gradle wrapper)
- App module with Kotlin + Jetpack Compose UI
- Room database, repository, and ViewModel
- README with build instructions

## How to build an APK
You have three options:

1. **Use Android Studio (recommended)**  
   - Download this repo zip and open it in Android Studio (Arctic Fox or newer).  
   - Let Android Studio install required SDKs.  
   - Build -> Build Bundle(s) / APK(s) -> Build APK(s).

2. **Use GitHub + GitHub Actions to build and produce an APK automatically**  
   - Create a new GitHub repository and push this project.  
   - Add a GitHub Actions workflow that installs Android SDK and runs `./gradlew assembleRelease`.  
   - Note: You will need to add signing config if you want a signed release; debug builds are unsigned and installable on devices with "Allow unknown sources" or via `adb install`.

3. **Use a CI service (Codemagic, Bitrise, etc.)**  
   - Upload the project to one of those services and configure an Android build.

## Limitations
- This ZIP does **not** contain Gradle wrapper files (`gradlew`) to keep size small. If you need, you can generate a wrapper locally with a matching Gradle version or ask me and I can add it.
- I cannot build an APK in this environment (no Android SDK / build tools available here). I can, however, produce this full source archive and help you set up a CI workflow to build it automatically.

## Next steps I can do for you (pick any)
- Add Gradle wrapper so CI can run `./gradlew` without additional setup.
- Create a GitHub Actions workflow file to build an unsigned debug APK and attach it to workflow artifacts.
- Add step-by-step screenshots for building on a cheap cloud VM or GitHub Actions.

