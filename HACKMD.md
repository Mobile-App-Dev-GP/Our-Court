---
title: Milestone 1 - APP_NAME_HERE (Unit 7)

---

# Milestone 1 - Our Court (Unit 7)

## Table of Contents

1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)

---

## Overview

### Description

Our Court helps tennis enthusiasts locate tennis courts near them. It allows users to communicate with each other to build an all-levels community. Users can share their availability to play at the courts of their choosing. Users are able to filter courts based on distance, star rating, price, and facility access. It allows users to see a broad view of how many people are going to a particular court day-of, and click for more detailed information and player sign-ups.

### App Evaluation

- **Category:** Fitness / Lifestyle / Community / Sports
- **Mobile:** Uses GPS location tracking for nearby courts, real-time map visualization, tap-to-navigate features, and push notifications for player coordination on the go.
- **Story:** Solves the common hassle tennis players face when looking for open local courts or available hitting partners. Creates a centralized local hub to connect players and gauge court crowd levels before leaving home.
- **Market:** Tennis enthusiasts of all skill levels (beginners to advanced league players), local park visitors, and tennis clubs looking to organize casual or competitive play.
- **Habit:** Checked regularly to view real-time court activity, log play availability, organize pickup matches, and find new local courts.
- **Scope:** V1 focuses on court discovery via list view with search/filters, court details, player skill profile, daily court attendance sign-up sheets, personal court lists, and a pure read-only guest mode. V2 expands to map overlays, direct messaging, and dynamic matchmaking.

---

## Product Spec

### 1. User Features (Required and Optional)

**Required Features**

1. **User Authentication & Guest Mode:**
   - Pure read-only guest browsing for courts, filters, and public posts.
   - Auth wall prompt triggered when attempting to check in, save courts, leave reviews, or post.
   - Email & Password registration/login with Firebase Auth.
   - 2-Step onboarding: Display name, UTR/NTRP skill rating dropdown (1.0–16.0 or N/A), GPS location or Zip Code, and optional surface preference.
2. **Tennis Court Locations (List and Search):** Real-time text search by court name or city.
3. **RecyclerView of Tennis Courts:** Displaying court cards with distance, surface type, and open status.
4. **Filters:** Distance radius (miles/km), star rating, access type (Free, Paid, Membership), open/closed status.
5. **Court Rating System:** Aggregate star rating and review list with submission dialog.
6. **Court Detail View & Official Site Link:** Surface specs, lighting status, hours, and external website intent.
7. **Community Sign-Up Sheet:** Day-of player turnout counter and check-in button per court.
8. **General Community Page:** Discussion board for local tennis announcements and partner requests with a Floating Action Button (FAB) to compose posts.
9. **Personal Court Lists (Profile):**
   - **Saved Courts:** Bookmarked courts saved for later reference.
   - **Hit List:** Unique, non-repetitive list of courts played; filterable by date first played or alphabetically; public/private toggle.
   - **Court History:** Private chronological log of all past court sessions (allows repeated visits).
10. **Settings Screen:** Accessed via gear icon on profile; controls distance units (mi/km), default Hit List privacy, notification preferences, log out, and account deletion.

**Optional / Stretch Features**

1. Google Sign-In OAuth integration.
2. Interactive Map view / toggle overlay with court pins.
3. Community group chats per court.
4. Future-date attendance scheduling.
5. Weather overlay with rain alerts for outdoor courts.
6. Automated hitting partner matchmaker.

---

### 2. Screen Navigation & Flow

**1. Main Court List Screen (Home)**
- **Displays:** RecyclerView list of nearby courts, search bar, filter button, and optional map view toggle.
- **Flow:**
  - Tap court card $\rightarrow$ Court Detail Screen.
  - Tap filter icon $\rightarrow$ Filter Options Modal.
  - Tap search bar $\rightarrow$ Live keyword search on court list.

