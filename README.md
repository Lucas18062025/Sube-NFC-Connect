# 💳 SUBE NFC Connect

Aplicación nativa para Android diseñada para la lectura, consulta, acreditación de cargas y análisis de tarjetas de transporte público **SUBE** (Argentina).

---

## 📱 Vista Previa de la Aplicación (App Preview)

<p align="center">
  <img src="app/src/main/res/drawable/app_preview_1785948141966.jpg" alt="Vista previa de SUBE NFC Connect" width="340"/>
</p>

---

## 🚀 Características Principales

- 📡 **Escáner y Visualizador NFC de Alta Tecnología**:
  - Componente animado interactivo `NfcScanAnimationVisual` en Jetpack Compose.
  - Simulación de campo de radiofrecuencia (RF), barrido de radar en tiempo real y flujo de estado APDU (ISO 14443-A).
  - Animación de tarjeta aproxima / tap dinámico sobre la antena NFC.

- 📊 **Gráfico de Consumo Mensual (Jetpack Compose Nativo)**:
  - Visualización interactiva mediante gráfico de líneas suaves y relleno de área `MonthlySpendingChart`.
  - Comparativa visual entre **Gastos en viajes** y **Cargas acreditadas** de los últimos 6 meses.
  - Selección táctil interactiva de meses y cálculo de ahorros con tarifa RED SUBE.

- 🔋 **Tema Dinámico Material 3 & Modo OLED Negro**:
  - **Modo OLED Negro (Ahorro Batería)**: Fondo `#000000` con píxeles completamente apagados para reducir el consumo en pantallas AMOLED/OLED.
  - Alternador dinámico accesible desde el menú superior entre modos **OLED**, **Oscuro**, **Claro** y **Sistema**.

- 📱 **Soporte Adaptador Puente / Modo Ligero (J7 & Teléfonos Antiguos)**:
  - Compatibilidad completa con hardware NFC nativo y modo emulador/puente para dispositivos con sensores de baja potencia.

- 🔒 **Seguridad y Persistencia Local**:
  - Almacenamiento seguro fuera de línea mediante **Room Database**.
  - Ocultación de saldos y autenticación biométrica (Huella Digital / PIN) para proteger el historial de transacciones.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose + Material Design 3
- **Arquitectura**: Clean Architecture / MVVM con ViewModel y StateFlow
- **Base de Datos**: Room Database
- **NFC**: Android NFC Adapter APIs + ISO 14443 APDU Handler Bridge
- **Gráficos y Animaciones**: Compose Canvas Drawing, Vector Paths, InfiniteTransition

---

## 👤 Autor

**Lucas Villagra**  
Cybersecurity Analyst | Ethical Hacker | SOC Analyst  
📍 San Miguel de Tucumán, Argentina

[![LinkedIn](https://img.shields.io/badge/LinkedIn-lucas--villagra--cybersecurity-0A66C2?style=flat&logo=linkedin)](https://linkedin.com/in/lucas-villagra-cybersecurity)
[![GitHub](https://img.shields.io/badge/GitHub-Lucas18062025-181717?style=flat&logo=github)](https://github.com/Lucas18062025)
[![Portfolio](https://img.shields.io/badge/Portfolio-lucas18062025.github.io-00D4FF?style=flat&logo=githubpages)](https://lucas18062025.github.io/Portafolio/)

---
