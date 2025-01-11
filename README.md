# ANS Battery Monitor Project

![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)
![Java](https://img.shields.io/badge/Language-Java-blue?logo=java)
![Gradle](https://img.shields.io/badge/Build-Gradle-blue?logo=gradle)
![Bluetooth](https://img.shields.io/badge/Module-HM10-lightgrey?logo=bluetooth)

![Application Layout](https://github.com/user-attachments/assets/f26d699b-d2f4-46d5-b50e-f4ded9c7d348)

## Overview
The ANS Battery Monitor is an Android application designed to help users monitor battery performance and manage battery-related metrics effectively. The application includes features for historical data tracking, Bluetooth connectivity, and customizable settings for user preferences.

## Features
- **Battery Measurement Tracking**: Monitor and store battery performance metrics.
- **History View**: View historical measurements and trends.
- **Settings**: Customize application behavior and preferences.
- **Bluetooth Integration**: Connect to external devices for data synchronization using the HM10 module (UART communication).

## Project Structure
The project is structured as follows:

- **`app/src/main/java/com/example/ans_batterymonitor_project`**
  - **`MainActivity.java`**: The main entry point of the application.
  - **Fragments**: Separate modules for the user interface, including:
    - `HomeFragment.java`
    - `HistoryFragment.java`
    - `SettingsFragment.java`
    - `MeasurementFragment.java`
  - **Measurement Management**: Classes to handle measurement data, such as:
    - `Measurement.java`
    - `MeasurementHistoryManager.java`
- **`app/src/main/res`**: Resources for layouts, drawables, and strings.
- **`build.gradle.kts`**: Build configuration using Kotlin DSL.

## Prerequisites
To build and run the project, ensure you have the following:

- Android Studio (latest version recommended)
- JDK 11 or higher
- Gradle (configured with the project)
- A physical or virtual Android device for testing

## Getting Started
### Step 1: Clone the Repository
```bash
git clone <repository_url>
cd ans-batterymonitor-project
```

### Step 2: Open in Android Studio
1. Launch Android Studio.
2. Select **Open an Existing Project**.
3. Navigate to the cloned repository and select it.

### Step 3: Build and Run
1. Sync the Gradle files.
2. Select a target device (physical or emulator).
3. Click **Run** to install and start the application.

## License
This project is licensed under the MIT License. See the `LICENSE` file for more details.

---

For more information or issues, please contact the project maintainer.
