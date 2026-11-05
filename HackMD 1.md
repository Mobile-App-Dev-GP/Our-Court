---
title: Milestone 1 - APP_NAME_HERE (Unit 7)

---

# Milestone 1 - Our Court (Unit 7)

## Table of Contents

1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)

## Overview

### Description

Our Court helps tennis enthusiasts locate tennis courts near them. It allows users to communicate with each other to build an all-levels community. Users can share their availability to play at the courts of their choosing. Users are able to filter courts based on distance, star rating, price, and facility access. It allows users to see a broad view of how many people are going to a particular court day-of, and click for more detailed information and player sign-ups.

### App Evaluation

- **Category:** Fitness / Lifestyle / Community / Sports
- **Mobile:** Uses GPS location tracking for nearby courts, real-time map visualization, tap-to-navigate features, and push notifications for player coordination on the go.
- **Story:** Solves the common hassle tennis players face when looking for open local courts or available hitting partners. Creates a centralized local hub to connect players and gauge court crowd levels before leaving home.
- **Market:** Tennis enthusiasts of all skill levels (beginners to advanced league players), local park visitors, and tennis clubs looking to organize casual or competitive play.
- **Habit:** Checked regularly to view real-time court activity, log play availability, organize pickup matches, and find new local courts.
- **Scope:** V1 focuses on court discovery via list view with search/filters, court details, player skill profile, daily court attendance sign-up sheets, and personal court lists. V2 expands to map overlays, direct messaging, and dynamic matchmaking.

---

## Product Spec

### 1. User Features (Required and Optional)

**Required Features**

1. Tennis Court Locations (List and Search)
2. RecyclerView of the Tennis Courts
3. Filters (Per Mile, Highest Rated, Free/Paid/Membership, Open or Closed)
4. Court Rating System (Star Rating & Reviews)
5. User UTR Rating Feature / Guest Profile
6. Link to the Official Court Site
7. Community Sign-Up Sheet (per court for day-of availability)
8. General Community Page / Local Tennis Notice Board with Post Creation
9. Court Star Rating & Spec Info (Surface type, lighting, net conditions)
10. Saved Courts Feature (Personal list of courts saved for later reference)
11. Hit List (A unique, shareable list of distinct courts the user has played at; non-repetitive; filterable by date first played or alphabetically; can be toggled public or private)
12. Court History (A private visit log of all sessions attended; chronological by visit date; allows duplicate entries for multiple visits to the same court)

**Optional Features**

1. Community Channels / Group Chats (per-court stretch feature)
2. Tennis Court Locations (Interactive Map view / overlay)
3. Open or Closed scheduling (ability to check court attendance on future dates)
4. Weather Overlay (real-time temperature & rain alerts for outdoor courts)
5. Hitting Partner Matchmaker (match by skill level / UTR rating)

---

### 2. Screen Navigation & Flow

**1. Main Court List Screen (Home)**
- **Displays:** RecyclerView list of nearby tennis courts, top search bar, filter button, and optional map view toggle icon.
- **Flow:** 
  - Tap court card $\rightarrow$ Court Detail Screen.
  - Tap filter icon $\rightarrow$ Filter Options Modal.
  - Tap search bar $\rightarrow$ Query courts by name or location.
  - Tap map toggle (optional) $\rightarrow$ Map View Overlay with court pins.

**2. Court Detail Screen**
- **Displays:** Court specs (surface, lights, net condition), operating hours, aggregate star rating and reviews, "Leave a Review / Star Rating" button, Save/Bookmark icon, link to official court site, and the Daily Community Sign-Up Sheet with day-of comments.
- **Flow:** 
  - Tap "Check In to Play" $\rightarrow$ Updates user attendance count and appends entry to user's Court History (and Hit List if first visit).
  - Tap "Save Court" icon $\rightarrow$ Adds or removes court from user's Saved Courts list.
  - Tap "Leave a Review / Star Rating" $\rightarrow$ Opens Rating & Review submission modal.
  - Tap "Official Website" $\rightarrow$ Launches external browser via Intent.