**2. Court Detail Screen**
- **Displays:** Court images, surface/lighting specs, operating hours, star rating & reviews, "Save Court" bookmark icon, "Leave a Review" button, external link, and Daily Sign-Up list.
- **Flow:**
  - Tap "Check In to Play" $\rightarrow$ If guest, show Auth Wall modal; if authenticated, add to daily sheet and Court History/Hit List.
  - Tap "Save Court" $\rightarrow$ If guest, show Auth Wall modal; if authenticated, toggle saved state.
  - Tap "Leave a Review" $\rightarrow$ If guest, show Auth Wall modal; if authenticated, open Review dialog.
  - Tap "Official Website" $\rightarrow$ Launch external browser via Intent.

**3. General Community Page**
- **Displays:** Notice board of community posts and partner requests, plus a "Create Post" FAB.
- **Flow:**
  - Tap post card $\rightarrow$ Expanded thread & comments screen.
  - Tap "Create Post" FAB $\rightarrow$ If guest, show Auth Wall modal; if authenticated, open Create Post compose screen.
  - Tap user avatar/name $\rightarrow$ Target User's Profile Screen.

**4. User Profile Screen**
- **Displays:**
  - If Guest: Prompt card to sign up / log in to unlock personal tracking and posting.
  - If Authenticated: User avatar, display name, skill rating badge (e.g., UTR 6.5 or N/A), Gear icon (Settings), and navigation rows for **Saved Courts**, **Hit List**, and **Court History**.
- **Flow:**
  - Tap Gear icon $\rightarrow$ Settings Screen.
  - Tap "Edit Profile" $\rightarrow$ Edit profile form (bio, rating, home zip).
  - Tap "Saved Courts" $\rightarrow$ RecyclerView of bookmarked courts.
  - Tap "Hit List" $\rightarrow$ Unique visited courts list (with date/alpha sorting and privacy indicator).
  - Tap "Court History" $\rightarrow$ Private chronological visit log.

**5. Settings Screen**
- **Displays:** Preference toggles (Distance unit: mi/km, Default Hit List visibility: public/private), notification toggles (check-in reminders, post replies), Account Actions (Edit Credentials, Log Out, Delete Account).
- **Flow:**
  - Tap "Log Out" $\rightarrow$ Signs out of Firebase, resets session to Guest mode, redirects to Home Tab.
  - Tap "Delete Account" $\rightarrow$ Shows confirmation dialog $\rightarrow$ Deletes data and redirects to Home Tab.
  - Tap Back arrow $\rightarrow$ Returns to User Profile Screen.

**6. Auth Flow (Sign In / Sign Up / Onboarding Modal)**
- **Displays:**
  - Auth Wall Bottom Sheet: "Sign In", "Create Account", "Continue as Guest".
  - Sign In Screen: Email, Password, Forgot Password.
  - Sign Up Step 1: Display Name, Email, Password.
  - Sign Up Step 2: Skill rating dropdown, Home location (GPS or Zip), preferred surface chips.
- **Flow:** Completing authentication dismisses the modal and completes the pending action (or loads user data into the Profile Tab).

---

### 3. Navigation

**Tab Navigation** (Bottom Navigation Bar)

- **Home Tab** $\rightarrow$ Main Court List Screen
- **Community Tab** $\rightarrow$ General Community Page
- **Profile Tab** $\rightarrow$ User Profile Screen (loads Guest landing view if unauthenticated; user dashboard if logged in)

**Flow Navigation** (Screen to Screen & Modals)

- **Main Court List Screen (Home)**
  - Tap a court card $\rightarrow$ Court Detail Screen
  - Tap search bar $\rightarrow$ Real-time text search filter on court RecyclerView
  - Tap filter icon $\rightarrow$ Filter Options Modal
  - Tap map icon (optional) $\rightarrow$ Interactive Map Overlay

- **Filter Options Modal**
  - Select distance radius $\rightarrow$ Filter courts by mileage (or km)
  - Select rating $\rightarrow$ Sort courts by minimum star rating
  - Select price $\rightarrow$ Filter free, paid, or membership courts
  - Select status $\rightarrow$ Filter open or closed courts
  - Tap "Apply" $\rightarrow$ Dismiss modal and return to filtered Main Court List Screen
  - Tap "Reset" $\rightarrow$ Clear active filters to defaults

