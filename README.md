# ShoppingListApp

A feature‑rich shopping list app with offline support, cloud sync, and sharing.

## Features
- Email/Password and Google Sign‑In
- Create, edit, delete shopping lists and items
- Categorize items, set priority, mark as purchased with strikethrough
- Sort by category, name, priority, or date
- Drag‑and‑drop reordering
- Share lists via shareable link
- Voice input and auto‑suggestions
- Offline‑first: Room DB works without internet; syncs with Firestore when online
- Background sync via WorkManager
- Localization: English & German
- Accessibility: minimum touch targets, content descriptions
- GDPR data export (JSON)

## Setup
1. Clone the repo.
2. Add your `google-services.json` in `app/src/main/`.
3. Enable Email/Password and Google sign‑in in Firebase Console.
4. Build and run on API 26+.

## Architecture
- MVVM with ViewModel + LiveData
- Room for local persistence
- Firebase Firestore for cloud sync
- WorkManager for background sync

## License
MIT