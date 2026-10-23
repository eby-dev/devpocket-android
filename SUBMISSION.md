# Hackathon submission prep

- **Event:** Appwrite Databases Hackathon 2026
- **Deadline:** October 31, 2026 at 11:59 PM Pacific Time
- **Project:** DevPocket — a private developer knowledge library built with Appwrite TablesDB
- **Repository:** https://github.com/eby-dev/devpocket-android

The official submission asks for a public repository and a 2–3 minute demo video. A live demo link is optional. Submit through the [hackathon dashboard](https://hackathon.appwrite.io/).

## Short project description

DevPocket gives Android developers one private place to save and retrieve Kotlin snippets, Android notes, architecture patterns, tools, and debugging fixes. It uses Appwrite Account for sign-in and TablesDB for persistent notes, with row-level permissions tied to each signed-in user. The current MVP provides keyword search, category filters, tags, bookmarks, and note CRUD.

## Demo outline (about 2 minutes 30 seconds)

1. **0:00–0:20 — Introduce the problem.** Show the library and describe how useful fixes and snippets get scattered across notes and browser tabs.
2. **0:20–0:45 — Sign in and show the library.** Point out the note count, search, and category filters. Use a real Appwrite account and real rows.
3. **0:45–1:25 — Create a note.** Enter a short debugging or Kotlin note, choose its category, add tags, bookmark it, and save.
4. **1:25–1:50 — Retrieve it.** Search by a tag or phrase, combine that with a category filter, then open the note and show its preserved line breaks.
5. **1:50–2:10 — Edit and delete.** Demonstrate editing and the delete confirmation using a disposable demo note.
6. **2:10–2:30 — Explain the database security.** Show the TablesDB schema and row security settings. If two test accounts have been verified, briefly show that the second account cannot see the first account’s note.
7. **2:30–2:40 — Close.** Name Kotlin, Compose Material 3, Appwrite Account, and Appwrite TablesDB; mention that semantic search is intentionally future work.

Record a real emulator or device session. Do not show passwords, session tokens, API keys, or private account details. Do not stage mock rows in submission screenshots or claim that an unverified flow works.

## Before recording and submitting

- [ ] Configure the Cloud table columns documented in `README.md` and check that the configured table ID matches `appwrite.properties`.
- [ ] Enable row security; grant authenticated users table-level CREATE only; verify row-level read, update, and delete permissions for the creator.
- [ ] Register two test accounts and verify sign-up, sign-in, logout, and restored sessions.
- [ ] Verify create, read, update, delete, restart persistence, and account isolation against the live project.
- [ ] Build the release variant and confirm the app works on a physical Android device or a fully booted emulator.
- [ ] Capture genuine screenshots of the library, editor, and note detail screens using real demo data.
- [ ] Record and review a 2–3 minute demo; confirm no credentials or personal information are visible.
- [ ] Push the finished project to the public repository and confirm it opens without signing in.
- [ ] Submit the public repository and video link in the official dashboard before the deadline.
