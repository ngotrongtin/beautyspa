# BeautySpa - Android Kotlin Compose App

A modern, elegant spa booking application built with Jetpack Compose and native Android.

## 🎯 Features

- **Home Screen**: Browse featured services and popular categories
- **Services Screen**: View all services with category filtering using FilterChips
- **Booking Screen**: Complete booking flow with service, date, time, and specialist selection
- **Profile Screen**: Manage appointments and user profile
- **Material Design 3**: Beautiful rose/pink theme with modern UI components
- **Fully Compose**: 100% Jetpack Compose UI - no XML layouts

## 🛠️ Technical Stack

### Configuration (Exact Requirements Met)
- **Android Studio**: Otter
- **Gradle**: 8.11.1
- **Android SDK**: 36 (Android 15)
- **JVM**: 11
- **AGP (Android Gradle Plugin)**: 8.9.1
- **Kotlin**: 2.0.0
- **Compose Compiler**: 1.5.14

### Architecture & Libraries
- **UI Framework**: Jetpack Compose (100%)
- **Architecture**: MVVM (Model-View-ViewModel)
- **State Management**: StateFlow and Compose State
- **Navigation**: Navigation Compose
- **Image Loading**: Coil Compose 2.7.0
- **Material Design**: Material3 Compose
- **Coroutines**: kotlinx-coroutines-android 1.8.0
- **Lifecycle**: AndroidX Lifecycle Compose 2.8.7

### Compose Features Used
- ✅ `LazyColumn` & `LazyRow` for efficient lists
- ✅ `LazyVerticalGrid` for grid layouts
- ✅ `Material3` components (Cards, Buttons, Chips, etc.)
- ✅ `StateFlow` for reactive state management
- ✅ `Navigation Compose` for screen navigation
- ✅ `AsyncImage` from Coil for image loading
- ✅ `BottomNavigation` with Material3
- ✅ Custom theming with Material3 ColorScheme

## 📁 Project Structure

