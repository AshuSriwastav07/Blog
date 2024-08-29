# POST

Post is a lightweight, fast, and efficient Android application designed for seamless blogging and content consumption. Whether you're a casual blogger or a content enthusiast, Post offers an intuitive platform where users can create accounts, publish blogs, and read blogs from others with ease. The app is optimized for low-end devices, ensuring a smooth and responsive user experience across all Android devices.#Features

- **User Registration and Authentication:** Users can create an account, log in, and manage their profiles using Firebase Authentication.

- **Create and Publish Blogs:** Write and publish blogs effortlessly. The app supports text-based content with optional background images, ensuring your posts are visually appealing.

- **Read and Explore Blogs:** Browse through blogs published by other users. Engage with content from diverse creators in a well-organized and user-friendly interface.

- **Profile Management:** Manage your user profile with ease. update your username, and view all your published blogs in one place, Edit your Blogs.

- **Like and Comments:** Users can like the blog and write comments on it.

- **Share Their Social Media Handels here:** Blog Write can share their social media account on their profile and others can connect with them.

- **Real-Time Updates:** Stay up-to-date with real-time data synchronization powered by Firebase Firestore. Any changes in blogs or user data are instantly reflected across all devices.

- **Optimized for Low-End Devices:** Post is built with performance in mind, making it an excellent choice for users with low-end devices. The application is optimized to minimize resource usage while maintaining a high level of functionality.

- **Firebase Integration:** Post leverages Firebase services for authentication, cloud storage, and real-time database management, ensuring secure and efficient data handling.

- **Modern UI/UX:** The application features a clean, modern, and intuitive user interface designed to provide a smooth and engaging user experience.
## Technical Overview

- **Architecture:** MVVM architecture is implemented for better separation of concerns, making the codebase more maintainable and scalable.

- **Backend:** Firebase Firestore is used as the primary database, with Firebase Storage handling media uploads.

- **Image Handling:** Picasso is used for efficient image loading and caching, ensuring smooth performance when displaying user profile pictures and blog images.

- **RecyclerView:** Efficiently manages the display of blogs in a scrolling list with the help of RecyclerView, providing a smooth scrolling experience even with large datasets.

- **Kotlin:** The app is built using Kotlin, leveraging modern Android development best practices.
- 
## Prerequisites

- Android Studio installed on your computer.
- A Firebase project set up with Firebase Authentication and Firestore enabled.

## Open the project in Android Studio:
- Open Android Studio.
- Select Open an existing project.
- Navigate to the cloned repository and select it.

## Configure Firebase:
- Add the google-services.json file to the app directory. You can obtain this file from the Firebase console after setting up your Firebase project.

## Add Dependencies:
- Ensure all required dependencies are added to your build.gradle files. These include Firebase Auth, Firestore, and other Android libraries.

## Project-level build.gradle:

// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

## App-level build.gradle:

plugins {

    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

## Project-level build.gradle:


    defaultConfig {
        applicationId = "com.TLC_Developer.Post"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding=true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.play.services.auth)
    implementation (libs.picasso)

}

## Run the project:

- Connect your Android device or start an emulator.
- Click on the Run button in Android Studio to build and run the project.


# Post Use
## 1 Write Blog 
click on Write Blog Button in the middle, and Enter Blog Detials like Title, Select Image or paste image URL, Enter Blog body, Enter some of tags related to your Blog. At end click on Publish Button.

## 2 Read Blog 
Read any blog showing on Home Page.

## 3 Edit Blog
GO to your Profile Select blogs you want to edit, click on right arrow at roght-top corrner and enter blog details you want to update. at end click on update Button.

## 4 Edit Profile details
Go to Profile section click profile edit button and then update your Name or Enter your social media link you want to add and save the profile.

## 5 Check other User Profile
Select blog from home page you want to check user profile, click on user image in blog and you will be redirect to user Profile, you can connect with user using their social media profile.


## Connect US: 🔗
* **Play Store** https://play.google.com/store/apps/details?id=com.dusol.thelearnerscommunity
* **Email** ashusriwastav58@gmail.com
* **Instagram** https://www.instagram.com/the_learners_community_dusol/
* **YouTube Channel** https://www.youtube.com/@TheLearnersCommunityDUSOL
