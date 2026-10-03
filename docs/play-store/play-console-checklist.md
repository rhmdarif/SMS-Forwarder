# Checklist Publikasi Google Play — Majopay Gateway

Dokumen ini merangkum apa yang sudah dipenuhi di kode dan apa yang masih harus diisi manual di Play Console. Urutkan pekerjaan dari atas ke bawah.

## Riwayat penolakan

**3 Okt 2026, versi dengan izin SMS ditolak.** Temuan: *Policy Declaration for SMS/Call Log*. Izin yang diminta tidak sesuai fungsi inti, fungsi inti tidak bisa diverifikasi, dan formulir mencantumkan kemampuan default SMS handler yang tidak dimiliki aplikasi.

Tindakan: fitur SMS dihapus total (rencana cadangan yang dulu ada di B2). Ikut dihapus juga foreground service, boot receiver, dan WorkManager karena ketiganya hanya melayani SMS. Pemantauan notifikasi berjalan lewat `NotificationListenerService`, yang di-bind sistem dan otomatis tersambung lagi setelah reboot. Langkah pengajuan ulang ada di bagian B0.

## A. Sudah dipenuhi di kode (per 3 Okt 2026)

| Ketentuan Play | Status | Di mana |
|---|---|---|
| Target API 36 (wajib sejak 31 Agu 2026) | ✅ | `app/build.gradle.kts` (`compileSdk`/`targetSdk` = 36) |
| Tidak memakai `QUERY_ALL_PACKAGES` | ✅ | Dihapus dari manifest; app picker memakai `<queries>` |
| Tidak memakai `PACKAGE_USAGE_STATS` | ✅ | Dihapus; `AppRepository` tidak lagi memakai `UsageStatsManager` |
| Tidak memakai izin SMS (`RECEIVE_SMS`, `READ_SMS`) | ✅ | Dihapus beserta `SmsReceiver`; tidak perlu Permissions Declaration Form |
| Tidak memakai foreground service | ✅ | `SmsForwardingService`, `BootReceiver`, `SmsMonitoringWorker` dihapus; tidak perlu deklarasi FGS |
| Prominent disclosure sebelum akses notifikasi | ✅ | `DataDisclosureDialog` dipanggil dari `SettingsScreen` |
| Tautan kebijakan privasi di dalam aplikasi | ✅ | Pengaturan > Tentang aplikasi; URL dari `PRIVACY_POLICY_URL` |
| Isi notifikasi tidak ikut cloud backup | ✅ | `backup_rules.xml` & `data_extraction_rules.xml` mengecualikan `database` |
| Log HTTP body hanya di build debug | ✅ | `NetworkModule` (`Level.NONE` di release) |

## B. Wajib dilakukan sebelum submit

### B0. Pengajuan ulang setelah penolakan SMS
1. Naikkan `versionCode` lalu `./gradlew bundleRelease`. Pastikan manifest hasil merge tidak berisi `SMS`, `FOREGROUND_SERVICE`, atau `RECEIVE_BOOT_COMPLETED`: buka AAB di Android Studio (Build > Analyze APK) lalu lihat `AndroidManifest.xml`.
2. Play Console > **Test and release**: di **setiap** track (internal, closed, open, production), ganti bundle lama yang masih meminta izin SMS dengan bundle baru, atau hentikan rilis track tersebut. Selama masih ada satu bundle aktif yang meminta SMS, Play tetap menagih formulir deklarasi SMS dan penolakan akan terulang.
3. **App content > Sensitive app permissions**: setelah tidak ada bundle aktif yang meminta SMS, bagian SMS and Call Log tidak lagi wajib. Jika formulir lama masih terbuka, ubah jawabannya menjadi aplikasi tidak memakai izin tersebut.
4. **App content > Foreground service permissions**: bagian ini tidak lagi wajib karena aplikasi tidak memakai FGS.
5. Perbarui **Data safety** sesuai tabel B4 (hapus SMS dan nomor pengirim).
6. Perbarui deskripsi listing sesuai B5, tanpa menyebut SMS.
7. Kirim untuk ditinjau dari **Publishing overview**. Tidak perlu mengajukan banding.

### B1. Hosting kebijakan privasi
1. Publikasikan isi `docs/play-store/privacy-policy.md` di URL publik yang stabil (bukan PDF, bukan halaman yang butuh login). Contoh: `https://majopay.id/gateway/privacy`.
2. Isi email dan alamat resmi di bagian Kontak sebelum dipublikasikan.
3. Set `PRIVACY_POLICY_URL` di `local.properties` (atau env CI) ke URL yang sama, lalu build ulang.
4. Isi URL yang sama di Play Console > **App content > Privacy policy**.

### B2. Permissions Declaration Form
Tidak diperlukan. Aplikasi tidak meminta izin terbatas apa pun (SMS, Call Log, `QUERY_ALL_PACKAGES`, `PACKAGE_USAGE_STATS`). Akses Notifikasi cukup dengan prominent disclosure dan kebijakan privasi, dan keduanya sudah ada.

Positioning aplikasi tetap **alat pemantau notifikasi** dari aplikasi yang dipilih pengguna. Jangan menyebut transaksi keuangan, bank, e-wallet, atau QRIS di listing.

### B3. Deklarasi Foreground Service
Tidak diperlukan. Aplikasi tidak lagi memiliki foreground service.

### B4. Data Safety form
Lokasi: Play Console > **App content > Data safety**.

Jawaban yang sesuai dengan perilaku kode saat ini:

| Pertanyaan | Jawaban |
|---|---|
| Apakah aplikasi mengumpulkan atau membagikan data pengguna? | **Ya** |
| Apakah semua data yang dikumpulkan dienkripsi saat transit? | **Ya** (HTTPS) |
| Apakah pengguna bisa meminta penghapusan data? | **Ya** (hapus riwayat dari aplikasi; relay tidak menyimpan isi pesan, hanya pemetaan API Key yang dihapus saat akun ditutup) |

