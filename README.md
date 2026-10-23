# DevPocket

DevPocket is a native Android notebook for developer knowledge: code snippets, Android notes, architecture patterns, tools, and fixes. It uses Kotlin, Jetpack Compose + Material 3, MVVM, Coroutines/StateFlow, and Appwrite Cloud TablesDB.

## Build

- Android Studio with JDK 17 or newer (the project wrapper uses Gradle 9.3.1).
- Android SDK Platform 37.0 and Build Tools 36.0.0 installed.
- Android app platform registered in Appwrite with application ID `com.ebydev.devpocket`.

From the repository root, run `gradlew.bat assembleDebug` on Windows or `./gradlew assembleDebug` on macOS/Linux. Run unit tests with `gradlew.bat testDebugUnitTest` or `./gradlew testDebugUnitTest`.

## Appwrite Cloud setup

1. Create an Appwrite Cloud project and register an Android platform with package name `com.ebydev.devpocket`.
2. Copy `appwrite.properties.example` to `appwrite.properties` and fill in the endpoint, project ID, database ID, and table ID from your Appwrite project. `appwrite.properties` is ignored by Git. These identifiers are client-side configuration; do not place an Appwrite server API key here.
3. Create a TablesDB database and a table whose ID matches `tableId` in `appwrite.properties` (the Cloud ID you supplied is already set in the ignored local file). Add these columns:

| Key | Type | Required | Suggested size/default |
| --- | --- | --- | --- |
| `ownerId` | varchar | yes | 36 |
| `title` | varchar | yes | 120 |
| `content` | mediumtext | yes | — |
| `category` | varchar | yes | 40 |
| `tags` | text | yes | JSON array stored as text |
| `isFavorite` | boolean | yes | false |
| `createdAt` | datetime | yes | — |
| `updatedAt` | datetime | yes | — |

4. Enable **Row security** on the `notes` table. Grant the `users` role **CREATE only** at table level. Do not grant table-level read, update, or delete; the Android SDK attaches read/update/delete permissions for the row owner when a note is created.
5. Create two test accounts to verify that one account cannot list, read, change, or delete another account’s rows.

The app sends client SDK requests only. It contains no Appwrite server API key. Timestamps in note data are ISO 8601 UTC strings; Appwrite also returns server-managed row timestamps.

## Architecture

`feature/` contains Compose screens, `app/` owns navigation and a small application graph, `domain/` defines note models/validation/repository contracts, and `data/appwrite/` implements authentication and TablesDB access. UI state is exposed from an Android ViewModel as `StateFlow`. Search and category filtering are combined locally over the signed-in user's permission-scoped rows.

## MVP features

- Email/password registration, login, persistent session, and logout.
- Create, edit, read, delete, bookmark, and categorize notes.
- Search note title, content, and tags; filter by Kotlin, Android, Architecture, Debugging, Tools, or Other.
- Empty, loading, retry, and form validation states.
- Private row permissions tied to the signed-in Appwrite user.

## Known limitations and submission checklist

- Search currently loads up to 100 rows before local keyword matching; paging and offline caching are not implemented.
- Semantic search is not included. It requires a secure Appwrite Function and VectorsDB integration and should only be added after end-to-end MVP verification.
- Live CRUD, session persistence, and two-account isolation still need verification against the owner’s Appwrite Cloud project.
- Capture real emulator/device screenshots and record a 2–3 minute walkthrough after configuring Appwrite. Do not use mock data for submission screenshots.
- The official event page confirms submissions close **October 31, 2026 at 11:59 PM Pacific Time**. Submit the public repository and a 2–3 minute demo video through the dashboard; a live demo link is optional. See the [official submission details](https://hackathon.appwrite.io/).
