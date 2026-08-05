# 💳 SUBE NFC Connect

Aplicación nativa para Android diseñada para la gestión integral de tarjetas de transporte público SUBE (Argentina).

## 🚀 Características Principales
* **Lectura NFC Directa:** Lectura y decodificación de chips NFC/Mifare ISO 14443.
* **Sincronización Manual:** Compatibilidad con dispositivos sin lector NFC (modo emulador o móviles J-Series / gama entrada).
* **Persistencia Offline-First:** Almacenamiento local mediante **Room Database**.
* **Historial & Transacciones:** Registro interactivo de viajes en colectivo, tren y subte.
* **Diagnóstico de Hardware:** Panel de estado del sensor NFC y compatibilidad del dispositivo.

## 🏗️ Arquitectura y Tecnologías
* **Lenguaje:** Kotlin 100%
* **Interfaz de Usuario:** Jetpack Compose + Material Design 3
* **Patrón de Diseño:** MVVM (Model-View-ViewModel) + Clean Architecture
* **Base de Datos:** Room DB con KSP
* **Asincronía:** Kotlin Coroutines & Flow