Jenis data yang dideklarasikan:

| Kategori | Jenis | Dikumpulkan | Dibagikan | Wajib/Opsional | Tujuan |
|---|---|---|---|---|---|
| Messages | Other in-app messages (isi notifikasi aplikasi lain) | Ya | Tidak | Wajib untuk fitur notifikasi | App functionality |
| App activity | Installed apps | Ya (hanya di perangkat) | Tidak | Opsional | App functionality |
| App info and performance | Diagnostics (riwayat lokal) | Tidak dikirim | Tidak | - | - |

"Dibagikan" dijawab **Tidak** dengan dua dasar yang diakui Play: (1) relay api-proxy.majopay.id berjalan di Cloudflare Workers + D1 yang berstatus *service provider* (pemroses data atas nama Majopay), dan (2) tujuan akhir adalah aplikasi/endpoint yang didaftarkan sendiri oleh pengguna, yaitu transfer atas tindakan pengguna yang memang mengharapkannya. Tetap tulis di kebijakan privasi bahwa data melewati Cloudflare.

Data "ephemeral"? Play mendefinisikan ephemeral sebagai data yang hanya ada di memori dan tidak disimpan lebih lama dari yang dibutuhkan untuk memproses permintaan. Relay memenuhi itu (isi pesan tidak pernah ditulis ke D1), tetapi aplikasi sendiri menyimpan pesan di riwayat lokal. Karena riwayat lokal hanya ada di perangkat dan tidak dikirim ke Majopay, jawab **Ya (ephemeral)** untuk isi notifikasi, dan jelaskan di kebijakan privasi bahwa penyimpanan terjadi di perangkat serta di endpoint milik pengguna.

### B5. Listing dan kategori
- Kategori: **Tools** atau **Productivity**. Hindari Finance.
- Deskripsi harus menyebut secara eksplisit bahwa aplikasi membaca notifikasi dari aplikasi yang dipilih pengguna dan meneruskan yang cocok aturan ke aplikasi/endpoint milik pengguna lewat relay Majopay. Jangan menyebut bank, e-wallet, pembayaran, atau QRIS.
- Screenshot wajib menampilkan dialog disclosure dan layar Aturan.
- Jangan menyebut SMS di judul, deskripsi singkat, deskripsi lengkap, maupun screenshot. Listing yang menyebut fitur yang tidak ada di aplikasi bisa memicu penolakan "store listing does not match".
- Rating konten: isi kuesioner IARC, hasilnya biasanya "Everyone".
- Target audience: 18+.

### B6. Akun dan penandatanganan
- Gunakan Play App Signing; upload **AAB** (`./gradlew bundleRelease`), bukan APK.
- Naikkan `versionCode` di `app/build.gradle.kts` setiap upload.

### B7. Peringatan "belum mengunggah simbol debug native"
Satu-satunya kode native di bundle adalah `libandroidx.graphics.path.so` dari dependensi Compose, bukan kode sendiri, dan file itu sudah di-strip oleh AndroidX. Peringatan ini **tidak memblokir rilis** dan boleh diabaikan.

Kalau ingin menghilangkannya, `app/build.gradle.kts` sudah memuat `ndk { debugSymbolLevel = "SYMBOL_TABLE" }` di build type release. AGP hanya menjalankannya jika NDK terpasang; tanpa NDK, langkah ini dilewati tanpa pesan. Caranya:
1. Android Studio > Settings > Languages & Frameworks > Android SDK > tab **SDK Tools** > centang **NDK (Side by side)** > OK.
2. Kalau versi NDK yang terpasang berbeda dari default AGP, tambahkan `ndkVersion = "<versi terpasang>"` di blok `android {}`.
3. `./gradlew bundleRelease`, lalu pastikan AAB berisi `BUNDLE-METADATA/com.android.tools.build.debugsymbols/`. Play membaca folder itu otomatis; tidak perlu upload manual.

Alternatif tanpa NDK: Play Console > App bundle explorer > pilih versi > tab Downloads > "Upload native debug symbols", unggah zip berisi `lib/<abi>/libandroidx.graphics.path.so` hasil ekstrak dari AAB. Secara teknis ini hanya menghilangkan peringatan, tidak menambah informasi crash karena file sudah di-strip.

## C. Hal yang masih perlu diputuskan (di luar kode)

1. **Pengujian tertutup 12 penguji** berlaku untuk akun developer perorangan baru. Jika akun developer Majopay adalah akun organisasi, syarat ini tidak berlaku.
2. **Konsistensi positioning**: nama developer, website, dan halaman kebijakan privasi yang tertaut akan dilihat reviewer. Jika website Majopay jelas-jelas layanan pembayaran, reviewer bisa menyimpulkan aplikasi ini bagian dari layanan keuangan meski listing tidak menyebutnya. Siapkan jawaban jika ditanya.

## D. Perubahan teknis yang menyertai targetSdk 36 dan perlu diuji di perangkat

- Reboot perangkat, lalu kirim notifikasi uji tanpa membuka aplikasi. Notifikasi harus tetap tercatat di Riwayat, karena `NotificationListenerService` tersambung ulang otomatis oleh sistem.
- Beberapa ROM (Xiaomi, Oppo, Vivo) mematikan listener secara agresif. Jika notifikasi berhenti terdeteksi, minta pengguna mengizinkan autostart dan menonaktifkan optimasi baterai untuk aplikasi ini.
- Android 16: predictive back aktif secara default. Uji gesture back di setiap layar.
- Edge-to-edge sudah diaktifkan lewat `enableEdgeToEdge()`, tidak ada perubahan.
