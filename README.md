# ZX Icon Changer

[![Build Debug & Release APK and Publish Release](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/actions/workflows/build-debug-release.yml/badge.svg)](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/actions/workflows/build-debug-release.yml)
[![Releases](https://img.shields.io/github/v/release/zuccheroxix-lab/ZX-Icon-Changer?color=00E5FF&label=Releases)](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B-brightgreen)](https://developer.android.com)

**ZX Icon Changer** adalah aplikasi Android native untuk membuat tampilan icon alternatif dan shortcut launcher kustom untuk aplikasi yang telah terpasang di perangkat Anda tanpa akses root, tanpa modifikasi APK asli, dan 100% diproses secara lokal/offline.

Dikembangkan dengan estetika futuristik **ZX Cyber** (*ZUCCHERO XANN*).

---

## 📥 Download APK

File APK resmi tersedia langsung pada bagian **Assets** di GitHub Releases:

👉 **[Buka Halaman GitHub Releases](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases)**

### Pilihan Unduhan Langsung:
1. **Debug APK (`ZX-Icon-Changer-debug.apk`)**:
   - **Ukuran File**: ~23 MB
   - **Instalasi**: Langsung dapat dipasang di seluruh perangkat Android 7.0+ tanpa setup sertifikat.
   - **Otomasi**: Dibuat dan dilampirkan otomatis ke Release Assets setiap ada push ke `main` atau trigger workflow.
2. **Release APK (`ZX-Icon-Changer-release.apk`)**:
   - **Ukuran File**: ~16 MB
   - **Instalasi**: Versi teroptimasi ukuran file dan performa runtime.

### Pola Direct Asset Download:
- **Debug APK**: `https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases/download/<TAG>/ZX-Icon-Changer-debug.apk`
- **Release APK**: `https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases/download/<TAG>/ZX-Icon-Changer-release.apk`

### Cara Download & Pasang:
1. Buka halaman **[Releases](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases)**.
2. Pilih rilis terbaru (contoh: **`ZX Icon Changer v1.0.0 (Build #...)`**).
3. Di bagian **Assets**, klik file **`ZX-Icon-Changer-debug.apk`** atau **`ZX-Icon-Changer-release.apk`**.
4. File APK asli akan langsung terdownload ke perangkat Android Anda.

---

## ⚡ Fitur Utama

- **Studio Editor Icon Interaktif**:
  - **Skala & Zoom**: Atur ukuran icon dari 0.4x hingga 2.5x.
  - **Rotasi Sudut**: Pengaturan derajat presisi 0°–360° dan tombol putar instan +90°.
  - **Posisi Bebas**: Geser horizontal (X) dan vertikal (Y).
  - **Padding Bingkai**: Atur kerapatan icon di dalam batas kartu.
  - **Bentuk Masking**: *Rounded Squircle*, *Circle*, *Square*, *Cyber Hexagon*, dan *Octagon Tech*.
  - **Warna & Efek**: Palet cyberpunk, background transparan, dan garis neon bercahaya.
  - **Live Preview Resolusi**: Tampilan nyata kanvas 256x256 pixel sebelum diterapkan.

- **Sumber Gambar Lengkap**:
  - Icon bawaan aplikasi terpasang.
  - Galeri & Foto via Android Photo Picker (`PickVisualMedia`).
  - File penyimpanan lokal via Storage Access Framework (`GetContent`).
  - Katalog preset glyph futuristik ZX (Rocket, Bolt, Terminal, Shield, Net, Gaming, dll.).

- **Nama Shortcut Kustom**:
  - Mengubah label shortcut sesuka hati tanpa menyentuh nama paket aplikasi asli (contoh: *Chrome* $\rightarrow$ *ZX Browser*).

- **Multi-Shortcut untuk Satu Aplikasi**:
  - Satu aplikasi target dapat memiliki beberapa shortcut launcher berbeda (contoh: *WhatsApp Personal*, *WhatsApp Work*, dll.).

- **Daftar Aplikasi Nyata (PackageManager)**:
  - Menampilkan seluruh aplikasi launcher yang terpasang di HP secara realtime.
  - Pencarian cepat, filter (Semua, User Apps, Sistem), dan pengurutan (A–Z, Z–A, Terakhir Diperbarui).

- **Manajemen Shortcut & Penyimpanan Lokal (Room Database)**:
  - Pengujian peluncuran target langsung dari aplikasi.
  - Sematkan ulang (*re-pin*) jika shortcut terhapus di layar utama.
  - Hapus shortcut dengan konfirmasi aman tanpa menghapus aplikasi target.

- **Cadangan Lokal Offline (Backup & Restore)**:
  - Ekspor seluruh konfigurasi dan icon bitmap ke file JSON portabel via SAF.
  - Impor dan pulihkan cadangan secara offline tanpa server eksternal.

- **Pembaruan & Download In-App**:
  - Menu khusus **DOWNLOAD APK** di aplikasi yang memantau status rilis, tanggal build, ukuran file, dan tombol unduhan langsung.

---

## ⚠️ Batasan Penting Sistem Android

> **Pemberitahuan Transparansi**:
> Icon alternatif dibuat sebagai **entry shortcut launcher resmi Android** menggunakan `ShortcutManager` dan `ShortcutInfo`. Sesuai standar keamanan sandbox Android, icon APK asli tidak dimodifikasi dan aplikasi tidak memerlukan hak akses root.

---

## 📱 Kebutuhan Sistem (Requirements)

- **Minimum SDK**: Android 7.0 (API level 24, Nougat)
- **Target SDK**: Android 16 (API level 36)
- **Rekomendasi**: Android 8.0+ untuk dukungan penuh `ShortcutManager.requestPinShortcut` pada launcher default.

---

## 🛠️ Kompilasi & Build Lokal

Instruksi kompilasi lokal menggunakan Gradle Wrapper yang tersedia:

### 1. Clone Repository
```bash
git clone https://github.com/zuccheroxix-lab/ZX-Icon-Changer.git
cd ZX-Icon-Changer
```

### 2. Beri Izin Eksekusi Gradle Wrapper
```bash
chmod +x gradlew
```

### 3. Build Debug APK
```bash
./gradlew assembleDebug
```
*Hasil APK:* `app/build/outputs/apk/debug/app-debug.apk` (Ukuran: ~23 MB)

### 4. Build Release APK
```bash
./gradlew assembleRelease
```
*Hasil APK:* `app/build/outputs/apk/release/app-release.apk` (Ukuran: ~16 MB)

### 5. Menjalankan Unit & Robolectric Tests
```bash
./gradlew test
```

---

## 🚀 Alur Kerja CI/CD (GitHub Actions)

Project ini dilengkapi workflow `.github/workflows/build-debug-release.yml`:
1. Berjalan otomatis pada `push` ke branch `main`, `workflow_dispatch`, atau pembuatan `tag` rilis.
2. Membangun **Debug APK** (`./gradlew assembleDebug`) dan **Release APK** (`./gradlew assembleRelease`).
3. Memvalidasi bahwa file APK nyata dihasilkan (> 0 bytes).
4. Menghitung checksum SHA-256 asli.
5. Mengunggah artifact ke GitHub Actions.
6. Membuat **GitHub Release** otomatis dan melampirkan file `ZX-Icon-Changer-debug.apk` dan `ZX-Icon-Changer-release.apk` langsung ke **Release Assets**.
7. Menampilkan tautan unduhan langsung di ringkasan GitHub Actions.

---

## 📤 Langkah Publish ke GitHub

Ikuti langkah-langkah berikut untuk mempublish project ini ke GitHub:

```bash
# 1. Inisialisasi git dan tambahkan remote repository
git init
git remote add origin https://github.com/zuccheroxix-lab/ZX-Icon-Changer.git

# 2. Set branch utama ke main
git branch -M main

# 3. Tambahkan seluruh file dan commit
git add .
git commit -m "feat: Automated Debug & Release APK build and publishing pipeline"

# 4. Push ke GitHub
git push -u origin main
```

Setelah push ke branch `main`, GitHub Actions akan otomatis membuat rilis dan menyediakan file APK asli di tab [Releases](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases).

---

## 📄 Lisensi

Project ini dilisensikan di bawah lisensi [MIT License](LICENSE).  
Copyright © 2026 **ZUCCHERO XANN**.
