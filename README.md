# 📄 Digital Document Expiry Reminder

## 📌 Project Overview

**Digital Document Expiry Reminder** is an Android application developed to help users keep track of important documents and their expiry dates.

The application allows users to add and manage document details and helps them remember upcoming expiry dates. The main purpose of the project is to reduce the chances of forgetting important document renewals.

---

## 🎯 Objective

The main objective of this project is to develop a simple and user-friendly Android application that:

* Stores important document information.
* Keeps track of document expiry dates.
* Displays all saved documents in one place.
* Helps users identify documents that are close to expiry.
* Provides a convenient way to manage digital document records.
* Provides reminders for upcoming document expiry.

---

## ✨ Features

### 🔐 User Login

* Login screen for accessing the application.
* Email/Username and Password fields.
* Simple and user-friendly interface.

### 📄 Add Document

Users can add important document information such as:

* Document name
* Document type
* Document number/details
* Issue date
* Expiry date
* Other required information

### 📋 All Documents

The application provides a dedicated screen to view saved documents.

Users can:

* View document details.
* Check expiry dates.
* See the total number of saved documents.
* Manage their stored documents.

### ⏰ Expiry Reminder

The application is designed to help users remember upcoming document expiries by providing expiry-related reminders.

This can help users renew documents before they expire.

### 💾 Local Data Storage

Document information is stored locally in the application so that the saved information can be displayed when the user opens the document list.

---

# 📱 Application Flow

```text
        ┌──────────────┐
        │  Splash      │
        │   Screen     │
        └──────┬───────┘
               ↓
        ┌──────────────┐
        │    Login     │
        │    Screen    │
        └──────┬───────┘
               ↓
        ┌──────────────┐
        │    Home /    │
        │  Dashboard   │
        └──────┬───────┘
               ↓
       ┌───────┴────────┐
       ↓                ↓
┌──────────────┐  ┌──────────────┐
│ Add Document │  │All Documents │
└──────┬───────┘  └──────┬───────┘
       ↓                 ↓
       └────────┬────────┘
                ↓
       ┌─────────────────┐
       │ Expiry Tracking │
       │  & Reminders    │
       └─────────────────┘
```

---

# 🎨 User Interface

The application uses Android XML layouts to create a clean and user-friendly interface.

The project uses:

* **ConstraintLayout** for screen layouts.
* **MaterialCardView** for card-based UI components.
* `TextView`
* `EditText`
* `Button`
* Other standard Android UI components.

The interface is designed to keep document information easy to read and manage.

---

# 🛠️ Technologies Used

| Technology            | Purpose                  |
| --------------------- | ------------------------ |
| **Kotlin**            | Application programming  |
| **Android Studio**    | Development environment  |
| **XML**               | User interface design    |
| **ConstraintLayout**  | Screen layouts           |
| **MaterialCardView**  | Card-based UI            |
| **SharedPreferences** | Local data storage       |
| **JSON**              | Storing document records |
| **Android SDK**       | Application development  |
| **Gradle**            | Project build system     |

---

# 💾 Data Storage

The application uses **SharedPreferences** for local storage.

Document records are stored using JSON data, allowing multiple documents to be saved and retrieved.

Example structure:

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

This allows the application to load previously saved documents when the **All Documents** screen is opened.

---

# 📂 Project Structure

```text
Expiry_Reminder_Project/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── example/
│           │           └── expiry_reminder_project/
│           │               ├── SplashActivity.kt
│           │               ├── LoginActivity.kt
│           │               ├── AddDocumentActivity.kt
│           │               └── DocumentsActivity.kt
│           │
│           ├── res/
│           │   ├── drawable/
│           │   ├── mipmap/
│           │   ├── values/
│           │   └── layout/
│           │
│           └── AndroidManifest.xml
│
├── gradle/
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

---

# 🔄 Main Application Screens

## 1. Splash Screen

The application starts with a splash screen before navigating to the main application flow.

---

## 2. Login Screen

The login screen allows the user to enter:

* Email / Username
* Password

After successful login, the user can access the application.

---

## 3. Add Document Screen

This screen is used to enter and save document information.

The user can provide the required document details and expiry date.

After saving, the document information is stored locally.

---

## 4. All Documents Screen

The **All Documents** screen displays the documents saved by the user.

It also displays the number of saved documents and provides access to the stored document information.

---

# ⏰ Expiry Management

The main purpose of the application is expiry tracking.

The application can use the document's expiry date to determine its status.

For example:

```text
Document
    ↓
Expiry Date
    ↓
Compare with Current Date
    ↓
┌───────────────┐
│ Active        │
│ Expiring Soon │
│ Expired       │
└───────────────┘
```

This makes it easier for users to identify documents that require attention.

---

# 🧪 Testing

The following functionalities can be tested:

| Test Case                 | Expected Result                          |
| ------------------------- | ---------------------------------------- |
| Open application          | Splash screen is displayed               |
| Login with details        | User enters application                  |
| Add document              | Document is saved                        |
| Open All Documents        | Saved document is displayed              |
| Add multiple documents    | All saved documents are displayed        |
| Check document count      | Correct number of documents is shown     |
| Check expiry date         | Document expiry information is displayed |
| Return to previous screen | Back navigation works correctly          |

---

# ▶️ How to Run

### 1. Clone the repository

```bash
git clone https://github.com/jiyapatel02/Expiry_Reminder_Project.git
```

### 2. Open the project

Open the project in **Android Studio**.

### 3. Sync Gradle

Allow Android Studio to complete the Gradle synchronization.

### 4. Connect Android Device / Emulator

Connect a physical Android device or start an Android Emulator.

### 5. Run the application

Click the **Run ▶** button in Android Studio.

---

# 📚 Concepts Used

This project demonstrates several Android development concepts:

* Android Activities
* Activity Navigation
* Kotlin Programming
* XML UI Design
* ConstraintLayout
* MaterialCardView
* EditText and Button
* User Input
* SharedPreferences
* JSON Data Handling
* Date and Expiry Management
* Local Data Storage
* Android Application Structure

---

# 🎓 Learning Outcomes

After completing this project, the following concepts were learned:

* How to create an Android application using Kotlin.
* How to design Android screens using XML.
* How to navigate between multiple Activities.
* How to accept and process user input.
* How to store application data locally.
* How to work with JSON data in Android.
* How to manage document records.
* How to implement expiry-date tracking.
* How to create a practical Android application based on a real-world problem.

---

# 🚀 Future Enhancements

The project can be further enhanced by adding:

* 🔔 Automatic notification reminders.
* 📅 Calendar integration.
* 🔍 Search and filter documents.
* ✏️ Edit existing documents.
* 🗑️ Delete documents.
* 📸 Upload or scan document images.
* ☁️ Cloud backup and synchronization.
* 🔐 Biometric authentication.
* 📊 Expiry statistics and dashboard.
* 📤 Export and import document data.

---

# 👩‍💻 Author

**Jiya Patel**

**B.Tech – Information Technology**
**Mobile Application Development (MAD)**

---

## 🔗 GitHub Repository

[Expiry_Reminder_Project](https://github.com/jiyapatel02/Expiry_Reminder_Project)

---

# ⭐ Conclusion

**Digital Document Expiry Reminder** is an Android application designed to simplify the management of important documents and their expiry dates.

The project demonstrates practical Android development concepts including **Kotlin, XML UI design, Activities, ConstraintLayout, MaterialCardView, SharedPreferences, JSON data handling, and document management**.

The application provides a simple approach to keeping track of important documents and reducing the possibility of missing their expiry dates.
