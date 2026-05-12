# MagnetarOrpheus: Premium Chromatic Instrument Tuner

## 🎼 Vision
Magnetar Orpheus is a professional-grade dark-mode chromatic instrument tuner for Android. Designed for musicians who demand precision, it combines high-performance Digital Signal Processing (DSP) with a premium "cockpit" interface that feels like a high-end audio instrument rather than a casual mobile app.

## 🚀 Key Features
- **Extreme Precision:** Sub-cent tuning accuracy using the YIN Pitch Detection algorithm.
- **Premium Adaptive UI:** Stunning graphite/neon-green aesthetics with optimized layouts for both Phones (Portrait) and Tablets (Landscape).
- **International Support:** Toggle between Scientific, Syllabic (La, Si, Do...), and German note naming systems.
- **Instrument Profiles:** Predefined tuning sets for Guitar, Bass, and Ukulele.
- **Real-time Analytics:** Integrated waveform visualization, pitch stability graphs, and tuning history.
- **Calibration:** Configurable A4 reference pitch (430Hz - 450Hz).

## 🛠 Technical Highlights
- **Architecture:** Clean Architecture with Unidirectional Data Flow.
- **Performance:** 60FPS UI rendering using Jetpack Compose and custom Canvas drawing.
- **Stability:** Temporal filtering to reduce visual jitter in noisy environments.
- **Quality:** Automated CI/CD with >90% code coverage enforcement via JaCoCo.

## 📱 Design Specification
The application follows the **Magnetar Orpheus Extreme UI Design Specification**, prioritizing:
- **High Contrast:** Neon green signal elements on a deep graphite background (#05080A).
- **Unambiguous UX:** Clear, large note displays and stable numeric frequency data.
- **Professional Aesthetics:** Minimalistic, technical, and high-contrast geometry.

## 🛠 Setup & Build
1. Clone the repository.
2. Open in Android Studio (Ladybug or newer).
3. Ensure you have Android SDK 34+ and JDK 17.
4. Run `./gradlew build` to verify the build and test coverage.

## 📜 License
Internal development for Magnetar Ecosystem.