**3. General Community Page**
- **Displays:** Local tennis announcement board, "Looking for Hitting Partner" posts, and a "Create Post" Floating Action Button (FAB).
- **Flow:** 
  - Tap post card $\rightarrow$ Expanded comment/thread screen.
  - Tap "Create Post" FAB $\rightarrow$ Create Post modal/screen.
  - Tap user avatar/username $\rightarrow$ Selected User's Profile Screen.

**4. User Profile Screen**
- **Displays:** User profile image, UTR/NTRP skill rating, guest/member badge, and navigation tabs/buttons for three distinct lists: **Saved Courts**, **Hit List**, and **Court History**.
- **Flow:** 
  - Tap "Edit Profile" $\rightarrow$ Modify skill level, bio, and profile details.
  - Tap "Saved Courts" $\rightarrow$ Displays list of bookmarked courts saved for later.
  - Tap "Hit List" $\rightarrow$ Displays unique list of played courts (filterable by date first played or alphabetically; includes public/private share toggle).
  - Tap "Court History" $\rightarrow$ Displays private chronological log of all past court visits (supports repeated court visits).

---

### 3. Navigation

**Tab Navigation** (Bottom Navigation Bar)

- **Home Tab** $\rightarrow$ Main Court List Screen
- **Community Tab** $\rightarrow$ General Community Page
- **Profile Tab** $\rightarrow$ User Profile Screen

**Flow Navigation** (Screen to Screen & Modals)

- **Main Court List Screen**
  - Tap a court card $\rightarrow$ Court Detail Screen
  - Tap search bar $\rightarrow$ Filter court list by keyword
  - Tap filter icon $\rightarrow$ Filter Options Modal
  - Tap map icon (optional) $\rightarrow$ Interactive Map Overlay

- **Filter Options Modal**
  - Select distance radius $\rightarrow$ Filter courts by mileage
  - Select rating $\rightarrow$ Sort courts by minimum star rating
  - Select price $\rightarrow$ Filter free, paid, or membership courts
  - Select status $\rightarrow$ Filter open or closed courts
  - Tap "Apply" $\rightarrow$ Return to filtered Court List Screen

- **Court Detail Screen**
  - Tap "Check In to Play" $\rightarrow$ Join daily sign-up sheet & log to Court History
  - Tap "Save Court" icon $\rightarrow$ Add to Saved Courts list
  - Tap "Leave a Review / Star Rating" $\rightarrow$ Open Review submission modal
  - Tap "Official Website" $\rightarrow$ Open court website in external browser

- **General Community Page**
  - Tap "Create Post" FAB $\rightarrow$ Open Create Post compose screen
  - Tap a post card $\rightarrow$ Open Post Detail & Comments View
  - Tap an author's profile $\rightarrow$ Open User Profile Screen

- **User Profile Screen**
  - Tap "Edit Profile" $\rightarrow$ Open Edit Profile form
  - Tap "Saved Courts" $\rightarrow$ Open RecyclerView of courts saved for later
  - Tap "Hit List" $\rightarrow$ Open RecyclerView of unique played courts (with sort/filter and privacy toggle)
  - Tap "Court History" $\rightarrow$ Open private chronological log of past visits

---

## Wireframes

![Our Court](https://hackmd.io/_uploads/SkqaSFmazl.png)



# Milestone 2 - Build Sprint 1 (Unit 8)

## GitHub Project board



![Our_Court_Project_Board_Under_1MB](https://hackmd.io/_uploads/rybC-h8pMg.jpg)




- Set up Android Studio project and application structure


- Implement Home, Community, and Profile bottom navigation


- Create RecyclerView of tennis courts


- Build Court Details screen with court specifications


- Implement basic search and filters


- Create initial user profile interface

## Issue cards

![Pictures of Issues ](https://hackmd.io/_uploads/HyhLihITMe.png)

- List the issues you completed this sprint



# Milestone 3 - Build Sprint 2 (Unit 9)

## GitHub Project board

![Milestone 3](https://hackmd.io/_uploads/r1mQAnUaMg.png)

## Completed user stories

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


