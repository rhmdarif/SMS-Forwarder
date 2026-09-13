# Checklist Publikasi Google Play — Majopay Gateway

Dokumen ini merangkum apa yang sudah dipenuhi di kode dan apa yang masih harus diisi manual di Play Console. Urutkan pekerjaan dari atas ke bawah.

## A. Sudah dipenuhi di kode (per 13 Sep 2026)

| Ketentuan Play | Status | Di mana |
|---|---|---|
| Target API 36 (wajib sejak 31 Agu 2026) | ✅ | `app/build.gradle.kts` (`compileSdk`/`targetSdk` = 36) |
| Tidak memakai `QUERY_ALL_PACKAGES` | ✅ | Dihapus dari manifest; app picker memakai `<queries>` |
| Tidak memakai `PACKAGE_USAGE_STATS` | ✅ | Dihapus; `AppRepository` tidak lagi memakai `UsageStatsManager` |
| Foreground service bertipe yang tidak dibatasi 6 jam | ✅ | `specialUse` + `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` di manifest |
| Prominent disclosure sebelum izin SMS & akses notifikasi | ✅ | `DataDisclosureDialog` dipanggil dari `SettingsScreen` |
| Tautan kebijakan privasi di dalam aplikasi | ✅ | Pengaturan > Tentang aplikasi; URL dari `PRIVACY_POLICY_URL` |
| Isi SMS/notifikasi tidak ikut cloud backup | ✅ | `backup_rules.xml` & `data_extraction_rules.xml` mengecualikan `database` |
| Log HTTP body hanya di build debug | ✅ | `NetworkModule` (`Level.NONE` di release) |
| Boot receiver hanya untuk paket sendiri | ✅ | `PACKAGE_REPLACED` (paket lain) dihapus dari filter |

## B. Wajib dilakukan sebelum submit

### B1. Hosting kebijakan privasi
1. Publikasikan isi `docs/play-store/privacy-policy.md` di URL publik yang stabil (bukan PDF, bukan halaman yang butuh login). Contoh: `https://majopay.id/gateway/privacy`.
2. Isi email dan alamat resmi di bagian Kontak sebelum dipublikasikan.
3. Set `PRIVACY_POLICY_URL` di `local.properties` (atau env CI) ke URL yang sama, lalu build ulang.
4. Isi URL yang sama di Play Console > **App content > Privacy policy**.

### B2. Permissions Declaration Form untuk SMS
Lokasi: Play Console > **App content > Sensitive app permissions > SMS and Call Log**.

Positioning aplikasi: **alat pemantau notifikasi** dari aplikasi yang dipilih pengguna. Jangan menyebut transaksi keuangan, bank, e-wallet, atau QRIS di formulir maupun listing.

Kategori yang paling mendekati untuk positioning ini adalah **"Task automation"** (aplikasi yang mengotomatiskan tindakan berulang berdasarkan pemicu, termasuk SMS). Kategori lain tidak cocok: bukan default SMS handler, bukan backup/restore, bukan sinkronisasi antar perangkat.

Contoh justifikasi:

> Majopay Gateway adalah alat pemantau notifikasi. Pengguna memilih aplikasi atau pengirim yang ingin dipantau dan membuat aturan pencocokan (kata kunci atau regex). Saat notifikasi atau SMS yang cocok terdeteksi, aplikasi meneruskannya secara otomatis ke aplikasi/endpoint milik pengguna melalui relay api-proxy.majopay.id. Fungsi inti aplikasi adalah deteksi dan penerusan otomatis ini; tanpa RECEIVE_SMS dan READ_SMS, sumber SMS tidak dapat dipantau karena tidak ada API alternatif untuk membaca SMS masuk secara real-time. Pesan yang tidak cocok aturan tidak pernah dikirim keluar perangkat. Sebelum meminta izin, aplikasi menampilkan penjelasan data dan meminta persetujuan pengguna. Pengguna dapat mencabut izin kapan saja dan aplikasi tetap berfungsi untuk sumber notifikasi.

Video demo wajib menunjukkan, berurutan:
1. Dialog disclosure muncul saat menekan "Izinkan" pada Izin SMS.
2. Dialog izin sistem muncul setelah pengguna menekan "Setuju & lanjut".
3. Aturan SMS dibuat, SMS yang cocok diterima, lalu muncul di tab Riwayat dengan status terkirim.

**Peringatan risiko.** "Pemantauan notifikasi" bukan salah satu pengecualian resmi izin SMS. Kategori Task automation pun mensyaratkan otomatisasi menjadi tujuan utama aplikasi dan reviewer menilai ketat. Peluang ditolak nyata. Rencana cadangan jika ditolak: hapus `RECEIVE_SMS` dan `READ_SMS` dari manifest beserta `SmsReceiver`, lalu terbitkan versi **notifikasi saja**. Akses Notifikasi tidak memerlukan Permissions Declaration Form, hanya disclosure dan kebijakan privasi, yang sudah ada.