- **Court Detail Screen**
  - Tap "Check In to Play" (Guest) $\rightarrow$ Auth Wall Modal
  - Tap "Check In to Play" (Authenticated) $\rightarrow$ Add user to Daily Sign-Up Sheet and append entry to Court History (and Hit List if first visit)
  - Tap "Save Court" icon (Guest) $\rightarrow$ Auth Wall Modal
  - Tap "Save Court" icon (Authenticated) $\rightarrow$ Toggle saved state in Saved Courts list
  - Tap "Leave a Review / Star Rating" (Guest) $\rightarrow$ Auth Wall Modal
  - Tap "Leave a Review / Star Rating" (Authenticated) $\rightarrow$ Open Review Submission Modal
  - Tap "Official Website" $\rightarrow$ Launch external browser via Intent
  - Tap Back arrow $\rightarrow$ Return to Main Court List Screen

- **Review Submission Modal**
  - Select star rating (1–5 stars) $\rightarrow$ Set rating value
  - Enter review text $\rightarrow$ Type written feedback
  - Tap "Submit Review" $\rightarrow$ Post review, update aggregate court rating, and dismiss modal
  - Tap "Cancel" / outside $\rightarrow$ Dismiss modal without saving

- **General Community Page**
  - Tap "Create Post" FAB (Guest) $\rightarrow$ Auth Wall Modal
  - Tap "Create Post" FAB (Authenticated) $\rightarrow$ Open Create Post Screen
  - Tap a post card $\rightarrow$ Open Post Detail & Comments View
  - Tap an author's profile photo/name $\rightarrow$ Open User Profile Screen (Public View)

- **Create Post Screen**
  - Select post category tag $\rightarrow$ Tag post as "General Announcement" or "Looking for Hitting Partner"
  - Enter message text $\rightarrow$ Compose post body
  - Tap "Publish Post" $\rightarrow$ Write post to community feed and return to General Community Page
  - Tap Cancel / Back arrow $\rightarrow$ Discard post draft and return to General Community Page

- **Post Detail & Comments View**
  - Enter comment text (Guest) $\rightarrow$ Tapping input field triggers Auth Wall Modal
  - Enter comment text & tap Send (Authenticated) $\rightarrow$ Post comment to thread
  - Tap author avatar $\rightarrow$ Open User Profile Screen (Public View)
  - Tap Back arrow $\rightarrow$ Return to General Community Page

- **User Profile Screen**
  - Tap "Sign In / Register" button (Guest state) $\rightarrow$ Open Sign In Screen
  - Tap Gear icon (Authenticated state) $\rightarrow$ Open Settings Screen
  - Tap "Edit Profile" $\rightarrow$ Open Edit Profile Form
  - Tap "Saved Courts" $\rightarrow$ Open Saved Courts List Screen
  - Tap "Hit List" $\rightarrow$ Open Hit List Screen (with date/alpha sorting and privacy status indicator)
  - Tap "Court History" $\rightarrow$ Open Court History Log Screen (chronological visit log)

- **Edit Profile Form**
  - Update display name / bio $\rightarrow$ Enter new text
  - Update skill rating $\rightarrow$ Select new rating from UTR/NTRP dropdown (1.0–16.0 or N/A)
  - Update home location $\rightarrow$ Enter new Zip Code or detect via GPS
  - Update preferred surface $\rightarrow$ Select Hard, Clay, or Grass chips
  - Tap "Save Changes" $\rightarrow$ Update profile in Firestore and return to User Profile Screen
  - Tap Cancel / Back arrow $\rightarrow$ Discard changes and return to User Profile Screen

- **Saved Courts List Screen**
  - Tap a saved court card $\rightarrow$ Navigate to Court Detail Screen
  - Tap "Remove" icon $\rightarrow$ Delete court from Saved Courts list
  - Tap Back arrow $\rightarrow$ Return to User Profile Screen

- **Hit List Screen**
  - Tap sort toggle $\rightarrow$ Sort courts alphabetically or by date first played
  - Tap visibility toggle $\rightarrow$ Switch Hit List between Public and Private
  - Tap a court card $\rightarrow$ Navigate to Court Detail Screen
  - Tap Back arrow $\rightarrow$ Return to User Profile Screen