```
android/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/beautyspa/app/
│   │       │   ├── MainActivity.kt (Compose entry point)
│   │       │   ├── data/
│   │       │   │   ├── model/
│   │       │   │   │   ├── Service.kt
│   │       │   │   │   ├── Specialist.kt
│   │       │   │   │   └── Appointment.kt
│   │       │   │   └── repository/
│   │       │   │       └── ServiceRepository.kt
│   │       │   └── ui/
│   │       │       ├── theme/
│   │       │       │   ├── Color.kt
│   │       │       │   ├── Theme.kt
│   │       │       │   └── Type.kt
│   │       │       ├── navigation/
│   │       │       │   └── BeautySpaApp.kt
│   │       │       └── screens/
│   │       │           ├── home/
│   │       │           │   ├── HomeScreen.kt
│   │       │           │   └── HomeViewModel.kt
│   │       │           ├── services/
│   │       │           │   ├── ServicesScreen.kt
│   │       │           │   └── ServicesViewModel.kt
│   │       │           ├── booking/
│   │       │           │   ├── BookingScreen.kt
│   │       │           │   └── BookingViewModel.kt
│   │       │           └── profile/
│   │       │               ├── ProfileScreen.kt
│   │       │               └── ProfileViewModel.kt
│   │       ├── res/ (minimal - only theme colors)
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Otter or later
- JDK 11
- Android SDK 36

### Installation

1. Clone or download the project
2. Open Android Studio
3. Select "Open an Existing Project"
4. Navigate to the `android` directory and open it
5. Wait for Gradle sync to complete
6. Run the app on an emulator or physical device (API 24+)

### Configuration

Make sure your `gradle.properties` has the correct JDK path:
```properties
org.gradle.java.home=/path/to/jdk-11
```

## 🎨 Design System

### Color Scheme (Rose/Pink Theme)
```kotlin
Primary: Rose600 (#E11D48)
OnPrimary: White
PrimaryContainer: Rose100
Secondary: Rose400 (#FB7185)
Background: White
Surface: White
SurfaceVariant: Rose50 (#FFF1F2)
```

### Typography
Material3 default typography with system fonts, fully customizable in `Type.kt`.

### Component Styling
- **Card Corner Radius**: 12dp
- **Button Corner Radius**: 12dp (8dp for small)
- **Chip Corner Radius**: 8dp
- **Image Corner Radius**: CircleShape for avatars

## 📱 Screens

### Home Screen (`HomeScreen.kt`)
- Welcome header with Material3 Typography
- Search bar with OutlinedTextField
- Featured services in horizontal LazyRow
- Categories in LazyVerticalGrid (2 columns)
- Fully scrollable with verticalScroll

### Services Screen (`ServicesScreen.kt`)
- Header with elevation
- Category FilterChips in LazyRow
- All services in LazyColumn
- Each service as a Card with image and details
- Real-time filtering by category

### Booking Screen (`BookingScreen.kt`)
- 4-step booking process in Cards
- Step 1: Service selection (LazyRow)
- Step 2: Date picker dialog
- Step 3: Time slots (LazyVerticalGrid 3x3)
- Step 4: Specialist selection (LazyRow)
- Confirm button with validation
- Border highlights for selected items

### Profile Screen (`ProfileScreen.kt`)
- Gradient header background
- Profile avatar with circular shape
- Edit profile button
- TabRow for Upcoming/Past appointments
- Appointment cards with specialist info
- Menu options (Favorites, Settings)
- Logout button
- Empty state handling

## 🔧 Compose Best Practices Implemented

1. **State Hoisting**: ViewModels manage state, Composables receive and display
2. **Stateless Composables**: Most UI components are stateless and reusable
3. **Side Effects**: Using `LaunchedEffect` for data loading
4. **Remember**: Using `remember` and `mutableStateOf` appropriately
5. **Recomposition**: Optimized with `StateFlow.collectAsState()`
6. **Modifiers**: Proper modifier ordering and composition
7. **Theming**: Consistent Material3 theming throughout
8. **Navigation**: Type-safe navigation with Navigation Compose

## 📦 Dependencies Compatibility

All dependencies are carefully selected to be compatible with:
- ✅ Gradle 8.11.1
- ✅ AGP 8.9.1
- ✅ Kotlin 2.0.0
- ✅ SDK 36
- ✅ JVM 11

No version conflicts or compatibility issues.

## 🎯 Compose Advantages Over XML

1. **Less Boilerplate**: No ViewBinding, no findViewById
2. **Type Safety**: Compile-time checks for UI code
3. **Reusability**: Easy to extract and reuse components
4. **State Management**: Built-in state handling
5. **Preview**: @Preview annotations for quick UI testing
6. **Animations**: Simple and powerful animation APIs
7. **Theming**: Dynamic theming with less code
8. **Interop**: Can still use XML views if needed

## 🐛 Troubleshooting

### Gradle Sync Issues
```bash
# Clear Gradle cache
./gradlew clean

# Rebuild project
./gradlew build --refresh-dependencies
```

### Compose Issues
- Ensure Kotlin version matches Compose compiler version
- Check that all Compose dependencies use the same BOM version
- Invalidate caches: File → Invalidate Caches → Invalidate and Restart

### Runtime Issues
- Minimum SDK is 24 (Android 7.0)
- Check logcat for compose recomposition issues
- Verify image URLs are accessible (Coil requires INTERNET permission)

## 🚀 Performance Tips

1. **LazyColumn**: Use `key` parameter for stable list items
2. **Images**: Coil automatically handles caching
3. **Recomposition**: Use `derivedStateOf` for computed values
4. **Navigation**: Use `launchSingleTop` to avoid duplicate screens
5. **State**: Minimize state hoisting levels

## 📈 Future Enhancements

- [ ] Add Room database for offline support
- [ ] Implement Retrofit for real API integration
- [ ] Add Hilt/Dagger for dependency injection
- [ ] Implement DataStore for preferences
- [ ] Add animations with Compose Animation APIs
- [ ] Implement pull-to-refresh
- [ ] Add skeleton loading states
- [ ] Implement deep linking
- [ ] Add biometric authentication
- [ ] Support dark theme

## 📄 License

This project is for demonstration purposes.

---

**Built with ❤️ using Jetpack Compose**