### B3. Deklarasi Foreground Service
Lokasi: Play Console > **App content > Foreground service permissions**.

- Tipe: `specialUse`.
- Justifikasi: sama seperti B2, tambahkan bahwa layanan berjalan agar pemantauan notifikasi/SMS tetap aktif dan pemrosesan tidak dimatikan sistem saat aplikasi di latar belakang. Jelaskan kenapa tipe lain tidak cocok: `dataSync` dibatasi 6 jam per hari di Android 15+ dan tidak boleh dimulai dari BOOT_COMPLETED, sedangkan `remoteMessaging` khusus transfer pesan antar perangkat.
- Video: tunjukkan notifikasi "Majopay Gateway Active" muncul dan pesan tetap diproses saat aplikasi tidak dibuka.

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
| Messages | SMS or MMS | Ya | Tidak | Wajib untuk fitur SMS | App functionality |
| Messages | Other in-app messages (isi notifikasi aplikasi lain) | Ya | Tidak | Wajib untuk fitur notifikasi | App functionality |
| Personal info | Other info (nomor pengirim SMS) | Ya | Tidak | Wajib | App functionality |
| App activity | Installed apps | Ya (hanya di perangkat) | Tidak | Opsional | App functionality |
| App info and performance | Diagnostics (riwayat lokal) | Tidak dikirim | Tidak | - | - |

"Dibagikan" dijawab **Tidak** dengan dua dasar yang diakui Play: (1) relay api-proxy.majopay.id berjalan di Cloudflare Workers + D1 yang berstatus *service provider* (pemroses data atas nama Majopay), dan (2) tujuan akhir adalah aplikasi/endpoint yang didaftarkan sendiri oleh pengguna, yaitu transfer atas tindakan pengguna yang memang mengharapkannya. Tetap tulis di kebijakan privasi bahwa data melewati Cloudflare.

Data "ephemeral"? Play mendefinisikan ephemeral sebagai data yang hanya ada di memori dan tidak disimpan lebih lama dari yang dibutuhkan untuk memproses permintaan. Relay memenuhi itu (isi pesan tidak pernah ditulis ke D1), tetapi aplikasi sendiri menyimpan pesan di riwayat lokal. Karena riwayat lokal hanya ada di perangkat dan tidak dikirim ke Majopay, jawab **Ya (ephemeral)** untuk SMS dan isi notifikasi, dan jelaskan di kebijakan privasi bahwa penyimpanan terjadi di perangkat serta di endpoint milik pengguna.

### B5. Listing dan kategori
- Kategori: **Tools** atau **Productivity**. Hindari Finance.
- Deskripsi harus menyebut secara eksplisit bahwa aplikasi membaca notifikasi (dan SMS, jika diizinkan) dari sumber yang dipilih pengguna dan meneruskan yang cocok aturan ke aplikasi/endpoint milik pengguna lewat relay Majopay. Jangan menyebut bank, e-wallet, pembayaran, atau QRIS.
- Screenshot wajib menampilkan dialog disclosure dan layar Aturan.
- Rating konten: isi kuesioner IARC, hasilnya biasanya "Everyone".
- Target audience: 18+.

### B6. Akun dan penandatanganan
- Gunakan Play App Signing; upload **AAB** (`./gradlew bundleRelease`), bukan APK.
- Naikkan `versionCode` di `app/build.gradle.kts` setiap upload.

## C. Hal yang masih perlu diputuskan (di luar kode)

1. **Pengujian tertutup 12 penguji** berlaku untuk akun developer perorangan baru. Jika akun developer Majopay adalah akun organisasi, syarat ini tidak berlaku.
2. **Konsistensi positioning**: nama developer, website, dan halaman kebijakan privasi yang tertaut akan dilihat reviewer. Jika website Majopay jelas-jelas layanan pembayaran, reviewer bisa menyimpulkan aplikasi ini bagian dari layanan keuangan meski listing tidak menyebutnya. Siapkan jawaban jika ditanya.
3. Jika Permissions Declaration Form SMS ditolak, jalankan rencana cadangan di B2 (versi notifikasi saja). Ini tidak butuh formulir deklarasi apa pun.

## D. Perubahan teknis yang menyertai targetSdk 36 dan perlu diuji di perangkat

- Android 15+: layanan `specialUse` tidak dibatasi waktu, tetapi tetap butuh `FOREGROUND_SERVICE_SPECIAL_USE`. Sudah ada.
- Android 15+: memulai foreground service dari `BOOT_COMPLETED` lewat WorkManager harus terjadi dalam jendela singkat setelah boot. Uji reboot perangkat dan cek notifikasi "Majopay Gateway Active" muncul.
- Android 16: predictive back aktif secara default. Uji gesture back di setiap layar.
- Edge-to-edge sudah diaktifkan lewat `enableEdgeToEdge()`, tidak ada perubahan.