- **Court History Log Screen**
  - Scroll chronological visit list $\rightarrow$ View all past check-in sessions and timestamps
  - Tap a court entry $\rightarrow$ Navigate to Court Detail Screen
  - Tap Back arrow $\rightarrow$ Return to User Profile Screen

- **Settings Screen**
  - Toggle Distance Units $\rightarrow$ Switch between Miles (mi) and Kilometers (km)
  - Toggle Default Hit List Visibility $\rightarrow$ Set default for new entries to Public or Private
  - Toggle Notifications $\rightarrow$ Enable or disable check-in reminders and community reply alerts
  - Tap "Edit Email / Password" $\rightarrow$ Open Account Security Re-authentication Dialog
  - Tap "Log Out" $\rightarrow$ Show confirmation dialog $\rightarrow$ Sign out of Firebase, clear local user session, reset app to Guest state, and redirect to Home Tab
  - Tap "Delete Account" $\rightarrow$ Show destructive confirmation dialog $\rightarrow$ Delete Firestore user record and Firebase Auth account, reset to Guest state, and redirect to Home Tab
  - Tap Back arrow $\rightarrow$ Return to User Profile Screen

- **Auth Flow & Modals**
  - **Auth Wall Modal (Bottom Sheet):**
    - Tap "Sign In" $\rightarrow$ Navigate to Sign In Screen
    - Tap "Create Account" $\rightarrow$ Navigate to Sign Up Step 1 Screen
    - Tap "Continue as Guest" / outside $\rightarrow$ Dismiss modal and remain on current screen
  - **Sign In Screen:**
    - Enter email and password $\rightarrow$ Tap "Sign In" $\rightarrow$ Authenticate user, dismiss auth flow, and return to pending action
    - Tap "Forgot Password?" $\rightarrow$ Trigger password reset email dialog
    - Tap "Don't have an account? Sign Up" $\rightarrow$ Navigate to Sign Up Step 1 Screen
    - Tap Close "X" $\rightarrow$ Dismiss modal and return to previous screen
  - **Sign Up Step 1 Screen (Credentials):**
    - Enter display name, email, and password $\rightarrow$ Tap "Next" $\rightarrow$ Navigate to Sign Up Step 2 Screen
    - Tap "Already have an account? Sign In" $\rightarrow$ Navigate to Sign In Screen
    - Tap Back arrow $\rightarrow$ Return to Auth Wall Modal
  - **Sign Up Step 2 Screen (Tennis Onboarding):**
    - Select skill rating $\rightarrow$ Choose self-assessed UTR/NTRP rating from dropdown (1.0–16.0 or N/A)
    - Set home location $\rightarrow$ Tap "Use GPS Location" or input manual 5-digit Zip Code
    - Select preferred surface (Optional) $\rightarrow$ Tap Hard, Clay, or Grass chips
    - Tap "Complete Sign Up" $\rightarrow$ Create Firebase Auth account, save profile document to Firestore, log user in, dismiss auth flow, and return to the action that triggered authentication
    - Tap "Skip & Finish" $\rightarrow$ Complete account setup with default settings and proceed to app

---

## Wireframes

