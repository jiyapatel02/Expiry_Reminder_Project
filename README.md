# 📄 Digital Document Expiry Reminder

An Android application designed to help users store, manage, and track important documents and their expiry dates in one place.

## 📌 About the Project

**Digital Document Expiry Reminder** is a Mobile Application Development project developed using **Kotlin and Android Studio**.

The application helps users maintain important document records and monitor their expiry dates. It provides a simple dashboard-based interface where users can add documents, view saved documents, check expiry information, and manage reminder-related settings.

The project focuses on providing a clean, simple, and user-friendly solution for managing documents that require periodic renewal.

---

## 🎯 Objectives

The main objectives of the application are:

* Store important document information digitally.
* Keep track of document issue and expiry dates.
* Display saved documents in one place.
* Help users identify documents approaching expiry.
* Provide expiry-related reminders.
* Provide a simple and organized Android interface.
* Store document information locally on the device.

---

## ✨ Features

### 🔐 Login

* User login screen.
* Email / Username field.
* Password field.
* Simple and clean interface.
* Navigation to the main application after login.

### 🏠 Home Dashboard

The home screen provides access to the main features of the application.

Users can access:

* Add Document
* My Documents / All Documents
* Reminders
* Settings
* Profile

The dashboard also provides document-related information for quick reference.

### ➕ Add Document

Users can add their important document information.

The document form includes information such as:

* Document name
* Document type
* Document number/details
* Issue date
* Expiry date

After saving, the document is stored locally and can be accessed from the document section.

### 📋 All Documents

The application provides a dedicated section for viewing saved documents.

Users can:

* View saved documents.
* Check document details.
* Check expiry dates.
* View the total number of documents.
* Manage stored document information.

### ⏰ Expiry Tracking

The application tracks document expiry dates and helps users identify documents according to their expiry status.

Typical document statuses include:

| Status      | Meaning                             |
| ----------- | ----------------------------------- |
| 🟢 Valid    | Document is currently valid         |
| 🟠 Due Soon | Document expiry is approaching      |
| 🔴 Expired  | Document has passed its expiry date |

### 🔔 Reminders

The application includes reminder functionality for upcoming document expiries.

Users can manage reminder-related preferences through the application settings.

### ⚙️ Settings

The Settings section provides options for managing application preferences, including notification/reminder settings.

### 👤 Profile

The application provides access to the user's profile section from the main application interface.

---

# 🔄 Application Flow

```text
┌─────────────────┐
│  Splash Screen  │
└────────┬────────┘
         ↓
┌─────────────────┐
│   Login Screen  │
└────────┬────────┘
         ↓
┌─────────────────┐
│ Home Dashboard  │
└────────┬────────┘
         │
    ┌────┼────┬──────────┐
    ↓    ↓    ↓          ↓
   Add  Docs  Reminders Settings
    │
    ↓
Save Document
    │
    ↓
Expiry Tracking
```

---

# 🛠️ Technologies Used

| Technology            | Usage                                   |
| --------------------- | --------------------------------------- |
| **Kotlin**            | Android application development         |
| **Android Studio**    | Development environment                 |
| **XML**               | User interface design                   |
| **ConstraintLayout**  | Screen layouts                          |
| **MaterialCardView**  | Card-based UI components                |
| **SharedPreferences** | Local data storage                      |
| **JSON**              | Document data representation            |
| **Android SDK**       | Android application platform            |
| **Gradle**            | Project build and dependency management |

---

# 💾 Data Storage

The application uses **SharedPreferences** for storing document information locally.

Document records are maintained using **JSON**, allowing multiple documents to be stored and retrieved.

The storage flow is:

```text
SharedPreferences
       ↓
ExpiryReminder
       ↓
documents
       ↓
JSON Array
       ↓
Document Records
```

This allows the application to retrieve saved document information when the user opens the document section.

---

# 🎨 User Interface

The application uses a modern teal-based visual theme.

### Color Palette

| Element        | Color     |
| -------------- | --------- |
| Primary Teal   | `#087F73` |
| Primary Dark   | `#05665C` |
| Primary Light  | `#DDF4F0` |
| Background     | `#F7FAF9` |
| Card           | `#FFFFFF` |
| Primary Text   | `#17201F` |
| Secondary Text | `#66716F` |
| Hint Text      | `#9AA5A3` |
| Valid          | `#1B9A59` |
| Due Soon       | `#D88900` |
| Expired        | `#D93636` |

