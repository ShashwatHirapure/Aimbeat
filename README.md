# Running the Project in Android Studio

## Prerequisites

- **Android Studio**: Ensure you have the latest stable version installed.
- **Java Development Kit (JDK)**: JDK 11 or higher is recommended.

## Steps to Run the Project

1. **Download the Project**

   - If you haven't already, download the project from GitHub:
     1. Go to the project repository on GitHub.
     2. Click the green `Code` button.
     3. Select `Download ZIP` and extract the ZIP file to a location on your computer.

2. **Open Android Studio**

   - Launch Android Studio from your applications menu.

3. **Import the Project**

   1. In Android Studio, select `File` > `Open`.
   2. Navigate to the location where you extracted the ZIP file.
   3. Select the folder containing the project and click `OK` or `Open`.

4. **Sync Project with Gradle Files**

   - Android Studio will automatically prompt you to sync the project with Gradle files. If not prompted:
     1. Click the `Sync Project with Gradle Files` button (represented by an elephant icon) in the toolbar at the top of the window.
     2. Wait for the synchronization process to complete.

5. **Build the Project**

   1. Click `Build` in the menu bar.
   2. Select `Build Bundle(s) / APK(s)` and then `Build APK(s)` or `Build Bundle(s)` as needed.
   3. Wait for the build process to complete.

6. **Run the Project**

   1. **Connect a Device**: Connect your Android device via USB or start an Android Emulator from within Android Studio.
   2. **Select Device**: In the toolbar, select the target device from the dropdown menu.
   3. **Run the App**: Click the `Run` button (green play icon) in the toolbar.

## Troubleshooting

- **Gradle Sync Issues**: If you encounter issues during synchronization, try `File` > `Invalidate Caches / Restart` and select `Invalidate and Restart`.
- **Build Errors**: Check the `Build` output window for error messages and consult the `Logcat` for runtime issues.

For more assistance, refer to the [Android Studio documentation](https://developer.android.com/studio/intro) or search for specific issues online.