![Our Court-3](https://hackmd.io/_uploads/rkcesm6azg.png)


# Milestone 2 - Build Sprint 1 (Unit 8)

## GitHub Project board



![Our_Court_Unit_8_Board](https://hackmd.io/_uploads/Sy4ODgrCMl.jpg)




## Progress Video

![Our Court](https://hackmd.io/_uploads/HyJd9eWCfx.gif)




- Set up Android Studio project and application structure


- Implement Home, Community, and Profile bottom navigation


- Create RecyclerView of tennis courts


- Build Court Details screen with court specifications


- Implement basic search and filters


- Create initial user profile interface

## Issue cards

![Pictures of Issues ](https://hackmd.io/_uploads/HyhLihITMe.png)





# Milestone 3 - Build Sprint 2 (Unit 9)

## GitHub Project board

![Milestone 3](https://hackmd.io/_uploads/r1mQAnUaMg.png)


## User Stories

### Daily Community Sign-Up Sheet (#20)

- As a tennis player, I want to sign up to play at a specific court so that other players can see my planned attendance and availability.

- Users can view the daily sign-up sheet and see how many players plan to attend.

### Saved Courts Feature (#21)

- As a user, I want to save tennis courts to a personal list so that I can easily find and revisit my preferred locations.

- Users can add or remove courts from their Saved Courts list.

### Hit List and Court History (#22)

- As a tennis player, I want to track the courts I have played at so that I can maintain a record of my tennis experiences.

- The Hit List displays unique courts visited, while Court History records individual visits, including repeat visits.

### Court Rating and Review System (#23)

- As a user, I want to rate and review tennis courts so that I can share my experiences and help other players choose suitable courts.

- Users can submit star ratings and written reviews.

### Community Posts and Discussions (#24)

- As a tennis player, I want to create community posts so that I can connect with other players, share announcements, and find hitting partners.

- Users can view community posts and create their own.

### Persistent Application Data (#25)

- As a user, I want my profile information, saved courts, and court activity to be stored so that my information remains available when I reopen the app.

- Application screens retrieve and display previously saved information.

- Application Testing and Final Demo (#26)

- As a user, I want the application to function correctly so that I can navigate between screens and use its features without errors.

- The development team tests major user flows, resolves bugs, and records the final demonstration.




## Completed User Stories 

## User Story Completion — Build Sprint 1

### Application Setup and Navigation

- [x] **Android Studio Project Setup**

  Set up the Android Studio project and establish the initial application structure.

- [x] **Home and Community Navigation**

  As a user, I want to navigate between the Home and Community screens so that I can easily access different application features.

- [x] **Profile Navigation**

  As a user, I want to navigate to my Profile screen so that I can access my personal information and court activity.

### Tennis Court Discovery

- [x] **Court Search Interface**

  As a user, I want to access a search bar so that I can search for tennis courts.

- [x] **Court Filter Interface**

  As a user, I want to select filters for distance, star rating, access type, and open/closed status so that I can customize my court search.

- [ ] **Tennis Court RecyclerView**

  As a user, I want to view a list of nearby tennis courts so that I can discover places to play.

- [ ] **Functional Search and Filters**

  As a user, I want to search and filter tennis court results based on my preferences.

- [ ] **Court Details**

  As a user, I want to view court specifications, including surface type, lighting, operating hours, and facility information.

### Community Posts and Discussions (#24)

- [x] **View Community Posts**

  As a tennis player, I want to view community posts so that I can stay informed about local tennis activities.

- [x] **Create General Announcements**

  As a tennis player, I want to create general announcements so that I can communicate with other players.

- [x] **Create Hitting Partner Requests**

  As a tennis player, I want to create hitting partner requests so that I can find other players to play with.

- [x] **Hitting Partner Preferences**

  As a tennis player, I want to select my preferred court, playing date, time, and skill level when creating a hitting partner request.

- [x] **Community Post Categories**

  As a user, I want to switch between All Posts and Looking for Hitting Partner so that I can find relevant community posts.

- [x] **Publish Community Posts**

  As a user, I want my published posts to appear in the community feed so that other players can view them.

### User Profile and Persistent Data

- [x] **User Profile Interface**

  As a user, I want to create and customize my personal profile so that other players can learn about my tennis experience and skill level.

- [ ] **Persistent Application Data**

  As a user, I want my profile information and application activity to be saved so that my information remains available when I reopen the app.
  
  
## Sprint 1 Issue Completion

### Completed Issues

- [x] #14 — Set up Android Studio project and application structure.
- [x] #15 — Implement Home, Community, and Profile bottom navigation.
- [x] #19 — Create initial user profile interface.
- [x] #32 — Create Account Edit Figma Screen.

### Issues In Progress

- [ ] #16 — Create RecyclerView of tennis courts.
- [ ] #17 — Build Court Details screen with court specifications.
- [ ] #18 — Implement basic search and filters.
- [ ] #33 — Implement Global Theme, Color Tokens, and Main Navigation Shell.