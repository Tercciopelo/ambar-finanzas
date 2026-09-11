# Ámbar Finanzas 💰

Aplicación Android nativa de finanzas personales para Ámbar.

## Tecnologías

- **Kotlin** + **Jetpack Compose**
- **Material 3** (Material You)
- **Room** (SQLite local)
- **Navigation Compose**
- **ViewModel** + **StateFlow**
- **Coroutines**
- **WorkManager** (alertas futuras)

## Arquitectura

```
MVVM + Repository

Compose → ViewModel → Repository → DAO → Room
```

## Estructura del Proyecto

```
app/src/main/java/com/ambar/finanzas/
├── AmbarApp.kt              # Application singleton
├── MainActivity.kt          # Punto de entrada
├── data/
│   ├── local/
│   │   ├── database/        # Room Database
│   │   ├── dao/             # Data Access Objects
│   │   └── entity/          # Entidades (13 tablas)
│   └── repository/          # FinanceRepository
├── ui/
│   ├── components/          # QuickAddSheet
│   ├── navigation/          # Bottom Nav + NavHost
│   ├── screens/
│   │   ├── home/            # Dashboard principal
│   │   ├── transactions/    # Historial de movimientos
│   │   ├── installments/    # Cuotas
│   │   └── settings/        # Ajustes
│   └── theme/               # Tema Ámbar (Material 3)
└── utils/                   # Formato CLP, fechas
```

## Compilar

No se necesita Android Studio. El APK se genera automáticamente en GitHub Actions.

### Desde GitHub:
1. Hacer push a `main`
2. Ir a Actions → "Build Android APK"
3. Descargar artifact `Ambar-Finanzas-APK`

## Moneda

Todos los valores monetarios se almacenan como `Long` (CLP enteros).
Formato: `$100.000` (es_CL)

## Privacidad

- ✅ 100% offline
- ✅ Sin servidores
- ✅ Sin analytics
- ✅ Sin publicidad
- ✅ Datos solo en el teléfono (Room/SQLite)
