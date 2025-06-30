# Khowar Folktales StoryApp

Khowar Folktales StoryApp is an Android application dedicated to preserving and sharing traditional folktales from the Khowar-speaking community. The app provides a curated collection of stories, each available in both Urdu and Khowar languages, with accompanying YouTube videos and downloadable content. It is designed for educational, cultural, and entertainment purposes, making Khowar heritage accessible to a wider audience.

## Features

- **Story List:** Browse a collection of Khowar folktales with titles, dates, and view counts.
- **Multilingual Support:** Read stories in both Urdu and Khowar languages, with easy toggling.
- **YouTube Video Playback:** Watch story videos directly within the app using an embedded YouTube player.
- **Download Videos:** Download story videos for offline viewing. Download progress is shown via notifications.
- **Watch Later:** Save stories to a "Watch Later" list for quick access.
- **Search:** Quickly find stories by title.
- **View Count:** Each story tracks and displays the number of views.
- **Modern UI:** Material Design 3, custom toolbar, and bottom navigation for seamless navigation.

## Screens & Navigation

- **Splash Screen:** App logo and transition to the main story list.
- **Main Screen:** List of stories with thumbnails, titles, dates, and view counts. Search bar and navigation menu.
- **Details Screen:** Story details, language toggle, YouTube video, download, watch later, and share options.
- **Watch Later:** List of saved stories for later viewing.
- **Downloads:** List of downloaded videos, with options to play or delete.

## Technical Overview

- **Language:** Java
- **Minimum SDK:** 24 (Android 7.0)
- **Target SDK:** 34
- **Architecture:** Single-module Android app
- **Data Source:** Firebase Realtime Database (stories, view counts, video links)
- **Video Hosting:** YouTube (for streaming), Firebase Storage (for downloads)
- **Libraries Used:**
  - [Firebase Realtime Database](https://firebase.google.com/docs/database)
  - [Firebase Core](https://firebase.google.com/docs/analytics)
  - [Glide](https://github.com/bumptech/glide) (image/video thumbnails)
  - [Android YouTube Player](https://github.com/PierfrancescoSoffritti/android-youtube-player)
  - AndroidX, Material Components

## How It Works

1. **Story Fetching:**
   - On launch, the app fetches stories from Firebase, including metadata, YouTube links, and download links.
   - View counts are incremented and updated in Firebase.
2. **Story Details:**
   - Users can view story details, switch between Urdu and Khowar, and watch the YouTube video.
   - Stories can be added to the Watch Later list or downloaded for offline viewing.
3. **Downloads:**
   - Videos are downloaded to the app's cache directory. Download progress is shown via notifications.
   - Downloaded videos can be played or deleted from the Downloads screen.
4. **Watch Later:**
   - Users can save stories to a Watch Later list, which is accessible from the bottom navigation.

## Build & Run Instructions

1. **Clone the Repository:**
   ```
   git clone https://github.com/mishwani7/Khowar-Folktales-StoryApp.git
   ```
2. **Open in Android Studio:**
   - Open the project folder in Android Studio.
3. **Firebase Setup:**
   - The `google-services.json` file is included. If you fork or re-create the project, set up your own Firebase project and replace this file.
4. **Build the App:**
   - Click **Build > Make Project** or use Gradle: `./gradlew assembleDebug`
5. **Run on Device/Emulator:**
   - Connect your device or start an emulator, then click **Run**.

## Project Structure

- `app/src/main/java/com/khowarfolktales/app/` — Main Java source code (activities, adapters, models, services)
- `app/src/main/res/` — Layouts, drawables, values, and other resources
- `app/build.gradle` — App-level Gradle configuration
- `google-services.json` — Firebase configuration

## Dependencies

See `app/build.gradle` for the full list. Key dependencies:
- Firebase Realtime Database
- Firebase Core
- Glide
- Android YouTube Player
- AndroidX, Material Components

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

## Author

- [mishwani7](https://github.com/mishwani7)

## Acknowledgements

- Khowar community for their stories and cultural heritage
- Open source libraries and contributors

---

For questions, suggestions, or contributions, please open an issue or pull request on the [GitHub repository](https://github.com/mishwani7/Khowar-Folktales-StoryApp).
