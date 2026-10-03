# Panduan Pengguna Majopay Gateway

Versi aplikasi 1.2 · Diperbarui 3 Oktober 2026

Majopay Gateway memantau notifikasi dari aplikasi yang kamu pilih di ponsel Android kamu. Setiap pesan dicek terhadap aturan yang kamu buat. Pesan yang cocok diteruskan otomatis, lewat relay Majopay, ke aplikasi atau endpoint milikmu. Pesan yang tidak cocok tidak pernah keluar dari ponsel.

## 1. Sebelum mulai

Yang kamu butuhkan:

- Ponsel Android 10 atau lebih baru.
- API Key dan API Secret dari dashboard Majopay.
- Endpoint tujuan sudah kamu daftarkan di dashboard Majopay untuk API Key tersebut.
- Koneksi internet di ponsel.

## 2. Setup pertama kali

Saat pertama dibuka, aplikasi menampilkan layar **Hubungkan ke Majopay**.

1. Isi **API Key** (contoh bentuknya `mp_live_xxxxxxxx`). Tidak boleh mengandung spasi.
2. Isi **API Secret**. Nilai ini rahasia, jangan dibagikan.
3. Ketuk **Simpan & lanjut**.

Kalau belum punya kredensial, ketuk **Lewati dulu**. Aplikasi tetap bisa dipakai untuk membuat aturan, tetapi pesan yang cocok belum diteruskan. Banner kuning **Kredensial API belum diisi** akan muncul di tab Aturan dan Riwayat sampai kamu mengisinya lewat tombol **Isi sekarang**.

Kredensial disimpan terenkripsi di ponsel dan tidak ikut backup Android.

## 3. Memberi izin

Buka tab **Pengaturan**, bagian **Izin akses**. Ada dua baris. Ketuk **Izinkan** pada yang belum bertanda **Aktif**.

### Akses notifikasi

Wajib. Tanpa akses ini aplikasi tidak bisa membaca notifikasi apa pun.

1. Ketuk **Izinkan** pada baris Akses notifikasi.
2. Muncul dialog **Sebelum kamu mengaktifkan akses notifikasi**. Ketuk **Setuju & lanjut**.
3. Android membuka layar **Akses notifikasi perangkat & aplikasi**. Cari **Majopay Gateway**, nyalakan sakelarnya, lalu konfirmasi **Izinkan**.
4. Tekan tombol kembali untuk pulang ke aplikasi. Status baris berubah menjadi **Aktif**.

### Akses internet

Selalu aktif, tidak perlu diatur.

Akses notifikasi bisa kamu cabut kapan saja lewat Setelan Android. Setelah dicabut, notifikasi berhenti dipantau.

## 4. Membuat aturan

Aturan menentukan pesan mana yang diteruskan. Buka tab **Aturan**, lalu ketuk **Buat aturan**.

| Isian | Keterangan |
|---|---|
| Nama aturan | Nama bebas supaya mudah dikenali di daftar dan riwayat. |
| Pola yang dicari | Teks yang harus ada di pesan. Huruf besar dan kecil dianggap sama. |
| Pakai regex | Nyalakan kalau pola ditulis sebagai ekspresi reguler. |
| Filter per aplikasi | Nyalakan lalu **Pilih aplikasi** supaya aturan hanya menangkap notifikasi dari satu aplikasi. |

Tujuan pengiriman sudah dikunci di dalam aplikasi, jadi tidak ada isian URL. Ketuk **Simpan**.

### Cara pencocokan bekerja

- **Tanpa regex**: pesan cocok kalau mengandung pola di mana pun, tidak peduli huruf besar atau kecil. Pola `pesanan baru` cocok dengan "Ada PESANAN BARU dari Andi".
- **Dengan regex**: pola dicocokkan sebagai ekspresi reguler, juga tidak peduli huruf. Contoh `pesanan #\d+` cocok dengan "Pesanan #4821 sudah dibayar". Kalau regex kamu tidak valid, aplikasi otomatis memperlakukannya sebagai teks biasa.
- Yang dicocokkan adalah gabungan judul dan isi dalam bentuk `judul: isi`. Jadi pola boleh mengacu ke judul, isi, atau keduanya.

### Contoh aturan

| Nama | Pola | Regex | Filter aplikasi |
|---|---|---|---|
| Pesanan Tokoku | `pesanan baru` | Mati | Tokoku |
| Pesan pelanggan | `pesan baru` | Mati | - |
| Nomor tiket | `tiket #\d{6}` | Nyala | Helpdesk |

### Mengelola aturan

- **Sakelar** di kartu aturan menyalakan atau mematikan aturan tanpa menghapusnya.
- **Ketuk kartu** untuk mengubah aturan.
- **Tombol hapus** di kartu menghapus aturan. Riwayat pesan yang pernah cocok tidak ikut terhapus.
- Bagian atas tab menampilkan **Total aturan** dan berapa yang **Aktif**.

## 5. Membaca riwayat

Tab **Riwayat** mencatat semua pesan yang diproses, termasuk yang tidak cocok aturan. Ini tempat pertama untuk mengecek kenapa sebuah pesan diteruskan atau tidak.

### Status pesan

| Status | Arti |
|---|---|
| Berhasil diteruskan | Endpoint tujuan membalas dengan kode 2xx. |
| Gagal | Pengiriman gagal setelah tiga kali percobaan, atau endpoint membalas error. |
| Dicoba ulang | Sedang dalam proses percobaan ulang. |
| Diterima | Pesan sudah dicatat, belum selesai diproses. |
| Tanpa aturan | Pesan tidak cocok dengan aturan aktif mana pun. Tidak ada yang dikirim. |

### Mencari dan memfilter

