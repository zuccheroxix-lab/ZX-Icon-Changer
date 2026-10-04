# ZX Icon Changer

[![Build Debug APK & Create Release](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/actions/workflows/build-debug-release.yml/badge.svg)](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/actions/workflows/build-debug-release.yml)
[![Releases](https://img.shields.io/github/v/release/zuccheroxix-lab/ZX-Icon-Changer?color=00E5FF&label=Releases)](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B-brightgreen)](https://developer.android.com)

**ZX Icon Changer** adalah aplikasi Android native untuk membuat tampilan icon alternatif dan shortcut launcher kustom untuk aplikasi yang telah terpasang di perangkat Anda tanpa akses root, tanpa modifikasi APK asli, dan 100% diproses secara lokal/offline.

Dikembangkan dengan estetika futuristik **ZX Cyber** (*ZUCCHERO XANN*).

---

## 📥 Download Debug APK

APK tersedia di GitHub Releases.

👉 **[Download Debug APK (GitHub Releases)](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases)**

### Cara Download APK Asli:
1. Buka halaman **[Releases](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases)**.
2. Pilih rilis terbaru (contoh: **`ZX Icon Changer Debug #...`**).
3. Di bagian **Assets**, klik file **`ZX-Icon-Changer-debug.apk`**.
4. File APK asli akan langsung terdownload ke perangkat Anda.

> **Catatan Otomasi CI/CD:**  
> Setiap kali project ini di-push ke branch `main` atau dijalankan via `workflow_dispatch`, GitHub Actions otomatis menjalankan `./gradlew assembleDebug`, memvalidasi APK, membuat GitHub Release, dan melampirkan file `ZX-Icon-Changer-debug.apk` ke daftar Assets.

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

- **Pembaruan Aplikasi (Update Checker)**:
  - Terintegrasi langsung dengan GitHub Releases API resmi di menu Pengaturan.

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
*Hasil APK akan berada di:* `app/build/outputs/apk/debug/app-debug.apk`

### 4. Build Release APK
```bash
./gradlew assembleRelease
```
*Hasil APK akan berada di:* `app/build/outputs/apk/release/app-release.apk`

### 5. Menjalankan Unit & Robolectric Tests
```bash
./gradlew test
```

---

## 🚀 Alur Kerja Distribusi (GitHub Actions)

Project ini dilengkapi workflow `.github/workflows/build-debug-release.yml`:
1. Berjalan otomatis saat ada `push` ke branch `main` atau `workflow_dispatch`.
2. Menjalankan `./gradlew assembleDebug`.
3. Memvalidasi bahwa file `app/build/outputs/apk/debug/app-debug.apk` benar-benar dibuat dan memiliki ukuran valid (> 0 bytes).
4. Menyalin APK menjadi `ZX-Icon-Changer-debug.apk`.
5. Menghitung checksum SHA-256 asli.
6. Membuat GitHub Release otomatis (`ZX Icon Changer Debug #<run_number>`).
7. Melampirkan `ZX-Icon-Changer-debug.apk` ke bagian Release Assets.
8. Menampilkan URL download langsung di Workflow Summary.

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
git commit -m "feat: Automated Debug APK build and release pipeline"

# 4. Push ke GitHub
git push -u origin main
```

Setelah push ke branch `main`, GitHub Actions akan otomatis membuat rilis debug dan menyediakan file `ZX-Icon-Changer-debug.apk` di tab [Releases](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases).

---

## 📄 Lisensi

Project ini dilisensikan di bawah lisensi [MIT License](LICENSE).  
Copyright © 2026 **ZUCCHERO XANN**.