The interface uses **ConstraintLayout** for screen layouts and **MaterialCardView** for card-based components.

---

# 📂 Repository Structure

The repository contains the following top-level project structure:

```text
Expiry_Reminder_Project/
│
├── .idea/
│
├── app/
│   └── Android application source
│
├── gradle/
│   └── Gradle wrapper files
│
├── .gitignore
│
├── README.md
│
├── build.gradle.kts
│
├── gradle.properties
│
├── gradlew
│
├── gradlew.bat
│
└── settings.gradle.kts
```

### 📱 `app/`

Contains the main Android application, including the source code, layouts, resources, manifest, and application configuration.

### ⚙️ `gradle/`

Contains Gradle wrapper-related files used for building the Android project.

### 📄 `build.gradle.kts`

Project-level Gradle build configuration.

### 📄 `gradle.properties`

Gradle project properties.

### 📄 `settings.gradle.kts`

Defines the Gradle project configuration and included modules.

### 📄 `gradlew`

Gradle wrapper script for Linux/macOS environments.

### 📄 `gradlew.bat`

Gradle wrapper script for Windows environments.

### 📄 `.gitignore`

Specifies files and directories that should not be committed to Git.

### 📄 `.idea/`

Android Studio project configuration.

---

# 📱 Main Application Modules

The application follows a simple screen-based flow:

```text
Splash
  ↓
Login
  ↓
Home
  ├── Add Document
  ├── All Documents
  ├── Reminders
  ├── Settings
  └── Profile
```

---

# 🧪 Testing

The major application functionalities can be tested using the following workflow:

| Test                   | Expected Result                        |
| ---------------------- | -------------------------------------- |
| Launch application     | Splash screen appears                  |
| Open login             | Login screen appears                   |
| Enter login details    | User proceeds to application           |
| Open Add Document      | Document form appears                  |
| Enter document details | Details are accepted                   |
| Save document          | Document is stored                     |
| Open All Documents     | Saved document is displayed            |
| Add multiple documents | All saved documents are displayed      |
| Check expiry           | Appropriate expiry status is displayed |
| Open Reminders         | Reminder section opens                 |
| Open Settings          | Settings section opens                 |
| Open Profile           | Profile section opens                  |

---

# 🚀 How to Run the Project

## Prerequisites

Make sure you have:

* Android Studio installed.
* Android SDK configured.
* Kotlin support available.
* An Android Emulator or physical Android device.

## Clone the Repository

```bash
git clone https://github.com/jiyapatel02/Expiry_Reminder_Project.git
```

## Open in Android Studio

1. Open **Android Studio**.
2. Select **Open**.
3. Select the cloned `Expiry_Reminder_Project` folder.
4. Allow Gradle synchronization to complete.
5. Connect an Android device or start an emulator.
6. Click **Run ▶**.

---

# 🔮 Future Enhancements

The application can be further improved with:

* ☁️ Cloud backup and synchronization.
* 📷 Document image upload.
* 📄 PDF document storage.
* 📸 Document scanning using the camera.
* 🔐 Biometric authentication.
* 🔔 Advanced scheduled notifications.
* 🔎 Search and filtering.
* 🗂️ Document categories.
* 📊 Document expiry statistics.
* ☁️ Cloud storage integration.
* 🔄 Backup and restore functionality.

---

# 🎓 Academic Project

**Project Name:** Digital Document Expiry Reminder

**Project Type:** Android Mobile Application

**Domain:** Mobile Application Development

**Programming Language:** Kotlin

**UI Technology:** XML

**Development Environment:** Android Studio

**Local Storage:** SharedPreferences

**Data Format:** JSON

---

# 👩‍💻 Developer

**Jiya Patel**

B.Tech Information Technology

GitHub:
https://github.com/jiyapatel02

Project Repository:
https://github.com/jiyapatel02/Expiry_Reminder_Project

---

# 📜 License

This project was developed as an academic/educational project for demonstrating Android application development and mobile application concepts.

---

## ⭐ Project Summary

**Digital Document Expiry Reminder** provides a centralized way to store important document information and monitor expiry dates. The application combines a clean Android interface, local storage, document management, and expiry tracking to make document management simpler and more organized.
