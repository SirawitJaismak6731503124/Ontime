# OnTime - Student Productivity App

A complete Android app that helps students stay focused during study sessions by monitoring their screen usage and blocking distracting apps.

## 🎯 Project Overview

**OnTime** is a fully functional Android app built with modern tech stack (Kotlin, Jetpack Compose, Room Database, Firebase Firestore) implementing smart Focus Sessions.

## 🚀 Quick Start

```bash
cd /workspaces/Ontime/Ontime
./gradlew build
./gradlew installDebug
```

See [Ontime/QUICKSTART.md](./Ontime/QUICKSTART.md) and [Ontime/FIREBASE_SETUP.md](./Ontime/FIREBASE_SETUP.md) for complete setup.

## ✨ Features

- ✅ Create focus sessions with custom time blocks
- ✅ Block distracting apps during sessions
- ✅ Smart notifications with motivational messages
- ✅ 100% offline support with Room Database
- ✅ Cloud sync with Firebase Firestore
- ✅ Beautiful dark theme UI with Jetpack Compose

## 📁 Structure

```
Ontime/
├── app/src/main/java/com/ontime/
│   ├── MainActivity.kt              # Entry point
│   ├── data/                        # Models & Database
│   ├── service/                     # Background monitoring
│   ├── ui/                          # Screens & Theme
│   └── viewmodel/                   # State management
├── build.gradle.kts
└── README.md, FIREBASE_SETUP.md, QUICKSTART.md
```

## 📚 Documentation

- [Ontime/README.md](./Ontime/README.md) - Full documentation
- [Ontime/QUICKSTART.md](./Ontime/QUICKSTART.md) - Setup guide
- [Ontime/FIREBASE_SETUP.md](./Ontime/FIREBASE_SETUP.md) - Firebase config

---

**Stay Focused. Stay Productive. OnTime!** ⏰
