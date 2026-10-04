# ZX Icon Changer

[![Build and Release Android APK](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/actions/workflows/build-apk.yml/badge.svg)](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/actions/workflows/build-apk.yml)
[![Latest Release](https://img.shields.io/github/v/release/zuccheroxix-lab/ZX-Icon-Changer?color=00E5FF&label=Release)](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases/latest)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B-brightgreen)](https://developer.android.com)

**ZX Icon Changer** adalah aplikasi Android native untuk membuat tampilan icon alternatif dan shortcut launcher kustom untuk aplikasi yang telah terpasang di perangkat Anda tanpa akses root, tanpa modifikasi APK asli, dan 100% diproses secara lokal/offline.

Dikembangkan dengan estetika futuristik **ZX Cyber** (*ZUCCHERO XANN*).

---

## 📥 Download APK

| Versi | Tipe | Tautan Unduhan |
| :--- | :--- | :--- |
| **v1.0.0 (Latest)** | Stable Release | [Download APK](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases/latest) |
| **v1.0.0 (Direct)** | Direct Asset | [ZX-Icon-Changer-v1.0.0-release.apk](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases/download/v1.0.0/ZX-Icon-Changer-v1.0.0-release.apk) |
| **All Releases** | History | [Semua Rilis GitHub](https://github.com/zuccheroxix-lab/ZX-Icon-Changer/releases) |

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
  - Fitur bawaan di menu Pengaturan untuk memeriksa rilis terbaru langsung dari GitHub API resmi.

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

### 1. Clone Repository
```bash
git clone https://github.com/zuccheroxix-lab/ZX-Icon-Changer.git
cd ZX-Icon-Changer
```

### 2. Beri Izin Eksekusi Gradle Wrapper
```bash
chmod +x gradlew
```

### 3. Build Debug APK (Untuk Pengujian)
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

## 🚀 Otomasi CI/CD (GitHub Actions)

Project ini dilengkapi workflow GitHub Actions di `.github/workflows/build-apk.yml`:
1. Otomatis berjalan saat ada `push` ke branch `main`, manual `workflow_dispatch`, atau pembuatan `tag` rilis (misal: `v1.0.0`).
2. Menjalankan pengujian `./gradlew test` dan membangun APK Debug & Release.
3. Menghitung checksum SHA-256 dan ukuran file secara otomatis.
4. Mengunggah APK ke GitHub Actions Artifact.
5. Saat tag rilis (`v*`) dipush, otomatis membuat **GitHub Release** dan melampirkan file APK.

### Konfigurasi Signing Release (GitHub Secrets)
Untuk menandatangani Release APK secara otomatis menggunakan keystore Anda sendiri di GitHub Actions, tambahkan Secrets berikut di **Settings $\rightarrow$ Secrets and variables $\rightarrow$ Actions**:

- `KEYSTORE_BASE64`: File keystore `.jks` yang telah dienkode ke Base64 (`base64 -w 0 your-keystore.jks`).
- `KEYSTORE_PASSWORD`: Kata sandi keystore.
- `KEY_ALIAS`: Alias kunci upload.
- `KEY_PASSWORD`: Kata sandi kunci.

*Catatan: Jika secrets di atas belum diisi, workflow secara cerdas menggunakan fallback signing sementara sehingga build tidak gagal.*

---

## 📤 Langkah Push ke Repository GitHub Anda

Jika Anda ingin mempublikasikan project ini ke akun GitHub Anda:

```bash
# Inisialisasi git jika belum ada
git init

# Tambahkan remote repository Anda
git remote add origin https://github.com/zuccheroxix-lab/ZX-Icon-Changer.git

# Pastikan berada di branch main
git branch -M main

# Tambahkan semua file dan commit
git add .
git commit -m "feat: Upgrade ZX Icon Changer v1.0.0 with GitHub Actions & Release pipeline"

# Push ke repository GitHub
git push -u origin main

# Untuk membuat rilis v1.0.0 otomatis:
git tag v1.0.0
git push origin v1.0.0
```

---

## 📄 Lisensi

Project ini dilisensikan di bawah lisensi [MIT License](LICENSE).
Copyright © 2026 **ZUCCHERO XANN**.
