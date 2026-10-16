# EasyShop - Android E-Commerce App

EasyShop is a fully-featured, modern Android e-commerce application built using **Jetpack Compose** and **Firebase**.

## Features

- **Authentication**: Secure user login and signup powered by Firebase Authentication.
- **Home Dashboard**: Features dynamic banners, category listings, and user profile welcome greeting.
- **Product Catalog**: Browse products by categories with grid layouts.
- **Product Details**: Detailed view featuring image carousels (`HorizontalPager`), dots indicator, pricing (with discounts/strikethrough), and description.
- **Cart Management**: Add items to cart with real-time Firestore database synchronization.
- **Material 3 Design**: Clean, adaptive, and modern UI adhering to Google's Material 3 guidelines.

## Tech Stack

- **UI**: Jetpack Compose, Material 3
- **Async Image Loading**: Coil 3
- **Backend & Database**: Firebase Firestore
- **Authentication**: Firebase Auth
- **Navigation & State**: Jetpack Navigation Compose, StateFlow / MutableState
- **Language**: Kotlin

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/Ankitkumar747/EasyShop.git
   ```
2. Open the project in **Android Studio**.
3. Add your `google-services.json` file to the `app/` directory.
4. Sync Gradle and run the app on an emulator or physical device.
