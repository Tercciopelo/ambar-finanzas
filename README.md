# Ámbar Finanzas

Ámbar Finanzas es una aplicación Android de finanzas personales diseñada específicamente para ser **Simple por fuera, Robusta por dentro**. Está construida para mantener la privacidad absoluta de los datos, operando de manera 100% offline sin servicios en la nube.

## Funcionalidades
- **Registro Rápido:** Ingresa gastos e ingresos en segundos.
- **Planificación:** Visualiza qué puedes gastar hoy, tus próximos pagos y cuotas.
- **Privacidad:** Datos almacenados localmente. Modo privado para ocultar saldos y soporte para biometría.
- **Backups:** Exporta e importa tus datos en formato JSON. Exporta movimientos a CSV.
- **Gráficos y Alertas:** Dashboards atractivos con reportes categorizados y notificaciones locales.

## Privacidad y Arquitectura
- **100% Offline:** Sin Firebase, sin Supabase, sin analíticas ni trackers.
- **Arquitectura:** MVI / MVVM con Jetpack Compose y Room (SQLite).

## Cómo compilar y testear
El proyecto utiliza Gradle. Puedes compilarlo localmente:
```bash
./gradlew clean
./gradlew test
./gradlew assembleDebug
```

## GitHub Actions y APK
El repositorio está configurado con **GitHub Actions**. 
- Cada push a `main` ejecuta pruebas unitarias y genera un APK de depuración (`Ambar-Finanzas-APK`).
- Puedes descargar este artefacto directamente desde la pestaña **Actions** en GitHub.

## Release Firmada
Existe un workflow preparado `release-apk.yml`. Para generar un APK firmado de producción:
1. Configura los secretos en tu repositorio de GitHub: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
2. Lanza el workflow manualmente (workflow_dispatch) o crea un Tag `v*`.

## Cómo hacer respaldo y restaurar
- **Respaldo:** Ve a Configuración -> Exportar Respaldo. Se generará un JSON en tus documentos.
- **Restaurar:** Ve a Configuración -> Importar Respaldo y selecciona el archivo JSON. Se reemplazarán los datos.
