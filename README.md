# ⚡ Rapid Quiz - Native Android Application

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-emerald?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35-blue?style=for-the-badge&logo=android)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)](LICENSE)

**Rapid Quiz Android**, 10 saniyelik refleks tabanlı modern bilgi yarışması platformunun **Native Android (Kotlin & Jetpack Compose)** mobil uygulamasıdır. 

Django REST Framework backend ve Vue 3 web frontend ile tam senkronize çalışan bu uygulama; dinamik soru akışı, milisaniye hassasiyetinde hız bonusu puanlaması, kategoriye özel arka plan müzikleri ve ses efektleriyle akıcı bir mobil oyun deneyimi sunar.

---

## 📱 Ekranlar ve Temel Özellikler

- **🎯 Dinamik Kategori Seçimi:** 
  - 9 zengin kategori (Teknoloji, Sinema, Tarih, Yazılım, Yapay Zeka, Fizik, Futbol, Aşçılık, Ülkeler) ve Yapay Zeka destekli özel quizler.
  - Kategoriye özel dinamik renk paletleri, ikonlar ve tema müzikleri.
- **⏱ 10 Saniyelik Refleks Zamanlayıcı (`TimerBar`):**
  - Yeşil ➔ Sarı ➔ Kırmızı renk geçişli animasyonlu süre çubuğu.
  - Süre bittiğinde otomatik sonraki soruya geçiş, milisaniyelik yanıt süresi kaydı (`time_taken`).
- **🎵 Ses & Müzik Motoru (`SoundManager`):**
  - Android Media3 (ExoPlayer) ile uzaktaki sunucudan kesintisiz müzik akışı (stream).
  - SoundPool ile düşük gecikmeli (low-latency) doğru/yanlış/tıklama ses efektleri.
- **🏆 Sonuç & Lider Tablosu (`ResultScreen`):**
  - Toplam puan, doğru/yanlış/boş sayıları, hız bonusu dökümü.
  - Soru inceleme (Answer Review) sekmesiyle hangi soruya ne yanıt verildiğini analiz etme.
  - Global liderlik tablosu (sıralama, oyuncu adı, puan, tam tarih).
- **🛡 Google Play & Production Readiness:**
  - Tam ProGuard / R8 kural yapılandırması (`rules.keep`, `proguard-rules.pro`).
  - Adaptive App Icon (vektörel foreground/background).
  - Detaylı KVKK / Privacy Policy dokümantasyonu (`PRIVACY_POLICY.md`).

---

## 🏗 Mimari ve Teknoloji Yığını

| Alan | Teknoloji / Kütüphane | Açıklama |
|---|---|---|
| **Dil** | Kotlin 2.0+ | Modern, tip güvenli ve performanslı |
| **UI Toolkit** | Jetpack Compose & Material 3 | Bildirime dayalı (declarative) modern arayüz |
| **Mimari** | MVVM + Clean Architecture | Repository pattern, Unidirectional Data Flow (UDF) |
| **State Yönetimi** | Kotlin Coroutines & `StateFlow` | Reaktif ve yaşam döngüsüne duyarlı state akışı |
| **Ağ (Networking)** | Retrofit 2 + OkHttp 3 + Gson | REST API istemcisi, cold-start için 45sn timeout |
| **Multimedya** | AndroidX Media3 (ExoPlayer) | Arka plan müzik akışı ve ses kontrolü |
| **Görsel & İkonlar** | Compose Extended Material Icons | Zengin ikon kütüphanesi |

---

## 🌐 API Entegrasyonu

Uygulama, canlı Django REST Backend servisiyle haberleşir:
* **Canlı API URL:** `https://rapid-quiz-backend.onrender.com/api/v1/`
* **Yerel Geliştirme URL:** `http://10.0.2.2:8000/api/v1/` *(Android Emülatör için)*

---

## 🛠 Kurulum ve Derleme (Build & Run)

### Gereksinimler
- Android Studio Ladybug / Koala veya daha yenisi
- JDK 17+
- Android SDK (compileSdk 35, minSdk 26)

### Komut Satırından APK Derleme
Projeyi klonlayıp debug APK üretmek için:

```bash
# Projeyi klonlayın
git clone https://github.com/bisraunal/RapidQuizAndroid.git
cd RapidQuizAndroid

# Debug APK derleyin
./gradlew assembleDebug
```

Derlenen APK dosyası şu dizinde hazır olacaktır:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 👩‍💻 Geliştirici

**Büşra Ünal**
* GitHub: [@bisraunal](https://github.com/bisraunal)
* Rapid Quiz Backend: [RapidQuizBackend](https://github.com/bisraunal/RapidQuizBackend)
* Rapid Quiz Web Frontend: [RapidQuizFrontend](https://github.com/bisraunal/RapidQuizFrontend)
