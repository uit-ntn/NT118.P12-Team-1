# Road Watcher App (for Android Studio)

**Road Watcher App** is an Android application that detects road potholes by collecting and analyzing data from the phone’s accelerometer sensor. The app can also capture the GPS location of detected potholes, display them on a map, allow manual reporting, and keep a history of all identified potholes.

---

## Table of Contents
- [Introduction](#introduction)
- [Technologies & Project Structure](#technologies--project-structure)
- [System Requirements](#system-requirements)
- [Installation & Running Guide](#installation--running-guide)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

---

## Introduction
- **Objective**: Provide a tool to collect pothole data on roads to help improve road infrastructure.
- **How it Works**: The application uses the **accelerometer** to detect abnormal vibrations while driving, thereby identifying potholes and saving their GPS locations.
- **Key Features**:
  - Real-time sensor data collection
  - Google Maps integration
  - Alerts when potholes are detected
  - Management and review of detected potholes
  - Manual reporting of road conditions

---

## Technologies & Project Structure
### Technologies Used
- **Programming Language**: Java
- **Android**: Built with Android Studio
- **Sensors**: Accelerometer
- **GPS**: Records pothole locations
- **Database**: MongoDB Atlas (cloud-based)
- **Backend**: Node.js (Express.js)
- **Google Maps API**: Displays pothole locations on a map

### Project Structure (Android folder)
- **/app**  
  - **/java**: Contains Java source code (Activities, Adapters, Services, etc.)  
  - **/res**: Contains resources (layouts, drawables, values)  
- **Gradle files**: Manage dependencies and project settings
- **AndroidManifest.xml**: Declares permissions and activities

---

## System Requirements
1. **Android Studio** (latest version)
2. **JDK 8** or higher
3. **Android SDK**: Minimum API 21
4. **Internet Connection**: Required to use Google Maps API and to send/receive pothole data
5. **Node.js** (version 14 or higher) - *optional* if you want to run the backend locally
6. **MongoDB Atlas** - *optional* for demo or local MongoDB if available

---

## Installation & Running Guide

### 1. Clone or Download the Source Code
- Download the project from the repository (either as a `.zip` file or via `git clone`).

### 2. Open the Project in Android Studio
1. Open **Android Studio**.
2. Select **File** > **Open** (or **Import Project**) and navigate to the folder containing the application’s source code.
3. Wait for Android Studio to sync the project and install necessary dependencies.

### 3. Configure Google Maps API Key
1. Go to [Google Cloud Console](https://console.cloud.google.com/), create or use an existing **API Key** for Google Maps.
2. Locate the `google_maps_api.xml` file in: app/src/main/res/values/google_maps_api.xml


### 4. Permissions

1. By default, the following permissions have been declared in AndroidManifest.xml
     <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
   <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

## Useage
1. Grant Permissions: On first launch, grant location and sensor permissions if prompted.
2. Start Data Collection: Tap the “Start” button to begin capturing accelerometer data.
3. Pothole Detection: The app displays an alert when it detects abnormal road vibrations. GPS location is automatically saved.
4. Manual Reporting: Use the “Report” feature to manually report potholes not automatically detected or other road issues.
5. Map View: Switch to the map interface to see all logged potholes.
6. Pothole History: The “History” tab allows you to review previously detected potholes.

## Contributing
1. Fork the repository, create a Pull Request, or submit an Issue to report bugs or suggest new features.
2. All contributions are welcome to help this project grow and improve.

## License
Released under the MIT License. See the LICENSE file for details.




