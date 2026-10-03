# Kebijakan Privasi Majopay Gateway

Terakhir diperbarui: 3 Oktober 2026

Kebijakan ini menjelaskan data apa yang diakses oleh aplikasi Android **Majopay Gateway** (package `id.majopay.ngateway`), untuk apa data itu dipakai, ke mana data dikirim, dan hak kamu atas data tersebut. Dengan memasang dan memakai aplikasi ini, kamu menyetujui kebijakan ini.

Majopay Gateway adalah alat pemantau notifikasi. Aplikasi mendeteksi notifikasi dari aplikasi yang kamu pilih untuk dipantau, mencocokkannya dengan aturan yang kamu buat, lalu meneruskan pesan yang cocok ke aplikasi atau endpoint milikmu secara otomatis melalui relay Majopay.

## 1. Data yang diakses

| Data | Sumber | Dipakai untuk |
|---|---|---|
| Nama aplikasi, judul, dan isi teks notifikasi dari aplikasi lain | Akses Notifikasi (Notification Listener) | Mendeteksi notifikasi dari aplikasi yang kamu pantau sesuai aturan |
| Daftar aplikasi yang bisa diluncurkan di perangkat | Package visibility (`<queries>`) | Menampilkan pilihan aplikasi saat kamu membuat aturan notifikasi |
| API Key dan API Secret Majopay | Diketik oleh kamu | Mengautentikasi pengiriman ke relay dan menentukan endpoint tujuan milikmu |

Aplikasi **tidak** mengakses SMS, kontak, lokasi, kamera, mikrofon, file, atau riwayat panggilan.

## 2. Data yang dikirim ke server

Hanya notifikasi yang **cocok dengan aturan yang kamu buat** yang dikirim keluar perangkat. Aplikasi mengirimnya ke relay `api-proxy.majopay.id`, yang meneruskannya ke aplikasi atau endpoint yang kamu daftarkan di dashboard Majopay. Untuk setiap pesan yang cocok, aplikasi mengirim:

- nama paket dan nama aplikasi pengirim notifikasi,
- judul dan teks notifikasi,
- waktu pesan diterima,
- API Key kamu sebagai bagian dari alamat endpoint dan API Secret sebagai header autentikasi.

Pengiriman selalu memakai koneksi terenkripsi HTTPS. Pesan yang **tidak** cocok aturan tidak pernah meninggalkan perangkat.

## 3. Data yang disimpan di perangkat

- **Riwayat**: semua notifikasi yang diproses, termasuk yang tidak cocok aturan, dicatat di database lokal aplikasi supaya kamu bisa memeriksa kenapa suatu pesan diteruskan atau tidak. Data ini hanya ada di perangkat dan tidak ikut cloud backup Android.
- **Aturan**: pola pencocokan yang kamu buat.
- **Kredensial API**: disimpan terenkripsi menggunakan Android Keystore dan tidak ikut backup maupun transfer perangkat.

Kamu bisa menghapus riwayat, aturan, dan kredensial kapan saja dari dalam aplikasi. Menghapus aplikasi (uninstall) menghapus seluruh data lokal.

## 4. Pembagian data ke pihak ketiga

Data dikirim ke relay Majopay lalu diteruskan ke aplikasi atau endpoint milikmu sendiri. Kami tidak menjual, menyewakan, atau membagikan data kepada pihak ketiga lain, kecuali diwajibkan oleh hukum yang berlaku.

Relay berjalan di infrastruktur **Cloudflare** (Workers dan D1) sebagai pemroses data atas nama Majopay. Cloudflare tidak memakai data ini untuk tujuannya sendiri.

Aplikasi tidak memakai SDK analitik, iklan, atau pelacakan pihak ketiga.

## 5. Relay api-proxy.majopay.id

Relay adalah Cloudflare Worker yang menerima pesan dari aplikasi, mencari endpoint tujuan berdasarkan API Key kamu di database D1, lalu meneruskan pesan ke endpoint tersebut. Relay hanya menyimpan pemetaan API Key ke endpoint tujuan. **Isi pesan tidak pernah disimpan di relay**: pesan diteruskan langsung dan tidak ditulis ke database.

Setelah diteruskan, data berada di aplikasi atau endpoint milikmu dan tunduk pada kebijakan kamu sendiri.

## 6. Izin yang diminta dan cara mencabutnya

| Izin | Alasan | Cara mencabut |
|---|---|---|
| Akses Notifikasi | Mendeteksi notifikasi dari aplikasi yang dipantau | Setelan Android > Notifikasi > Akses notifikasi perangkat & aplikasi |
| Internet | Mengirim pesan yang cocok ke relay Majopay | Tidak bisa dicabut, tetapi tanpa kredensial tidak ada data yang dikirim |

Sebelum membuka layar akses notifikasi, aplikasi menampilkan penjelasan dan meminta persetujuanmu terlebih dahulu.

## 7. Keamanan

- Semua komunikasi ke server memakai HTTPS.
- Kredensial API disimpan terenkripsi dengan Android Keystore.
- Database lokal dikecualikan dari cloud backup Android.
- Log jaringan detail hanya aktif pada build pengembangan, tidak pada versi yang dipublikasikan.

## 8. Anak-anak

Aplikasi ini ditujukan untuk pengguna dewasa dan tidak dirancang untuk pengguna di bawah 18 tahun.

## 9. Perubahan kebijakan

Perubahan kebijakan akan diumumkan melalui pembaruan halaman ini dan, bila material, melalui pembaruan aplikasi.

## 10. Kontak

Pertanyaan atau permintaan terkait data dapat dikirim ke:

- Email: privacy@majopay.id
- Alamat: [isi alamat resmi PT / badan usaha Majopay]
