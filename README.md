# Artify: Cross-Platform Art Exploration Ecosystem

![JavaScript](https://img.shields.io/badge/Language-JavaScript-yellow)
![TypeScript](https://img.shields.io/badge/Language-TypeScript-blue)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple)
![Java](https://img.shields.io/badge/Language-Java-orange)

![React](https://img.shields.io/badge/Frontend-React-61DAFB)
![Node.js](https://img.shields.io/badge/Backend-Node.js-339933)
![Android](https://img.shields.io/badge/Mobile-Android-3DDC84)
![Status](https://img.shields.io/badge/Status-Completed-success)

A comprehensive, full-stack application ecosystem built to explore the Artsy API, featuring a unified backend serving both a responsive web application and a native Android client. Developed as part of the **CSCI 571: Web Technologies** course at the **University of Southern California**.

> **Note:** Per university policy, the source code for this project is hosted in a private repository. This repository serves as a portfolio to demonstrate the full-stack architecture and features implemented during the MSCS program.

---

## 🚀 Project Overview

Artify is a distributed system designed to bridge the gap between web and mobile platforms. Evolving from a server-side prototype into a production-grade ecosystem, it enables users to search for artists, view detailed biographies and artwork catalogs, and manage a personalized list of favorites that persists across devices.

## 🛠 Modules Implemented

### 1. Unified Backend Service

Developed a robust RESTful API that serves as the single source of truth for both client applications.

- **API Proxying:** Implemented a **Node.js/Express** server to securely communicate with the Artsy API, handling authentication tokens and hiding client secrets.
- **Session Management:** Built persistent session handling using HTTP-only cookies and JWT.
- **Data Persistence:** Integrated **MongoDB Atlas** to store user profiles and synchronized "Favorites" lists.

### 2. Web Client (React)

Built a responsive Single Page Application (SPA) providing a rich user interface.

- **Dynamic Search:** Implemented real-time autocomplete and filtering using **JavaScript/TypeScript** and **React**.
- **Responsive Design:** Utilized Bootstrap grid systems to ensure seamless rendering on desktops and mobile browsers.
- **State Management:** Handled complex application state for user authentication, favorites synchronization, and tabbed navigation.

### 3. Native Mobile Client (Android)

Engineered a fully native mobile experience using modern Android development standards.

- **Modern UI:** Developed using **Kotlin** and **Jetpack Compose** (Material Design 3) for a fluid, reactive user interface.
- **Networking:** Utilized **Retrofit** and **Java** concurrency patterns for efficient, non-blocking API calls.
- **Image Handling:** Integrated Coil for asynchronous image loading and caching.
- **Local Storage:** Implemented `PersistentCookieJar` and Shared Preferences to maintain login sessions shared with the web platform.

---

## 📸 Demos & Features

The application capabilities were verified through deployment on Google Cloud Platform (GCP) and rigorous cross-platform testing.

### **Web Application Demo**

_Features: Autocomplete Search, Artist Biographies, Artworks Carousel, Favorites Management._

[Insert your Web/HW3 Video Link Here]

### **Mobile Application Demo**

_Features: Splash Screen, Native Navigation, Dark Mode Support, "Similar Artists" Recommendations._

[Insert your Mobile/HW4 Video Link Here]

---

## 💻 Technical Stack

- **Languages:** JavaScript, TypeScript, Kotlin, Java
- **Frontend:** React, HTML5, CSS3, Bootstrap
- **Mobile:** Android SDK (API 34), Jetpack Compose, Retrofit
- **Backend:** Node.js, Express.js
- **Database:** MongoDB Atlas
- **Cloud:** Google Cloud Platform (App Engine)

---

## 🔗 References & Credits

- **Data Source:** Powered by the [Artsy API](https://developers.artsy.net/).
- **Instructor:** [Prof. Marco Papa](https://viterbi.usc.edu/directory/faculty/Papa/Marco).
- **Course:** CSCI 571: Web Technologies, University of Southern California
