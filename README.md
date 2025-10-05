# 📱 Dozaria — Challenge-Driven Social Media App (MVP)

**Tagline:** *Live Challenges. Real Fun.*  
**Vision:** Dozaria is a social media app where people don’t just scroll — they *do*.  
Every day, week, and festival comes with challenges that push users to explore, engage, and share.

---

## ✨ Core Features (MVP Roadmap)

### 1. Onboarding
- [x] Splash screen with logo + tagline
- [ ] Sign up (Google / Email)
- [ ] Guest Mode (view-only challenges & posts)

### 2. Interests Selection
- [ ] User selects interests (Travel, Reading, Fitness, Food, Festivals, Walking, Science)
- [ ] Challenges auto-generated based on tags

### 3. Home Feed (Challenges)
- [ ] Daily challenges (e.g., "Walk 5,000 steps today")
- [ ] Weekly challenges (e.g., "Eat out at XYZ restaurant")
- [ ] Festival challenges (e.g., "Visit Siddhivinayak Temple this Ganesh Chaturthi")
- [ ] Join button + progress tracking

### 4. Challenge Participation
- [ ] Join challenge → appears in "My Challenges"
- [ ] Proof upload:
  - Steps → Google Fit / Health API
  - Travel/Food → photo/video with location stamp
  - Festivals → geotagged photo
  - Reading/Other → photo/video + community upvotes
- [ ] Verification: automated (steps/location) + community upvotes

### 5. Discovery Feed
- [ ] Explore tab showing challenge posts
- [ ] Like ❤️, Comment 💬, Share 🔄
- [ ] Sponsored brand challenges (future)

### 6. Profile
- [ ] Avatar + Bio
- [ ] Total challenges completed
- [ ] Badges (Daily Streak, Festival Explorer, Fitness Pro)
- [ ] Posts gallery
- [ ] Points earned → redeem later (Phase 2)

### 7. Notifications
- [ ] Daily → "Your walk challenge is waiting 🚶‍♂️"
- [ ] Weekly → "Don’t miss your travel task 🏞️"
- [ ] Festival → "Ganesh Chaturthi challenge is live 🙏"

---

## 🎯 Future Phases
- [ ] Points & rewards system (redeem coupons, discounts)
- [ ] Leaderboards & global competitions
- [ ] Brand-sponsored challenges
- [ ] Advanced gamification (streaks, ranks, badges)

---

## 🛠️ Tech Stack
- **Frontend:** Kotlin + Jetpack Compose
- **Backend:** Firebase (Auth, Firestore, Storage)
- **Integrations:** Google Fit API, Geolocation API
- **Push Notifications:** Firebase Cloud Messaging
- **Architecture:** MVVM

---

## 🔥 Firebase Setup
This project uses Firebase. For security reasons, the `google-services.json` file is not included.

To run the project:
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com).
2. Add an Android app with package name `com.example.dozaria`.
3. Download the `google-services.json` file.
4. Place it inside the `app/` directory.
5. Sync Gradle and run the app.

---

## 🤝 Contributing
We welcome contributions of all kinds!  
Missing features are listed in the roadmap above — pick one, and start building 🚀

### Steps:
1. Fork the repo
2. Clone your fork
   ```bash
   git clone https://github.com/Shrikant2801/dozaria.git
Create a feature branch

bash
Copy code
git checkout -b feature/your-feature
Commit your changes

bash
Copy code
git commit -m "Added: your feature"
Push and create a Pull Request

📌 Roadmap Progress
MVP Core: 🚧 In Progress

Engagement Features: ⏳ Planned

Monetization & Rewards: 🔮 Future Phase

## 🤝 Contributing
We welcome contributions! Please read our [CONTRIBUTING.md](CONTRIBUTING.md) file before submitting any code.

---

## 📜 License
This project is proprietary. See [LICENSE](LICENSE) for details.