- Kotak **Cari pesan, pengirim, atau aplikasi…** mencari di isi pesan, pengirim, dan nama aplikasi.
- Tombol **Filter riwayat** membuka pilihan **Aplikasi** dan **Pola isi pesan**. Ketuk **Terapkan**, atau **Reset filter** untuk menghapus.
- Daftar dimuat bertahap. Ketuk **Muat lebih banyak** di bagian bawah untuk pesan lama.

### Detail dan kirim ulang

Ketuk sebuah baris untuk melihat **Isi pesan**, **Tujuan**, **Payload terkirim**, dan **Respons server**. Kalau pesan cocok aturan tetapi statusnya Gagal, ketuk **Kirim ulang ke webhook**, lalu konfirmasi. Pesan dengan status Tanpa aturan tidak bisa dikirim ulang.

### Menghapus

Geser baris ke samping untuk menghapus satu catatan riwayat. Penghapusan hanya berlaku di ponsel ini.

## 6. Apa yang dikirim ke endpoint kamu

Setiap pesan yang cocok dikirim sebagai `POST` dengan body JSON ke relay Majopay. Relay mencari endpoint tujuan berdasarkan API Key kamu, lalu meneruskan pesan ke sana tanpa menyimpan isinya.

- Alamat: URL relay ditambah API Key kamu sebagai bagian akhir path.
- Header `x-app-key`: berisi API Secret kamu.
- Kalau gagal, aplikasi mencoba ulang sampai tiga kali dengan jeda 1 detik lalu 2 detik.

Payload:

```json
{
  "sourceType": "NOTIFICATION",
  "packageName": "id.tokoku.app",
  "appLabel": "Tokoku",
  "title": "Pesanan baru",
  "text": "Andi memesan 2 barang",
  "postTime": 1757740800000,
  "extras": { },
  "timestamp": 1757740801234
}
```

Field `extras` berisi data tambahan dari notifikasi bila ada, misalnya teks panjang.

## 7. Notifikasi yang sengaja dilewati

Supaya riwayat tidak penuh sampah, aplikasi tidak memproses:

- notifikasi dari sistem Android dan Google Play Services,
- notifikasi yang terus berjalan seperti pemutar musik atau unduhan,
- notifikasi yang sama persis dalam jangka 5 detik,
- notifikasi dari Majopay Gateway sendiri.

## 8. Pemantauan di latar belakang

Pemantauan berjalan lewat akses notifikasi yang dikelola Android, jadi tetap aktif walaupun aplikasi tidak dibuka dan otomatis tersambung lagi setelah ponsel dinyalakan ulang. Tidak ada notifikasi status yang menetap di bilah notifikasi.

Beberapa merek ponsel mematikan aplikasi latar belakang secara agresif. Kalau pesan sering tidak tercatat, buka Setelan Android, cari **Baterai** atau **Optimasi baterai**, lalu kecualikan Majopay Gateway. Di beberapa merek ada juga pengaturan **Autostart** yang perlu dinyalakan.

## 9. Pengaturan

- **Kredensial API**: lihat API Key, tampilkan atau sembunyikan Secret, **Ubah**, atau **Hapus**. Setelah dihapus, aplikasi kembali ke layar setup.
- **Izin akses**: status akses notifikasi dan internet, lihat bagian 3.
- **Tentang aplikasi**: versi, package, target SDK, dan tautan **Kebijakan privasi** yang terbuka di browser.
- **Tips singkat**: pengingat cara menguji notifikasi.
- **Alat debug**: tombol **Uji visibilitas aplikasi** menampilkan aplikasi mana saja yang bisa dilihat pemilih aplikasi. Berguna kalau aplikasi yang kamu cari tidak muncul saat memilih filter.

## 10. Kalau ada masalah

| Gejala | Kemungkinan penyebab | Yang bisa dicoba |
|---|---|---|
| Notifikasi tidak muncul di Riwayat | Akses notifikasi belum aktif, atau notifikasinya termasuk yang dilewati | Cek Akses notifikasi. Lihat bagian 7. |
| Status **Tanpa aturan** padahal aturannya ada | Pola tidak cocok, aturan mati, atau filter aplikasi salah | Buka detail pesan, bandingkan isi dengan pola. Pastikan sakelar aturan nyala. Ingat formatnya `judul: isi`. |
| Status **Gagal** | Kredensial salah, endpoint tujuan tidak merespons, atau tidak ada internet | Baca **Respons server** di detail. Periksa kredensial di Pengaturan. Setelah dibetulkan, **Kirim ulang ke webhook**. |
| Banner **Kredensial API belum diisi** | Kredensial kosong | Ketuk **Isi sekarang**. |
| Pesan tercatat, tapi hanya saat aplikasi dibuka | Sistem mematikan aplikasi di latar belakang | Lihat bagian 8. |
| Aplikasi tidak muncul di pemilih aplikasi | Aplikasi itu tidak punya ikon peluncur | Jalankan **Uji visibilitas aplikasi** di Pengaturan. |
| Tautan kebijakan privasi tidak terbuka | Tidak ada browser, atau URL belum aktif | Muncul pesan berisi URL. Buka URL itu manual di browser. |

## 11. Privasi singkat

- Yang dibaca: nama aplikasi, judul, dan isi notifikasi. Aplikasi tidak membaca SMS.
- Yang keluar dari ponsel: hanya pesan yang cocok aturan, lewat HTTPS ke relay Majopay, lalu ke endpoint milikmu.
- Yang disimpan di ponsel: riwayat semua pesan yang diproses, aturan, dan kredensial terenkripsi. Riwayat tidak ikut backup Android.
- Relay tidak menyimpan isi pesan.

Kebijakan privasi lengkap ada di Pengaturan > Tentang aplikasi > Kebijakan privasi.
