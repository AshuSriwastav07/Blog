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
click on the Write Blog Button in the middle, and Enter Blog details like Title, Select the Image or paste the image URL, Enter the Blog body, and Enter some of the tags related to your Blog. At the end click on the Publish Button.

## 2 Read Blog 
Read any blog showing on the Home Page or other user profiles. 

## 3 Edit Blog
Go to your Profile Select the blogs you want to edit, click on the right arrow at the right-top corner, and enter the blog details you want to update. at the end click on the Update Button.

## 4 Edit Profile details
Go to the Profile section click the Profile edit button and then update your Name or Enter the social media link you want to add and save the profile.

## 5 Check other User Profile
Select the blog from the home page where you want to check the user profile, click on the user image in the blog and you will be redirected to the user Profile, you can connect with the user using their social media profile.

![Screenshot_20240830_162703](https://github.com/user-attachments/assets/5e4a22d2-ef30-4e85-92b9-9b463ea6eae1)
![Screenshot_20240830_162836](https://github.com/user-attachments/assets/61e182a1-c77d-4d0a-bb52-285c2355e842)
![Screenshot_20240830_162937](https://github.com/user-attachments/assets/f08dceab-b0b1-412a-9a47-6bd4be02173c)
![Screenshot_20240830_162945](https://github.com/user-attachments/assets/277d7e37-4ccd-4d32-97ce-2c4b288a4f4a)
![Screenshot_20240830_163124](https://github.com/user-attachments/assets/890d0462-d671-46dd-a055-5de8a7e376fe)
![Screenshot_20240830_163013](https://github.com/user-attachments/assets/6a4de963-7fc9-496f-ac59-9a0e71728981)
![Screenshot_20240830_163108](https://github.com/user-attachments/assets/35d94f72-98e6-404f-b0f3-aea03c6b1cbd)


## Connect US: 🔗
* **Play Store** https://play.google.com/store/apps/details?id=com.dusol.thelearnerscommunity
* **Email** ashusriwastav58@gmail.com
* **Instagram** https://www.instagram.com/the_learners_community_dusol/
* **YouTube Channel** https://www.youtube.com/@TheLearnersCommunityDUSOL
