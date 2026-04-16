// ============================================================
//  E-Flash — build.gradle (Module: app) dependencies to add
// ============================================================
//
// Add these inside the dependencies { } block of your
// app/build.gradle file:
//
// dependencies {
//
//     // Jetpack Compose Navigation
//     implementation("androidx.navigation:navigation-compose:2.7.7")
//
//     // Coil — image loading (for profile photo from gallery)
//     implementation("io.coil-kt:coil-compose:2.6.0")
//
//     // Material Icons Extended (for Lock, Visibility, etc.)
//     implementation("androidx.compose.material:material-icons-extended")
//
// }
//
// ============================================================
//  File structure — copy files to these paths in your project:
// ============================================================
//
//  app/src/main/java/com/example/loginandprofile/
//    ├── MainActivity.kt      ← replaces the existing file
//    ├── LoginScreen.kt       ← new file
//    ├── RegisterScreen.kt    ← new file
//    └── ProfileScreen.kt     ← new file
//
// ============================================================
//  Permissions — add to AndroidManifest.xml (inside <manifest>):
// ============================================================
//
//  <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
//  <!-- For Android 12 and below: -->
//  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
//      android:maxSdkVersion="32" />
//
// ============================================================
//  Theme — make sure your ui/theme/Theme.kt has:
// ============================================================
//
//  @Composable
//  fun LoginAndProfileTheme(content: @Composable () -> Unit) {
//      MaterialTheme(
//          colorScheme = lightColorScheme(),
//          content = content
//      )
//  }
//
// ============================================================

