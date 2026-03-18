# Firebase Configuration Guide

## Overview

OnTime uses Firebase Firestore for cloud backup and multi-device synchronization. Here's how to set it up.

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project"
3. Name it: **OnTime**
4. Disable Google Analytics (optional)
5. Click "Create project"

## Step 2: Add Android App

1. In the Firebase project, click "Add app"
2. Select "Android"
3. Fill in the details:
   - **Package name**: `com.ontime`
   - **App nickname**: OnTime (optional)
   - **SHA-1 certificate hash**: (Get from Android Studio or command line)

### Getting SHA-1 Certificate Hash

```bash
# Using Android Studio's built-in debugKeystore
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Or use gradle task
./gradlew signingReport
```

4. Click "Register app"

## Step 3: Download Configuration File

1. Download `google-services.json`
2. Place it in `app/` directory:
   ```
   Ontime/Ontime/app/google-services.json
   ```

## Step 4: Firestore Security Rules

1. In Firebase Console, go to **Firestore Database**
2. Click **Start collection**
3. Create collection: `focus_sessions`
4. Add a document with sample data or skip

### Setup Security Rules

Go to **Rules** tab and update:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow read/write for authenticated users
    match /focus_sessions/{document=**} {
      allow read, write: if request.auth != null;
    }
    
    // Allow users to only modify their own documents
    // (Requires user UID in document)
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

## Step 5: Enable Required Services

In Firebase Console, go to **Project Settings** > **APIs** and ensure these are enabled:
- Cloud Firestore
- Authentication (if using)
- Cloud Storage (if using)

## Step 6: Authentication (Optional)

If you want user authentication:

1. Go to **Authentication** > **Sign-in method**
2. Enable "Email/Password" (or other providers)
3. Create a simple auth flow in the app

## Step 7: Build and Test

```bash
# From Ontime/Ontime directory
./gradlew build

# Run on device
./gradlew installDebug
```

## Troubleshooting

### "google-services.json not found"
- Ensure file is in `app/` directory
- Rebuild the project: `./gradlew clean build`

### Firestore Connection Errors
- Check internet connectivity
- Verify security rules allow access
- Check Firebase credentials in `google-services.json`

### Authentication Issues
- Ensure user is signed in before accessing Firestore
- Check authentication security rules

## Testing Sync

1. Create a focus session in the app
2. When saved, check Firebase Console > Firestore
3. You should see the session document in `focus_sessions` collection

## Best Practices

✅ **DO:**
- Keep `google-services.json` secure (don't commit with real credentials)
- Implement proper authentication
- Use Firestore rules to restrict data access
- Test sync across devices

❌ **DON'T:**
- Commit sensitive data to version control
- Use overly permissive Firestore rules in production
- Store passwords in plain text
- Ignore authentication

## Next Steps

1. Configure email/password authentication
2. Implement user profiles
3. Add offline persistence
4. Setup Firestore backups
5. Monitor Firestore usage and costs

---

For more info, visit [Firebase Documentation](https://firebase.google.com/docs)
